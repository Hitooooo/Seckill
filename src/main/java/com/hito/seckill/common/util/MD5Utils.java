package com.hito.seckill.common.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/16 16:47
 **/
public class MD5Utils {
    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String SALT = "fas_12^*(%&";

    public static String inputPassToFormPass(String src) {
        String str = SALT.charAt(4) + SALT.substring(4) + SALT.substring(0, 5) + src + SALT.charAt(9);
        return md5(str);
    }

    public static String formPassToDBPass(String src, String salt) {
        String str = salt.charAt(4) + salt.substring(4) + salt.substring(0, 5) + src + salt.charAt(9);
        return md5(str);
    }


    public static String inputPassToDBPass(String src, String saltDB) {
        return formPassToDBPass(inputPassToFormPass(src), saltDB);
    }

    public static void main(String[] args) {
        System.out.println(inputPassToDBPass("123456", "y95834zn39"));
    }
}
