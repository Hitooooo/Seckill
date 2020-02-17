package com.hito.seckill.result;

import lombok.Data;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/14 22:56
 **/
@Data
public class CodeMsg {
    private int code;
    private String msg;

    // 通用错误
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常: %s");
    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102, "请求非法");
    public static CodeMsg VERIFY_CODE_FAIL = new CodeMsg(500102, "验证码错误");
    public static CodeMsg ACCESS_LIMIT_REACHED = new CodeMsg(500103, "请求太频繁了!");

    // 登录模块错误码
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已经失效");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "登录密码不能为空");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "手机号不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "手机号格式不正确");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "账号不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");

    // 秒杀模块
    public static CodeMsg MIAO_SHA_OVER = new CodeMsg(500500, "库存不足");
    public static CodeMsg REPEATE_MIAOSHA = new CodeMsg(500501, "不能重复下单");
    public static CodeMsg MIAOSHA_FAIL = new CodeMsg(500502, "秒杀失败");

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CodeMsg fillArgs(Object... args) {
        return new CodeMsg(this.code, String.format(this.msg, args));
    }
}
