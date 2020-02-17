package com.hito.seckill.domain;

import lombok.Data;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/17 22:46
 **/
@Data
public class Order {
    private Long id;
    private Long userId;
    private Long  orderId;
    private Long goodsId;
}
