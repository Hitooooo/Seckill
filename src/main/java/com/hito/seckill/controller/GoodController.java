package com.hito.seckill.controller;

import com.hito.seckill.common.access.AccessLimit;
import com.hito.seckill.common.redis.GoodKey;
import com.hito.seckill.domain.SeckillUser;
import com.hito.seckill.domain.vo.GoodVo;
import com.hito.seckill.domain.vo.GoodsDetailVo;
import com.hito.seckill.result.Result;
import com.hito.seckill.service.GoodService;
import com.hito.seckill.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

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

    @Autowired
    RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    // produces控制返回的是html模板文件 注意返回responseBody
    @AccessLimit(seconds = 3, maxCount = 1, needLogin = false)
    @GetMapping(value = "/to_list", produces = "text/html;charset=UTF-8")
    public @ResponseBody
    String toList(HttpServletRequest request, HttpServletResponse response,
                  Model model, SeckillUser user) {
        // 产品列表页面缓存在redis中
        String html = redisService.get(GoodKey.getGoodsList, "", String.class);
        if (StringUtils.isNotEmpty(html)) {
            log.info("商品列表页面有缓存，我直接返回页面了");
            return html;
        }
        // 缓存不存在，就手动生成页面
        List<GoodVo> goodVos = goodService.listGoodsVo();
        model.addAttribute("goodsList", goodVos);
        model.addAttribute("user", user);

        log.info("Query good list success. total count:{}", goodVos.size());
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("good_list", webContext);

        if (StringUtils.isNotEmpty(html)) {
            redisService.set(GoodKey.getGoodsList, "", html);
        }
        return html;
    }

    // 详情页面不再是由thymeleaf渲染，而是静态的html，然后通过ajax获取详情
    @GetMapping(value = "detail/{goodsId}")
    public @ResponseBody Result<GoodsDetailVo> getDetail(SeckillUser user, @PathVariable Long goodsId) {
        GoodVo goodVo = goodService.getGoodsVoByGoodsId(goodsId);
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
        return Result.success(detail);
    }
}
