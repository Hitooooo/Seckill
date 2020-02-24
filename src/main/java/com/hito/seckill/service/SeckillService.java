package com.hito.seckill.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.script.ScriptUtil;
import com.hito.seckill.common.exception.GlobalException;
import com.hito.seckill.common.redis.GoodKey;
import com.hito.seckill.common.redis.SeckillKey;
import com.hito.seckill.common.util.MD5Utils;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

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

    private static final char[] OPS = new char[]{'+', '-', '*'};

    public SeckillService(GoodMapper goodMapper, GoodService goodService, OrderService orderService, RedisService redisService, OrderMapper orderMapper) {
        this.goodMapper = goodMapper;
        this.goodService = goodService;
        this.orderService = orderService;
        this.redisService = redisService;
        this.orderMapper = orderMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderInfo doSeckill(SeckillUser user, GoodVo good){
        log.info("开始真正的mysql操作");
        boolean reduceStock = goodService.reduceStock(good);
        if (reduceStock) {
            // 下订单
            log.info("购买成功，开始生成订单");
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

    /**
     * 验证用户输入的验证码。正确记得删除
     * @param user user
     * @param goodsId 商品id
     * @param verifyCode 输入的验证码
     * @return 是否验证通过
     */
    public boolean checkMiaoshaVerifyCode(SeckillUser user, Long goodsId, Integer verifyCode) {
        Integer oldCode = redisService.get(SeckillKey.GET_MIAOSHA_VERIFY_CODE, "" + user.getId() + "_" + goodsId, Integer.class);
        if (null == oldCode || verifyCode == null || oldCode - verifyCode != 0) {
            return false;
        }
        redisService.delete(SeckillKey.GET_MIAOSHA_VERIFY_CODE, "" + user.getId() + "_" + goodsId);
        return true;
    }

    /**
     * 根据用户id，生成一个验证码。并且保存在redis中
     * @param user
     * @param goodsId
     * @return
     */
    public BufferedImage createVerifyCode(SeckillUser user, Long goodsId) {
        int width = 80, height = 32;
        // 创建图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        // 设置画笔颜色
        graphics.setColor(new Color(0xDCDCDC));
        // 设置背景颜色
        graphics.fillRect(0, 0, width, height);
        // 重设画笔颜色
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawRect(0, 0, width -1, height -1);
        // 创建50个干扰点
        Random random = new Random();
        IntStream.range(0, 50).forEach(i->{
            int x = random.nextInt();
            int y = random.nextInt();
            graphics.drawOval(x, y, 0, 0);
        });
        // 创建验证码
        String verifyCode = verifyCode(random);
        graphics.setColor(new Color(0, 100, 0));
        graphics.setFont(new Font("Candara", Font.BOLD, 24));
        graphics.drawString(verifyCode, 8, 24);
        graphics.dispose();

        // 将验证码存放到redis中
        int rnd = calc(verifyCode);
        redisService.set(SeckillKey.GET_MIAOSHA_VERIFY_CODE, ""+user.getId()+"_"+goodsId, rnd);
        return image;
    }

    private int calc(String exp) {
        return (Integer) ScriptUtil.eval(exp);
    }

    private String verifyCode(Random random) {
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);

        char op1 = OPS[random.nextInt(3)];
        char op2 = OPS[random.nextInt(3)];
        return String.format("%d%s%d%s%d", num1, op1, num2, op2, num3);
    }

    /**
     * 生成秒杀地址
     */
    public String createMiaoshaPath(SeckillUser user, Long goodsId) {
        String path = MD5Utils.md5(RandomUtil.randomString(32) + "&*!@:LJ:");
        // 存入redis
        redisService.set(SeckillKey.GET_MIAOSHA_PATH, ""+user.getId()+"_"+goodsId, path);
        return path;
    }

    /**
     * 校验秒杀地址
     */
    public boolean checkMiaoshaPath(SeckillUser user, Long goodsId, String path) {
        if (null == user || null == path) {
            return false;
        }
        String pathOld = redisService.get(SeckillKey.GET_MIAOSHA_PATH, "" + user.getId() + "_" + goodsId, String.class);
        return Objects.equals(pathOld, path);
    }

    public long getMiaoshaResult(Long id, Long goodsId) {
        Order miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(id, goodsId);
        // 秒杀成功
        if (null != miaoshaOrder) {
            return miaoshaOrder.getId();
        } else {
            // 判断该商品是否已售完
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private boolean getGoodsOver(Long goodsId) {
        return redisService.exists(GoodKey.IS_GOODS_OVER, ""+goodsId);
    }
}
