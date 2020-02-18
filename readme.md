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

### redis缓存一致性