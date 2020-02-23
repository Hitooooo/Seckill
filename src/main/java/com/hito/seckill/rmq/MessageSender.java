package com.hito.seckill.rmq;

import com.hito.seckill.common.Constant;
import com.hito.seckill.common.util.StrObjConverter;
import com.hito.seckill.rmq.dto.SeckillMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/23 10:35
 **/
@Service
@Slf4j
public class MessageSender {
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void miaoshaSender(SeckillMsg message) {
        String msg = StrObjConverter.obj2Str(message);
        log.debug("miaosha sender={}", msg);
        amqpTemplate.convertAndSend(Constant.MIAOSHA_QUEUE, msg);
    }
}
