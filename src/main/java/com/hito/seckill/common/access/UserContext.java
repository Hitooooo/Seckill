package com.hito.seckill.common.access;

import com.hito.seckill.domain.SeckillUser;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/24 23:53
 **/
public class UserContext {
    private static final ThreadLocal<SeckillUser> USER_HOLDER = new ThreadLocal<>();

    public static SeckillUser getUser() {
        return USER_HOLDER.get();
    }

    public static void setUser(SeckillUser user) {
        USER_HOLDER.set(user);
    }

    public static void remove() {
        USER_HOLDER.remove();
    }
}
