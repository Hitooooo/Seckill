package com.hito.seckill.config.web;

import com.hito.seckill.common.access.UserContext;
import com.hito.seckill.domain.SeckillUser;
import com.hito.seckill.service.SeckillUserService;
import com.sun.deploy.net.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 实现此方法, 将自动对方法入参包含 {@link SeckillUser} 的对象的进行注入
 *
 * @author HitoM
 * @date 2020/2/16 20:13
 **/
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class <?> parameterType = parameter.getParameterType();
        return parameterType == SeckillUser.class;
    }

    /**
     * 由于拦截器中已经做了用户获取的动作，不需要重复从redis中获取用户
     */
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        return UserContext.getUser();
    }
}
