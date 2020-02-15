package com.hito.seckill.mapper;

import com.hito.seckill.domain.User;
import org.apache.ibatis.annotations.Select;

/**
 * 查询用户相关
 *
 * @author HitoM
 * @date 2020/2/15 10:45
 **/
public interface UserMapper {
    @Select("select * from user where id = #{id}")
    User getUserById(Integer id);
}
