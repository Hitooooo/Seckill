package com.hito.seckill.service;

import com.hito.seckill.common.exception.GlobalException;
import com.hito.seckill.domain.Good;
import com.hito.seckill.domain.Order;
import com.hito.seckill.domain.OrderInfo;
import com.hito.seckill.domain.SeckillUser;
import com.hito.seckill.domain.vo.GoodVo;
import com.hito.seckill.mapper.OrderMapper;
import com.hito.seckill.result.CodeMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 秒杀实现
 *
 * @author HitoM
 * @date 2020/2/17 22:41
 **/
@Service
@Slf4j
public class SeckillService {
    private final GoodService goodService;

    private final OrderService orderService;

    private final RedisService redisService;

    private final OrderMapper orderMapper;

    public SeckillService(GoodService goodService, OrderService orderService, RedisService redisService, OrderMapper orderMapper) {
        this.goodService = goodService;
        this.orderService = orderService;
        this.redisService = redisService;
        this.orderMapper = orderMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderInfo doSeckill(SeckillUser user, GoodVo good){
        // 判断库存
        Order order = orderMapper.getMiaoshaOrderByUserIdGoodsId(user.getId(), good.getId());
        boolean available = goodService.checkStockCount(good.getId());
        if (order != null || !available) {
            log.warn("秒杀失败，重复下单或库存不足");
            return null;
        }
        // 下单
        goodService.reduceStock(good);
        return orderService.createOrder(user, good);
    }
}
