# 一个Springboot秒杀

## Part2

### Login

### 根据Cookie实现登录状态的保存


## 商品列表和商品详情

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