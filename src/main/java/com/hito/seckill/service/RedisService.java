package com.hito.seckill.service;

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

    public String get(String key) {
        try {
            Jedis resource = jedisPool.getResource();
            return resource.get(key);
        } finally {
            jedisPool.close();
        }
    }
}
