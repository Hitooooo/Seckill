package com.hito.seckill.common.access;

import com.alibaba.fastjson.JSON;
import com.hito.seckill.common.redis.AccessKey;
import com.hito.seckill.domain.SeckillUser;
import com.hito.seckill.result.CodeMsg;
import com.hito.seckill.result.Result;
import com.hito.seckill.service.RedisService;
import com.hito.seckill.service.SeckillUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/24 23:47
 **/
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


    /**
     * 向响应流写数据.返回前端错误具体原因
     */
    private void render(HttpServletResponse response, CodeMsg codeMsg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(codeMsg));
        outputStream.write(str.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

    private SeckillUser getUser(HttpServletRequest request, HttpServletResponse response) {

        String paramToken = request.getParameter(SeckillUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, SeckillUserService.COOKIE_NAME_TOKEN);

        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }

        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return userService.getByToken(response, token);
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
