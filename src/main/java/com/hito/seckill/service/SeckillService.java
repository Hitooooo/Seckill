package com.hito.seckill.service;

import com.hito.seckill.common.exception.GlobalException;
import com.hito.seckill.common.redis.GoodKey;
import com.hito.seckill.domain.Good;
import com.hito.seckill.domain.Order;
import com.hito.seckill.domain.OrderInfo;
import com.hito.seckill.domain.SeckillUser;
import com.hito.seckill.domain.vo.GoodVo;
import com.hito.seckill.mapper.GoodMapper;
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

    private final GoodMapper goodMapper;

    public SeckillService(GoodMapper goodMapper, GoodService goodService, OrderService orderService, RedisService redisService, OrderMapper orderMapper) {
        this.goodMapper = goodMapper;
        this.goodService = goodService;
        this.orderService = orderService;
        this.redisService = redisService;
        this.orderMapper = orderMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderInfo doSeckill(SeckillUser user, GoodVo good){
        boolean reduceStock = goodService.reduceStock(good);
        if (reduceStock) {
            // 下订单
            return orderService.createOrder(user, good);
        } else {
            // 设置该商品状态为已售完
            setGoodOver(good.getId());
            return null;
        }
    }

    private void setGoodOver(Long goodsId) {
        redisService.set(GoodKey.IS_GOODS_OVER, ""+goodsId, true);
    }

    private boolean isGoodOver(Long goodsId) {
        return redisService.get(GoodKey.IS_GOODS_OVER, ""+goodsId, Boolean.class);
    }

    /**
     * 库存 -1
     * @param goodsVo 秒杀商品
     */
    public boolean reduceStock(GoodVo goodsVo) {
        Order miaoshaOrder = new Order();
        miaoshaOrder.setGoodsId(goodsVo.getId());
        int i = goodMapper.reduceStock(miaoshaOrder);
        return i > 0;
    }
}
