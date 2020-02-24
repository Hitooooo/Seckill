package com.hito.seckill.common.redis;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/24 22:09
 **/
public class SeckillKey extends BaseKeyPrefix {
    protected SeckillKey(String prefix) {
        super(prefix);
    }

    protected SeckillKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    // 验证码key，5分钟过期
    public static final SeckillKey GET_MIAOSHA_VERIFY_CODE = new SeckillKey(300, "verify_code");

    public static final SeckillKey GET_MIAOSHA_PATH = new SeckillKey(300, "path");
}
