package com.hito.seckill.controller;

import com.hito.seckill.domain.SeckillUser;
import com.hito.seckill.domain.vo.GoodVo;
import com.hito.seckill.domain.vo.GoodsDetailVo;
import com.hito.seckill.service.GoodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.List;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/16 19:35
 **/
@Controller
@RequestMapping("goods")
@Slf4j
public class GoodController {

    @Autowired
    private GoodService goodService;

    @GetMapping("to_list")
    public String toList(HttpServletRequest request, HttpServletResponse response,
                         Model model, SeckillUser user){
        List<GoodVo> goodVos = goodService.listGoodsVo();
        log.info("Query good list success. total count:{}", goodVos.size());
        model.addAttribute("goodsList", goodVos);
        return "good_list";
    }

    @GetMapping(value = "to_detail/{goodId}")
    public String goodDetail(Model model,SeckillUser user, @PathVariable Long goodId){
        GoodVo goodVo = goodService.getGoodsVoByGoodsId(goodId);
        long startTme = goodVo.getStartDate().getTime();
        long endTime = goodVo.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus;
        int remainSeconds;

        // 还没开始
        if (now < startTme) {
            miaoshaStatus = 0;
            remainSeconds = (int) (startTme - now) / 1000;
            // 已结束
        } else if (now > endTime) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        GoodsDetailVo detail = new GoodsDetailVo();
        detail.setGoods(goodVo);
        detail.setMiaoshaStatus(miaoshaStatus);
        detail.setRemainSeconds(remainSeconds);
        detail.setUser(user);
        model.addAttribute("detail", detail);
        model.addAttribute("user", user);
        return "good_detail";
    }
}
