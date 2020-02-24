package com.hito.seckill.common.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description 访问限制, 通过拦截器实现. {@link AccessInterceptor}
 *
 * @author HitoM
 * @date 2020/2/24 23:46
 **/
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
