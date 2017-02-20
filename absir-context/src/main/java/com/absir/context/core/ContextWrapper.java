/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-14 下午6:37:29
 */
package com.absir.context.core;

import com.absir.context.bean.IContext;
import com.absir.core.kernel.KernelObject;

public class ContextWrapper extends ContextBase {

    private IContext context;

    public ContextWrapper(IContext context) {
        this.context = context;
    }

    @Override
    public void retainAt(long contextTime) {
        context.retainAt(contextTime);
    }

    @Override
    public boolean stepDone(long contextTime) {
        return context.stepDone(contextTime);
    }

    @Override
    public void unInitialize() {
        context.unInitialize();
    }

    @Override
    public int hashCode() {
        return KernelObject.hashCode(context);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == null || (obj instanceof ContextWrapper ? KernelObject.equals(context, ((ContextWrapper) obj).context) : KernelObject.equals(context, obj));
    }
}
