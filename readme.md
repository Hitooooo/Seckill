# 一个Springboot秒杀

## 一、基本的登录和分布式Session

### Login

### 根据Cookie实现登录状态的保存

## 二、商品列表和商品详情

### Springboot静态文件的过滤

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

### 1. 缓存

#### 页面缓存

1. 商品列表页面缓存到Redis，未查询到缓存就手动调用thymeleaf解析html
2. 商品详情页缓存

#### 对象缓存

### 2. 快速失败

* redis
* 内存标记

### 3. 消息队列异步处理



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

      

   

   