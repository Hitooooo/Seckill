package com.hito.seckill.config.web;

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
 * TODO
 *
 * @author HitoM
 * @date 2020/2/16 20:13
 **/
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    SeckillUserService seckillUserService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        // 只处理入参是秒杀用户的请求
        return methodParameter.getParameterType() == SeckillUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
        HttpServletResponse response = (HttpServletResponse) nativeWebRequest.getNativeResponse();
        // 兼容url或cookie中
        String paramToken = nativeWebRequest.getParameter(SeckillUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, SeckillUserService.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }

        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return seckillUserService.getByToken(response, token);
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (null != cookies && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
