package com.hito.seckill.service;

import com.hito.seckill.common.exception.GlobalException;
import com.hito.seckill.common.redis.SeckillUserKey;
import com.hito.seckill.common.util.MD5Utils;
import com.hito.seckill.common.util.UUIDUtils;
import com.hito.seckill.domain.SeckillUser;
import com.hito.seckill.domain.vo.LoginVo;
import com.hito.seckill.mapper.SeckillUserMapper;
import com.hito.seckill.result.CodeMsg;
import com.sun.deploy.net.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/16 16:17
 **/
@Service
public class SeckillUserService {
    private final SeckillUserMapper seckillUserMapper;

    private final RedisService redisService;

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    public SeckillUserService(SeckillUserMapper seckillUserMapper, RedisService redisService) {
        this.seckillUserMapper = seckillUserMapper;
        this.redisService = redisService;
    }

    private SeckillUser getUserById(Long id) {
        // 缓存取
        SeckillUser seckillUser = redisService.get(SeckillUserKey.getById, id + "", SeckillUser.class);
        if (seckillUser != null) {
            return seckillUser;
        }
        // 数据库取,并缓存
        seckillUser = seckillUserMapper.getById(id);
        if (seckillUser != null) {
            redisService.set(SeckillUserKey.getById, id + "", seckillUser);
        }
        return seckillUser;
    }

    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        // 获取用户，缓存或者数据库
        SeckillUser user = getUserById(Long.parseLong(loginVo.getMobile()));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // 验证md5
        String dbPwd = user.getPassword();
        String dbSalt = user.getSalt();

        String inputPwd = MD5Utils.inputPassToDBPass(loginVo.getPassword(), dbSalt);
        if (!StringUtils.equals(inputPwd, dbPwd)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        // 保存登录状态/更新缓存有效期
        String token = UUIDUtils.uuid();
        writeCookie(response, token, user);
        return true;
    }

    private void writeCookie(HttpServletResponse response, String token, SeckillUser user) {
        redisService.set(SeckillUserKey.TOKEN, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(SeckillUserKey.TOKEN.getExpireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 根据web保存的cookie种的token，获取用户
     *
     * @param response http response
     * @param token token in cookie
     * @return user if redis saved it
     */
    public SeckillUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        SeckillUser seckillUser = redisService.get(SeckillUserKey.TOKEN, token, SeckillUser.class);
        if (seckillUser != null) {
            // 延迟下cookie有效期
            writeCookie(response, token, seckillUser);
        }
        return seckillUser;
    }
}
