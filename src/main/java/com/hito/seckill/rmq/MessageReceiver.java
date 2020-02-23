package com.hito.seckill.rmq;

import com.hito.seckill.common.Constant;
import com.hito.seckill.common.util.StrObjConverter;
import com.hito.seckill.domain.Order;
import com.hito.seckill.domain.SeckillUser;
import com.hito.seckill.domain.vo.GoodVo;
import com.hito.seckill.rmq.dto.SeckillMsg;
import com.hito.seckill.service.GoodService;
import com.hito.seckill.service.OrderService;
import com.hito.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/23 10:36
 **/
@Service
@Slf4j
public class MessageReceiver {
    @Autowired
    private GoodService goodService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    @RabbitListener(queues = Constant.MIAOSHA_QUEUE)
    public void miaoshaQueueReceiver(String message) {
        if (StringUtils.isBlank(message)) {
            log.error("receiver empty message form rabbit mq");
            return;
        }
        log.debug("miaoshaQueueReceiver msg={}", message);
        SeckillMsg seckillMsg = StrObjConverter.str2Obj(message, SeckillMsg.class);
        if (seckillMsg != null) {
            SeckillUser seckillUser = seckillMsg.getSeckillUser();
            Long goodsId = seckillMsg.getGoodsId();
            // 判断库存
            GoodVo goodsVo = goodService.getGoodsVoByGoodsId(goodsId);
            if (null == goodsVo || goodsVo.getStockCount() <= 0) {
            log.error("miaoshaQueueReceiver, 库存不足, u={}, g={}", seckillUser.getId(), goodsId);
                return;
            }
            // 判断是否重复下单
            Order order = orderService.getMiaoshaOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
            if (order != null) {
                log.warn("u:{} 重复下单", seckillUser);
                return;
            }
            // 减库存，下订单
            seckillService.doSeckill(seckillUser, goodsVo);
        }else {
            log.warn("从message中获取用户、商品号失败.message:{}", message);
        }
    }
}
