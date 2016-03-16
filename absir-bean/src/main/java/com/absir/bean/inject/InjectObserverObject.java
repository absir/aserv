/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-18 下午12:05:39
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.core.kernel.KernelObject;

public class InjectObserverObject extends InjectObserverClass {

    Object beanObject;

    public InjectObserverObject(Object beanObject) {
        this.beanObject = beanObject;
    }

    @Override
    public int hashCode() {
        return KernelObject.hashCode(beanObject);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof InjectObserverObject) {
            return KernelObject.equals(beanObject, ((InjectObserverObject) obj).beanObject);
        }

        return beanObject.equals(obj);
    }

    public void changed(BeanFactory beanFactory, BeanDefine beanDefine, boolean register) {
        if (injectObservers != null) {
            for (InjectObserver injectObserver : injectObservers) {
                injectObserver.observer(beanFactory, beanDefine, beanObject);
            }
        }
    }

}
