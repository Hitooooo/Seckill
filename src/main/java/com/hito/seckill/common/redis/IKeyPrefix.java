package com.hito.seckill.common.redis;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/15 20:17
 **/
public interface IKeyPrefix {
    /**
     * @return 该种key的过期时间
     */
    int getExpireSeconds();

    String getPrefix();

    String getRealKey(String key);
}
