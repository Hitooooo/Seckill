package com.hito.seckill.service;

import com.hito.seckill.common.redis.OrderKey;
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

    @Autowired
    private RedisService redisService;

    /**
     *
     * @param userId   用户id
     * @param goodsId 商品id
     * @return 如果用户秒杀了该商品，那么返回用户，否则返回null
     */
    public Order getMiaoshaOrderByUserIdGoodsId(Long userId, Long goodsId) {
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, ""+userId+"_"+goodsId, Order.class);
    }

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
        // 缓存下单信息，防止重复下单
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, ""+user.getId()+"_"+goodsVo.getId(), miaoshaOrder);
        return orderInfo;
    }

    public OrderInfo getById(long orderId) {
        return orderMapper.getById(orderId);
    }
}
