package com.hito.seckill.domain.vo;

import com.hito.seckill.domain.SeckillUser;
import lombok.Data;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/17 19:24
 **/
@Data
public class GoodsDetailVo {
    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodVo goods;
    private SeckillUser user;
}
