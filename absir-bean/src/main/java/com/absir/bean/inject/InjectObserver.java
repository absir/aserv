/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-18 下午2:15:51
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.inject.value.InjectType;
import com.absir.core.kernel.KernelObject;

public abstract class InjectObserver {

    public abstract Object getComponent();

    @Override
    public int hashCode() {
        return KernelObject.hashCode(getComponent());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof InjectObserver) {
            return getComponent().equals(((InjectObserver) obj).getComponent());
        }

        return getComponent().equals(obj);
    }

    public abstract InjectType getInjectType();

    public boolean support(BeanDefine beanDefine, boolean register) {
        return (register || getInjectType() == InjectType.ObServed) && support(beanDefine);
    }

    protected abstract boolean support(BeanDefine beanDefine);

    public abstract Object parameter(BeanFactory beanFactory, BeanDefine beanDefine);

    public abstract boolean observer(Object beanObject, Object parameter);

    public boolean observer(BeanFactory beanFactory, BeanDefine beanDefine, Object beanObject) {
        return observer(beanObject, parameter(beanFactory, beanDefine));
    }

    public boolean changed(BeanFactory beanFactory, BeanDefine beanDefine, Object beanObject, boolean register) {
        if (support(beanDefine, register)) {
            return observer(beanFactory, beanDefine, beanObject);
        }

        return false;
    }
}
