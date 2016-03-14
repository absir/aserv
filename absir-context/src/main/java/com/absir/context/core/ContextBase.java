/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-11 下午5:21:36
 */
package com.absir.context.core;

import com.absir.context.bean.IContext;

/**
 * @author absir
 */
public class ContextBase implements IContext {

    /**
     * expirationTime
     */
    protected long expirationTime;

    /**
     * @return
     */
    public final boolean isExpiration() {
        return expirationTime == -1;
    }

    /**
     *
     */
    public final void setExpiration() {
        expirationTime = -1;
    }

    /**
     * @return
     */
    protected long getLifeTime() {
        return 600000;
    }

    /**
     *
     */
    public void retainAt() {
        retainAt(ContextUtils.getContextTime());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.system.context.IContext#retainAt(long)
     */
    @Override
    public void retainAt(long contextTime) {
        expirationTime = contextTime + getLifeTime();
    }

    /*
     * MUST NIO
     *
     * (non-Javadoc)
     *
     * @see com.absir.aserv.system.context.IContext#stepDone(long)
     */
    @Override
    public boolean stepDone(long contextTime) {
        return expirationTime < contextTime;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.context.bean.IContext#uninitializeDone()
     */
    @Override
    public boolean uninitializeDone() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.system.context.IContext#uninitialize()
     */
    @Override
    public void uninitialize() {
    }
}
