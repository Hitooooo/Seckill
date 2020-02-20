package com.hito.seckill.common.redis;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/20 10:39
 **/
public class OrderKey extends BaseKeyPrefix {
    /**
     * 订单默认不会过期
     * @param prefix order前缀
     */
    protected OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("seckill_order");
}
