package com.hito.seckill.controller;

import cn.hutool.core.util.ImageUtil;
import com.hito.seckill.common.redis.GoodKey;
import com.hito.seckill.domain.Order;
import com.hito.seckill.domain.SeckillUser;
import com.hito.seckill.domain.vo.GoodVo;
import com.hito.seckill.mapper.OrderMapper;
import com.hito.seckill.result.CodeMsg;
import com.hito.seckill.result.Result;
import com.hito.seckill.rmq.MessageSender;
import com.hito.seckill.rmq.dto.SeckillMsg;
import com.hito.seckill.service.GoodService;
import com.hito.seckill.service.OrderService;
import com.hito.seckill.service.RedisService;
import com.hito.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 秒杀。需要配合订单
 *
 * @author HitoM
 * @date 2020/2/17 22:38
 **/
@Controller
@RequestMapping("seckill")
@Slf4j
public class SeckillController implements InitializingBean {

    private static final Map<Long, Boolean> LOCAL_GOODS_MAP = new ConcurrentHashMap<>();

    private final SeckillService seckillService;

    private final GoodService goodService;

    private final OrderService orderService;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    RedisService redisService;

    @Autowired
    MessageSender messageSender;
    public SeckillController(SeckillService seckillService, GoodService goodService, OrderService orderService) {
        this.seckillService = seckillService;
        this.goodService = goodService;
        this.orderService = orderService;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodVo> goodsVos = goodService.listGoodsVo();
        if (null != goodsVos) {
            goodsVos.parallelStream().forEach(goodsVo -> {
                redisService.set(GoodKey.getMiaoshaGoodsStock, ""+goodsVo.getId(), goodsVo.getStockCount());
                LOCAL_GOODS_MAP.put(goodsVo.getId(), goodsVo.getStockCount() <= 0);
            });
        }
      log.info("加载数据库中商品列表完成。GOOD_MAP:{}", LOCAL_GOODS_MAP);
    }

    // 5000 qps 734
    @PostMapping("/{path}/do_seckill")
    public @ResponseBody Result<Integer> doSeckill(@PathVariable String path,  SeckillUser user, Long goodsId) {
        // 未登录
        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
//        Order order = orderMapper.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        // 从缓存中快速获取
        if (goodsId <= 0) {
            return Result.error(CodeMsg.GOOD_NOT_EXIST);
        }

        // 判断秒杀接口,60s会自动失效
        boolean checkMiaoshaPath = seckillService.checkMiaoshaPath(user, goodsId, path);
        if (!checkMiaoshaPath) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        // 内存标记, 减少redis访问
        if (LOCAL_GOODS_MAP.get(goodsId)) {
            log.info("商品不足！from memory flag");
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        Order order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        // 符合下单条件，查看是否有足够数量秒杀
        Long stock = redisService.decr(GoodKey.getMiaoshaGoodsStock, goodsId + "");
        if (null != stock && stock < 0) {
            LOCAL_GOODS_MAP.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        messageSender.miaoshaSender(new SeckillMsg(user, goodsId));
        return Result.success(0);
    }

    @GetMapping("result")
    public @ResponseBody Result<Long> miaoshaResult(SeckillUser user, Long goodsId) {
        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        if (goodsId <= 0) {
            return Result.error(CodeMsg.GOOD_NOT_EXIST);
        }

        long orderId = seckillService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(orderId);
    }

    @GetMapping("verifyCode")
    public @ResponseBody Result getMiaoshaVerifyCode(HttpServletResponse response, SeckillUser user, Long goodsId) {
        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        if (goodsId <= 0) {
            return Result.error(CodeMsg.GOOD_NOT_EXIST);
        }

        BufferedImage bufferedImage = seckillService.createVerifyCode(user, goodsId);

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            ImageIO.write(bufferedImage, ImageUtil.IMAGE_TYPE_JPEG, outputStream);
            outputStream.flush();
            return null;
        } catch (IOException e) {
            log.error(e.getMessage());
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

    @GetMapping("path")
    public @ResponseBody Result<String> getMiaoshaPath(SeckillUser user, Long goodsId, Integer verifyCode) {

        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        if (goodsId <= 0) {
            return Result.error(CodeMsg.GOOD_NOT_EXIST);
        }

        // 校验验证码是否正确
        boolean code = seckillService.checkMiaoshaVerifyCode(user, goodsId, verifyCode);
        if (!code) {
            return Result.error(CodeMsg.VERIFY_CODE_FAIL);
        }

        // 生成请求地址
        String path = seckillService.createMiaoshaPath(user, goodsId);

        return Result.success(path);
    }
}
