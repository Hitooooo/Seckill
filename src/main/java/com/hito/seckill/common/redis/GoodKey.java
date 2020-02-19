package com.hito.seckill.common.redis;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/19 20:49
 **/
public class GoodKey extends BaseKeyPrefix {
    private GoodKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodKey getGoodsList = new GoodKey(60, "gl");
    public static GoodKey getGoodsDetail = new GoodKey(60, "gd");

}
