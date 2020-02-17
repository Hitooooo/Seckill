package com.hito.seckill.controller;

import com.hito.seckill.domain.Order;
import com.hito.seckill.domain.OrderInfo;
import com.hito.seckill.domain.SeckillUser;
import com.hito.seckill.domain.vo.GoodVo;
import com.hito.seckill.mapper.OrderMapper;
import com.hito.seckill.result.CodeMsg;
import com.hito.seckill.service.GoodService;
import com.hito.seckill.service.OrderService;
import com.hito.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 秒杀。需要配合订单
 *
 * @author HitoM
 * @date 2020/2/17 22:38
 **/
@Controller
@RequestMapping("seckill")
public class SeckillController {

    private final SeckillService seckillService;

    private final GoodService goodService;

    private final OrderService orderService;

    @Autowired
    OrderMapper orderMapper;
    public SeckillController(SeckillService seckillService, GoodService goodService, OrderService orderService) {
        this.seckillService = seckillService;
        this.goodService = goodService;
        this.orderService = orderService;
    }

    @PostMapping("do_seckill")
    public String doSeckill(Model model, SeckillUser user, Long goodsId) {
        // 未登录
        if (null == user) {
            model.addAttribute("errorMsg", "未登录");
            return "miaosha_fail";
        }
        Order order = orderMapper.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            model.addAttribute("errorMsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        if (goodsId <= 0 || !goodService.checkStockCount(goodsId)) {
            model.addAttribute("errorMsg", "商品已售空");
            return "miaosha_fail";
        }
        GoodVo goodVo = goodService.getGoodsVoByGoodsId(goodsId);
        OrderInfo orderInfo = seckillService.doSeckill(user, goodVo);
        model.addAttribute("goods", goodVo);
        model.addAttribute("orderInfo", orderInfo);
        return "order_detail";
    }
}
