package com.hito.seckill;

import com.hito.seckill.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/15 12:18
 **/
@SpringBootTest
public class DataSourceTest {
    @Autowired
    RedisService redisService;

    @Test
    void contextLoads() {
        String testkey = redisService.get("testkey");
        System.out.println(testkey);
    }
}
