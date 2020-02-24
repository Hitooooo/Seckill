package com.hito.seckill.common.redis;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/24 23:50
 **/
public class AccessKey  extends BaseKeyPrefix{
    private AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static AccessKey withExpireSeconds(int expireSeconds) {
        return new AccessKey(expireSeconds, "access");
    }
}
