package com.hito.seckill.controller;

import com.hito.seckill.domain.OrderInfo;
import com.hito.seckill.domain.SeckillUser;
import com.hito.seckill.domain.vo.GoodVo;
import com.hito.seckill.domain.vo.OrderDetailVo;
import com.hito.seckill.result.CodeMsg;
import com.hito.seckill.result.Result;
import com.hito.seckill.service.GoodService;
import com.hito.seckill.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/24 23:20
 **/
@Controller
@Slf4j
@RequestMapping("order")
public class OrderController {
    private final OrderService orderService;
    private final GoodService goodsService;

    @Autowired
    public OrderController(OrderService orderService, GoodService goodsService) {
        this.orderService = orderService;
        this.goodsService = goodsService;
    }

    @GetMapping("detail")
    public @ResponseBody
    Result detail(SeckillUser user, Long orderId) {

        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        // 查询order
        OrderInfo orderInfo = orderService.getById(orderId);
        // 查询goods
        if (null == orderInfo) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        Long goodsId = orderInfo.getGoodsId();
        GoodVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setGoods(goodsVo);
        orderDetailVo.setOrder(orderInfo);

        return Result.success(orderDetailVo);
    }
}
