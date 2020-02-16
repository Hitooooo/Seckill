package com.hito.seckill.common.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

/**
 * string和object之间的想换转换，包括Integer、Double等包装类
 *
 * @author HitoM
 * @date 2020/2/16 12:30
 **/
public class StrObjConverter {
    public static <T> String obj2Str(T val) {
        if (val == null) {
            return null;
        }
        Class<?> valClass = val.getClass();
        boolean baseType = isBaseType(valClass, true);
        if (baseType) {
            return String.valueOf(val);
        } else {
            return JSON.toJSONString(val);
        }
    }

    public static <T> T str2Obj(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(json);
        } else if (clazz == String.class) {
            return (T) json;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(json);
        } else {
            return JSON.toJavaObject(JSON.parseObject(json), clazz);
        }
    }

    /**
     * 判断对象属性是否是基本数据类型,包括是否包括string
     *
     * @param className
     * @param incString 是否包括string判断,如果为true就认为string也是基本数据类型
     * @return
     */
    public static boolean isBaseType(Class<?> className, boolean incString) {
        if (incString && className.equals(String.class)) {
            return true;
        }
        return className.equals(Integer.class) ||
                className.equals(int.class) ||
                className.equals(Byte.class) ||
                className.equals(byte.class) ||
                className.equals(Long.class) ||
                className.equals(long.class) ||
                className.equals(Double.class) ||
                className.equals(double.class) ||
                className.equals(Float.class) ||
                className.equals(float.class) ||
                className.equals(Character.class) ||
                className.equals(char.class) ||
                className.equals(Short.class) ||
                className.equals(short.class) ||
                className.equals(Boolean.class) ||
                className.equals(boolean.class);
    }
}
