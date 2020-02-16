package com.hito.seckill.service;

import com.hito.seckill.common.exception.GlobalException;
import com.hito.seckill.common.util.MD5Utils;
import com.hito.seckill.domain.SeckillUser;
import com.hito.seckill.domain.vo.LoginVo;
import com.hito.seckill.mapper.SeckillUserMapper;
import com.hito.seckill.result.CodeMsg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        SeckillUser user = seckillUserMapper.getById(Long.parseLong(loginVo.getMobile()));
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
        return true;
    }
}
