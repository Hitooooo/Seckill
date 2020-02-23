package com.hito.seckill.rmq.dto;

import com.hito.seckill.domain.SeckillUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀请求消息
 *
 * @author HitoM
 * @date 2020/2/23 10:34
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMsg {
    private SeckillUser seckillUser;
    private Long goodsId;
}
