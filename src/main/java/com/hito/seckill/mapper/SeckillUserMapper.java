package com.hito.seckill.mapper;

import com.hito.seckill.domain.SeckillUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 秒杀用户dao
 *
 * @author HitoM
 * @date 2020/2/16 16:18
 **/
public interface SeckillUserMapper {
    @Select("SELECT * FROM miaosha.miaosha_user WHERE id = #{id} ")
    SeckillUser getById(@Param("id") long id);

    @Update("UPDATE miaosha_user SET password = #{password} WHERE id = #{id} ")
    void update(SeckillUser updateUser);

    @Select("INSERT INTO miaosha_user (id, nickname, password, salt, head, redister_date, login_count) " +
            "VALUES (#{id}, #{nickname}, #{password}, #{salt}, #{head}, #{registerDate}, #{loginCount} )")
    SeckillUser insert(SeckillUser user);
}
