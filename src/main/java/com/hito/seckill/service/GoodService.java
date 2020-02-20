package com.hito.seckill.service;

import com.hito.seckill.domain.Order;
import com.hito.seckill.domain.vo.GoodVo;
import com.hito.seckill.mapper.GoodMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/17 17:49
 **/
@Service
public class GoodService {
    private final GoodMapper goodMapper;

    public GoodService(GoodMapper goodMapper) {
        this.goodMapper = goodMapper;
    }

    public List<GoodVo> listGoodsVo() {
        return goodMapper.listGoodsVo();
    }

    public GoodVo getGoodsVoByGoodsId(long goodsId) {
        return goodMapper.getGoodsVoByGoodsId(goodsId);
    }

    /**
     * 判断库存
     * @param goodsId 商品id
     * @return 是否能够下单
     */
    public boolean checkStockCount(Long goodsId) {
        GoodVo goodsVo = this.getGoodsVoByGoodsId(goodsId);
        if (null != goodsVo) {
            Integer stockCount = goodsVo.getStockCount();
            return stockCount > 0;
        }
        return false;
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
