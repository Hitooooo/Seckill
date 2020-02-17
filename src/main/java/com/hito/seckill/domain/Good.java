package com.hito.seckill.domain;

import lombok.Data;

/**
 * 商品
 *
 * @author HitoM
 * @date 2020/2/17 17:45
 **/
@Data
public class Good {
    private Long id;
    private String goodsName;
    private String goodsTitle;
    private String goodsImg;
    private String goodsDetail;
    private Double goodsPrice;
    private Integer goodsStock;
}
