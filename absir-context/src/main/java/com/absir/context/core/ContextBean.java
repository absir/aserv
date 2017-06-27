/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-9 上午11:11:10
 */
package com.absir.context.core;

import com.absir.context.bean.IContext;

import java.io.Serializable;

public abstract class ContextBean<ID extends Serializable> extends Context<ID> implements IContext {

    protected long retainAt;

    protected long expirationTime;

    public final boolean isExpiration() {
        return expirationTime == -1;
    }

    public final void setExpiration() {
        expirationTime = -1;
    }

    public void setUnExpiration(boolean unExpiration) {
        retainAt = unExpiration ? -1 : ContextUtils.getContextTime();
    }

    protected long getLifeTime() {
        return 600000;
    }

    public void retainAt() {
        retainAt(ContextUtils.getContextTime());
    }

    @Override
    public void retainAt(long contextTime) {
        if (retainAt >= 0) {
            retainAt = contextTime;
        }

        expirationTime = contextTime + getLifeTime();
    }

    @Override
    public boolean stepDone(long contextTime) {
        return retainAt >= 0 && expirationTime < contextTime;
    }

    public Class<?> getContextClass() {
        return getClass();
    }

    @Override
    public boolean unInitializeDone() {
        return false;
    }

    @Override
    public void unInitialize() {
    }
}
