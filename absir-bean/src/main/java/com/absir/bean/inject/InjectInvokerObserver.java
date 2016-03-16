/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-18 下午12:57:11
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.inject.value.InjectType;

public abstract class InjectInvokerObserver extends InjectInvoker {

    InjectType injectType;

    public InjectInvokerObserver(InjectType injectType) {
        this.injectType = injectType;
    }

    public void invoke(BeanFactory beanFactory, Object beanObject) {
        invokeImpl(beanObject, parameter(beanFactory));
    }

    protected abstract Object parameter(BeanFactory beanFactory);

    protected abstract void invokeImpl(Object beanObject, Object parameter);

    public InjectObserver getInjectObserver() {
        if (injectType == InjectType.ObServed || injectType == InjectType.ObServeRealed) {
            return getInjectObserverImpl();
        }

        return null;
    }

    protected InjectObserver getInjectObserverImpl() {
        return null;
    }
}
