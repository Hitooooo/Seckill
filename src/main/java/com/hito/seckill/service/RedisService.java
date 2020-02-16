package com.hito.seckill.service;

import com.hito.seckill.common.redis.IKeyPrefix;
import com.hito.seckill.common.util.StrObjConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis操作类
 *
 * @author HitoM
 * @date 2020/2/15 12:10
 **/
@Service
public class RedisService {
    private final JedisPool jedisPool;

    @Autowired
    public RedisService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * the raw way to get value
     *
     * @param key string key
     * @return match value
     */
    public String get(String key) {
        try {
            Jedis resource = jedisPool.getResource();
            return resource.get(key);
        } finally {
            jedisPool.close();
        }
    }

    /**
     * get object from redis
     *
     * @param prefix prefix type
     * @param key    string after prefix
     * @param clazz  pojo
     * @param <T>    generic type
     * @return string converter to object which you want and defined in 2nd param
     */
    public <T> T get(IKeyPrefix prefix, String key, Class<T> clazz) {
        try (Jedis resource = jedisPool.getResource()) {
            String val = resource.get(prefix.getRealKey(key));
            return StrObjConverter.str2Obj(val, clazz);
        }
    }

    public <T> boolean set(IKeyPrefix prefix, String key, T value) {
        try(Jedis jedis = jedisPool.getResource()) {
            String str = StrObjConverter.obj2Str(value);
            if (null != str && str.length() > 0) {
                String realKey = prefix.getRealKey(key);
                int seconds = prefix.getExpireSeconds();
                if (seconds <= 0) {
                    jedis.set(realKey, str);
                } else {
                    jedis.setex(realKey, seconds, str);
                }
                return true;
            }
        }
        return false;
    }

    public boolean exists(IKeyPrefix prefix, String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            String realKey = prefix.getRealKey(key);
            return jedis.exists(realKey);
        }
    }

    public Long incr(IKeyPrefix prefix, String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            String realKey = prefix.getRealKey(key);
            return jedis.incr(realKey);
        }
    }

    public boolean delete(IKeyPrefix prefix, String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            String realKey = prefix.getRealKey(key);
            return jedis.del(realKey) > 0;
        }
    }

    public Long decr(IKeyPrefix prefix, String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            String realKey = prefix.getRealKey(key);
            return jedis.decr(realKey);
        }
    }
}
