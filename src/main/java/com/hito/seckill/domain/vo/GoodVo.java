package com.hito.seckill.domain.vo;

import com.hito.seckill.domain.Good;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/17 17:50
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodVo extends Good {
    private Double miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
