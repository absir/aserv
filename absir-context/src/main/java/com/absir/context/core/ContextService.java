/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-10 下午1:48:14
 */
package com.absir.context.core;

public abstract class ContextService extends ContextBase {

    public abstract void step(long contextTime);

    @Override
    public final boolean stepDone(long contextTime) {
        step(contextTime);
        return false;
    }
}
