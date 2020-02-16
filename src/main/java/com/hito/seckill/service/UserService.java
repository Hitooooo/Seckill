package com.hito.seckill.service;

import com.hito.seckill.domain.User;
import com.hito.seckill.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户Service
 *
 * @author HitoM
 * @date 2020/2/16 16:01
 **/
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public User getById(Integer id) {
        return userMapper.getUserById(id);
    }
}
