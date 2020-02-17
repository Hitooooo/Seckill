package com.hito.seckill.mapper;

import com.hito.seckill.domain.Order;
import com.hito.seckill.domain.vo.GoodVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/17 17:50
 **/
public interface GoodMapper {
    /**
     * 查询秒杀商品列表
     *
     * @return 秒杀商品列表
     */
    @Select("SELECT mg.miaosha_price, mg.stock_count, mg.start_date, mg.end_date, g.* " +
            "FROM miaosha_goods mg " +
            "LEFT JOIN goods g " +
            "ON mg.goods_id = g.id")
    List<GoodVo> listGoodsVo();

    @Select("SELECT mg.miaosha_price, mg.stock_count, mg.start_date, mg.end_date, g.* " +
            "FROM miaosha_goods mg " +
            "LEFT JOIN goods g " +
            "ON mg.goods_id = g.id " +
            "WHERE g.id = #{goodsId}")
    GoodVo getGoodsVoByGoodsId(@Param("goodsId")long goodsId);

    @Update("UPDATE miaosha_goods SET stock_count = stock_count - 1 WHERE goods_id = #{goodsId} ")
    int reduceStock(Order order);
}
