package com.hito.seckill.domain.vo;

import com.hito.seckill.domain.OrderInfo;
import lombok.Data;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/24 23:23
 **/
@Data
public class OrderDetailVo {
    private OrderInfo order;
    private GoodVo goods;
}
