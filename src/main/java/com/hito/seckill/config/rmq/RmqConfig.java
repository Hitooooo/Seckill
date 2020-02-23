package com.hito.seckill.config.rmq;

import com.hito.seckill.common.Constant;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/23 13:03
 **/
@Configuration
public class RmqConfig {
    @Bean
    public Queue miaoshaQueue() {
        return new Queue(Constant.MIAOSHA_QUEUE, true);
    }


}
