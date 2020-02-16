package com.hito.seckill.common.redis;

/**
 * 抽取公共的key方法，实现了定义的key接口
 *
 * @author HitoM
 * @date 2020/2/15 20:19
 **/
public abstract class BaseKeyPrefix implements IKeyPrefix {

    private int expireSeconds;

    private String prefix;

    protected BaseKeyPrefix(String prefix) {
        this(0, prefix);
    }

    protected BaseKeyPrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int getExpireSeconds() {
        return this.expireSeconds;
    }

    @Override
    public String getPrefix() {
        String simpleName = getClass().getSimpleName();
        return simpleName + ":" + this.prefix;
    }

    @Override
    public String getRealKey(String key) {
        return getPrefix() + key;
    }
}
