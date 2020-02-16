package com.hito.seckill;

import com.hito.seckill.common.redis.IKeyPrefix;
import com.hito.seckill.common.redis.UserKey;
import com.hito.seckill.domain.User;
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

    @Test
    public void redisKeyTest() {
        boolean exists = redisService.exists(UserKey.GET_BY_ID, "1");
        boolean set = redisService.set(UserKey.GET_BY_ID, "1", 100);
        User user = new User();
        user.setId(2);
        user.setName("hito2");
        boolean insertUser = redisService.set(UserKey.GET_BY_ID, user.getId() + "", user);
        System.out.println(insertUser);
    }
}
