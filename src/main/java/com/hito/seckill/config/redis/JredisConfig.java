package com.hito.seckill.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 获取redis connection配置类
 *
 * @author HitoM
 * @date 2020/2/15 12:07
 **/
@Component
public class JredisConfig {

    private final RedisConfig redisConfig;


    public JredisConfig(@Autowired RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait());
        return poolConfig;
    }

    @Bean
    public JedisPool jedisPool(JedisPoolConfig poolConfig) {
        String password = "".equals(redisConfig.getPassword()) ? null : redisConfig.getPassword();
        return new JedisPool(poolConfig, redisConfig.getHost(), redisConfig.getPort(),
                redisConfig.getTimeout(), password, redisConfig.getDatabase());
    }
}
