package com.hito.seckill.controller;

import com.hito.seckill.common.access.AccessLimit;
import com.hito.seckill.domain.vo.LoginVo;
import com.hito.seckill.result.Result;
import com.hito.seckill.service.SeckillUserService;
import com.hito.seckill.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/16 15:59
 **/
@Controller
@RequestMapping("login")
@Slf4j
public class LoginController {

    @Autowired
    SeckillUserService seckillUserService;

    @GetMapping("login")
    public String login(){
        return "login";
    }

    @AccessLimit(seconds = 1, maxCount = 1, needLogin = false)
    @PostMapping("do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, LoginVo loginVo){
        seckillUserService.login(response, loginVo);
        return Result.success(true);
    }

}
