package com.hito.seckill.controller;

import com.hito.seckill.result.CodeMsg;
import com.hito.seckill.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/14 22:23
 **/
@RequestMapping("demo")
@Controller
public class DemoController {

    @RequestMapping("thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name", "hello thymeleaf");
        return "hello";
    }

    @RequestMapping("success")
    @ResponseBody
    public Result<String> success(){
        return Result.success("i am ok");
    }

    @RequestMapping("error")
    @ResponseBody
    public Result<String> error(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }
}
