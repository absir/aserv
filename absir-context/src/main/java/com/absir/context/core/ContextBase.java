/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-11 下午5:21:36
 */
package com.absir.context.core;

import com.absir.context.bean.IContext;

public class ContextBase implements IContext {

    protected long expirationTime;

    public final boolean isExpiration() {
        return expirationTime == -1;
    }

    public final void setExpiration() {
        expirationTime = -1;
    }

    protected long getLifeTime() {
        return 600000;
    }

    public void retainAt() {
        retainAt(ContextUtils.getContextTime());
    }

    @Override
    public void retainAt(long contextTime) {
        expirationTime = contextTime + getLifeTime();
    }

    @Override
    public boolean stepDone(long contextTime) {
        return expirationTime < contextTime;
    }

    @Override
    public boolean uninitializeDone() {
        return true;
    }

    @Override
    public void uninitialize() {
    }
}
