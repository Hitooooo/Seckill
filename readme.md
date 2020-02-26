# 一个Springboot秒杀

> [掘金观后感](https://juejin.im/post/5aabd1956fb9a028d82b8738)文章帮助梳理。

## 一、基本的登录和分布式Session

### Login

### 分布式Session

根据Cookie实现登录状态的保存在Redis等内存数据库，所有的session获取和保存都在redis中操作。

[[分布式session的几种实现方式](https://www.cnblogs.com/daofaziran/p/10933221.html)](https://www.cnblogs.com/daofaziran/p/10933221.html)

## 二、商品列表和商品详情

Springboot静态文件的过滤

```java
    @Configuration
    public class WebMvcConfig extends WebMvcConfigurationSupport {
        
        // 过滤
        @Override
        protected void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        }
    }
```

## 三、秒杀设计

首先需要确定下单这一过程，需要哪些操作，如果没有优化的情况下，是如何实现的呢?

1. 确认库存数量可用 ```select store from good where good_id = 123```
2. 更新库存数量，就是数量减一 ```update good set store = store -1 where good_id = 123```
3. 插入订单表```insert into order```

显然这是一个事务，其中的任何一个步骤出错，就需要回滚。那这个事务可以放在Java代码中控制，也可以放在mysql的sql语句中控制。在分析这两种方式之前，先看看这个事务的过程有没有问题.第一句查询sql，产生共享锁，可以多个线程同时访问，但是第二个update操作会产生行级锁，多个线程串行执行。第二个事务，只有等前一个事务commit/rollback之后才能从第二步开始执行。**显然，第二个事务开始更新时，此时的store可能是前一个事务已经修改过的，但是第二个可能并不清楚，此时就会产生超卖现象。**那正确的方式应该是怎么样的？

* 方法1：修改第一句为 ```select ... for update```第一步就加锁，效率不高
* 方法2：直接先做update操作，然后判断最新的store数值是否大于等于0，是的话库存更新成功，否则抛出异常回滚。
* 方法3：```update good set store = store -1 where good_id = 123 and store - 1 >= 0```通过一行sql保证。

解决了超卖问题，那么这个事务的过程，是不是还可以优化呢？

![](https://user-gold-cdn.xitu.io/2018/3/16/1622f2a838b0137e?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

对于insetr操作，mysql是不会产生行级锁的，可以放在第一步做，减少锁的持有时间（inset耗时）。有了以上两个操作，现在来看放在java代码中，和sql中怎么操作。

### Java代码

通过异常机制，让spring帮我们回滚。

```java
@Override
public SeckillExecution executeSeckill(Long seckillId, Long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
    if (StringUtils.isEmpty(md5) || !md5.equals(getMD5(seckillId))) {
        throw new SeckillException(SeckillStatEnum.DATA_REWRITE.getStateInfo());
    }
    //执行秒杀逻辑:1.减库存.2.记录购买行为
    // 优化为先记录购买行为，再执行减操作
    Date nowTime = new Date();
    try {
        //记录购买行为.直接插入订单表
        int inserCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
        if (inserCount <= 0) {
            //重复秒杀rollback
            throw new RepeatKillException(SeckillStatEnum.REPEAT_KILL.getStateInfo());
        } else {
            //减库存  热点商品竞争
            int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0) {
                //rollback
                throw new SeckillCloseException(SeckillStatEnum.END.getStateInfo());
            } else {
                //秒杀成功  commit
                SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
            }

        }
    } catch (SeckillCloseException e1) {
        throw e1;
    } catch (RepeatKillException e2) {
        throw e2;
    } catch (Exception e) {
        LOG.error(e.getMessage());
        //所有的编译期异常转化为运行期异常,spring的声明式事务做rollback
        throw new SeckillException("seckill inner error: " + e.getMessage());
    }

}
```

### Mysql流程控制

事务在Java代码中控制，免不了需要多次的网络IO，如果将整个下单过程放到MySQL服务器中，可以减少多次的网络请求耗时。也算是一种优化。

```mysql


-- 秒杀执行储存过程
DELIMITER $$ -- console ; 转换为
$$
-- 定义储存过程
-- 参数： in 参数   out输出参数
-- row_count() 返回上一条修改类型sql(delete,insert,update)的影响行数
-- row_count:0:未修改数据 ; >0:表示修改的行数； <0:sql错误
CREATE PROCEDURE `seckill`.`execute_seckill`
  (IN v_seckill_id BIGINT, IN v_phone BIGINT,
   IN v_kill_time  TIMESTAMP, OUT r_result INT)
  BEGIN
    DECLARE insert_count INT DEFAULT 0;
    START TRANSACTION;
    INSERT IGNORE INTO success_killed
    (seckill_id, user_phone, create_time)
    VALUES (v_seckill_id, v_phone, v_kill_time);
    SELECT row_count()
    INTO insert_count;
    IF (insert_count = 0)
    THEN
      ROLLBACK;
      SET r_result = -1;
    ELSEIF (insert_count < 0)
      THEN
        ROLLBACK;
        SET r_result = -2;
    ELSE
      UPDATE seckill
      SET number = number - 1
      WHERE seckill_id = v_seckill_id
            AND end_time > v_kill_time
            AND start_time < v_kill_time
            AND number > 0;
      SELECT row_count()
      INTO insert_count;
      IF (insert_count = 0)
      THEN
        ROLLBACK;
        SET r_result = 0;
      ELSEIF (insert_count < 0)
        THEN
          ROLLBACK;
          SET r_result = -2;
      ELSE
        COMMIT;
        SET r_result = 1;

      END IF;
    END IF;
  END;
$$
--  储存过程定义结束
DELIMITER ;
SET @r_result = -3;
--  执行储存过程
CALL execute_seckill(1003, 13502178891, now(), @r_result);
-- 获取结果
SELECT @r_result;

```



### 具体优化

有了上面的控制保证流程的正确性，现在可以引入一些中间件或者缓存策略，减少mysql的访问数。

#### 1. 缓存

##### 页面缓存

1. 商品列表页面缓存到Redis，未查询到缓存就手动调用thymeleaf解析html
2. 商品详情页缓存

##### 对象缓存

#### 2. 快速失败

* redis
* 内存标记

#### 3. 消息队列异步处理



###   redis缓存一致性

### 超卖问题解决

1. 不同用户在读请求的时候，发现商品库存足够，然后同时发起请求，进行秒杀操作，减库存，导致库存减为负数
   
   简单办法加一个可用数量大于0的条件判断，但是不能完全避免。
   
   ```sql
   update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count >0
   ```
   
   因为数据库底层的写操作和读操作可以同时进行，虽然写操作默认带有隐式锁（即对同一数据不能同时进行写操作）但是读操作默认是不带锁的，所以当用户1去修改库存的时候，用户2依然可以读到库存为1，所以出现了超卖现象。
   
   可以通过加锁的方式解决：
   
   乐观锁：表中新增字段version，每次写入时version自增，如果version跟读取时值比较发生变化，那么说明中间发生过改动，此时应该放弃更新库存操作。
   
   悲观锁： 在select ...语句最后加上for update）这样一来用户1在进行读操作时用户2就需要排队等待 
   
2. 同一个用户在有库存的时候，连续发出多个请求，两个请求同时存在，于是生成多个订单

    将userId和商品Id 加上唯一索引，可以解决这种情况。插入失败 

## 四、安全优化

### 1. 秒杀地址隐藏

点击秒杀时，不是直接请求地址，请求秒杀地址。获取地址成功后再进行秒杀的操作。防止恶意的接口访问。

### 2. 图形验证码

1. 前端界面渲染时，获取验证码
2. 服务端对于每次生成的验证码都保存在redis中，并且设置过期时间
3. 秒杀时带上用户输入的验证码跟redis中的做校验

### 3.接口限流秒杀

固定的用户，在单位时间内访问接口次数限制。**拦截器实现。**

1. 定义限流注解

   ```java
   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.METHOD)
   public @interface AccessLimit {
       /** 几秒内 */
       int seconds();
       /** 最多允许几次访问 */
       int maxCount();
       /** 默认需要登录 */
       boolean needLogin() default true;
   }
   ```

2. 编写拦截器

   ```java
   @Component
   public class AccessInterceptor extends HandlerInterceptorAdapter {
       private final SeckillUserService userService;
       private final RedisService redisService;
   
       @Autowired
       public AccessInterceptor(SeckillUserService userService, RedisService redisService) {
           this.userService = userService;
           this.redisService = redisService;
       }
   
       @Override
       public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
           if (handler instanceof HandlerMethod) {
               // 判断方法
               HandlerMethod hm = (HandlerMethod) handler;
               SeckillUser miaoshaUser = getUser(request, response);
               UserContext.setUser(miaoshaUser);
               AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
               // 方法没有注解，直接放行
               if (Objects.isNull(accessLimit)) {
                   return true;
               }
               int maxCount = accessLimit.maxCount();
               boolean needLogin = accessLimit.needLogin();
               int seconds = accessLimit.seconds();
               String key = request.getRequestURI();
               if (needLogin) {
                   if (Objects.isNull(miaoshaUser)) {
                       render(response, CodeMsg.SERVER_ERROR);
                       return false;
                   }
                   key += "_" + miaoshaUser.getId();
               }
               AccessKey accessKey = AccessKey.withExpireSeconds(seconds);
               Integer currentCount = redisService.get(accessKey, key, Integer.class);
               if (null == currentCount) {
                   redisService.set(accessKey, key, 1);
               } else if (currentCount < maxCount) {
                   redisService.incr(accessKey, key);
               } else {
                   render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                   return false;
               }
           }
           return true;
       }
   }
   ```

3. WebConfig中配置拦截器

   ```java
   @Configuration
   public class WebMvcConfig extends WebMvcConfigurationSupport {
       private final AccessInterceptor accessInterceptor;
       
       @Override
       protected void addInterceptors(InterceptorRegistry registry) {
           registry.addInterceptor(accessInterceptor);
       }
       // 过滤静态文件的拦截
       @Override
       protected void addResourceHandlers(ResourceHandlerRegistry registry) {      	registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
       }
   }
   ```

   

   

   