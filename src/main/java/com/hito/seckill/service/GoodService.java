package com.hito.seckill.service;

import com.hito.seckill.domain.vo.GoodVo;
import com.hito.seckill.mapper.GoodMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
}
