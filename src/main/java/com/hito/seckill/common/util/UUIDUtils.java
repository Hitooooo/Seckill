package com.hito.seckill.common.util;

import java.util.UUID;

/**
 * 生成uuid
 *
 * @author HitoM
 * @date 2020/2/16 19:36
 **/
public class UUIDUtils {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
