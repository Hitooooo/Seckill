package com.hito.seckill.common.redis;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/15 20:22
 **/
public class UserKey extends BaseKeyPrefix {
    public static UserKey GET_BY_ID = new UserKey("id");
    public static UserKey GET_BY_NAME = new UserKey("name");

    public UserKey(String prefix) {
        super(prefix);
    }
}
