package com.hito.seckill.domain;

import lombok.Data;

import java.util.Date;

/**
 * 原有商品的基础上，还有秒杀需要的一些属性
 *
 * @author HitoM
 * @date 2020/2/17 17:45
 **/
@Data
public class SeckillGood {
    private Long id;
    private Long goodsId;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
