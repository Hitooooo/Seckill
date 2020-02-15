package com.hito.seckill.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 获取配置文件中的redis配置
 *
 * @author HitoM
 * @date 2020/2/15 12:04
 **/
@ConfigurationProperties("redis")
@Component
@Data
public class RedisConfig {
    private String host;
    private Integer port;
    private Integer timeout;
    private String password;
    private Integer poolMaxTotal;
    private Integer poolMaxIdle;
    private Integer poolMaxWait;
    private Integer database;
}
