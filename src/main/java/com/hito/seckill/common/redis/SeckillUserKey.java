package com.hito.seckill.common.redis;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/16 19:42
 **/
public class SeckillUserKey extends BaseKeyPrefix {
    public SeckillUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    private static final int TOKEN_EXPIRE = 3600 * 24 * 30;
    public static SeckillUserKey TOKEN = new SeckillUserKey(TOKEN_EXPIRE, "token");
    public static SeckillUserKey getById = new SeckillUserKey(TOKEN_EXPIRE, "getById");
}
