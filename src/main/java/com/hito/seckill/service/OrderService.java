package com.hito.seckill.service;

import com.hito.seckill.domain.Order;
import com.hito.seckill.domain.OrderInfo;
import com.hito.seckill.domain.SeckillUser;
import com.hito.seckill.domain.vo.GoodVo;
import com.hito.seckill.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/17 22:42
 **/
@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    public OrderInfo createOrder(SeckillUser user, GoodVo goodsVo) {
        // 下订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(Date.from(Instant.now()));
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderMapper.insert(orderInfo);

        // 下秒杀订单
        Order miaoshaOrder = new Order();
        miaoshaOrder.setGoodsId(goodsVo.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        orderMapper.insertMiaoshaOrder(miaoshaOrder);
        log.info("成功下单. userId={}, goodsId={}", user.getId(), goodsVo.getId());
        return orderInfo;
    }

    public OrderInfo getById(long orderId) {
        return orderMapper.getById(orderId);
    }
}
