package com.hito.seckill.controller;

import com.hito.seckill.domain.SeckillUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/16 19:35
 **/
@Controller
@RequestMapping("goods")
public class GoodController {

    @GetMapping("to_list")
    public String toList(HttpServletRequest request, HttpServletResponse response,
                         Model model, SeckillUser user){
        model.addAttribute("user", user);
        return "good_list";
    }
}
