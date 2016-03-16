/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-18 下午12:06:09
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.core.kernel.KernelObject;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Iterator;

public class InjectObserverSoftObject extends InjectObserverClass {

    Class<?> beanType;

    public InjectObserverSoftObject(Class<?> beanType) {
        this.beanType = beanType;
    }

    @Override
    public int hashCode() {
        return KernelObject.hashCode(beanType);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof InjectObserverSoftObject) {
            return KernelObject.equals(beanType, ((InjectObserverSoftObject) obj).beanType);
        }

        return beanType.equals(obj);
    }

    public void changed(BeanFactory beanFactory, BeanDefine beanDefine, Collection<SoftReference<Object>> beanObjects, boolean register) {
        for (InjectObserver injectObserver : injectObservers) {
            if (injectObserver.support(beanDefine, register)) {
                Object parameter = injectObserver.parameter(beanFactory, beanDefine);
                synchronized (beanObjects) {
                    Iterator<SoftReference<Object>> iterator = beanObjects.iterator();
                    while (iterator.hasNext()) {
                        Object beanObject = iterator.next().get();
                        if (beanObject == null) {
                            iterator.remove();

                        } else {
                            injectObserver.observer(beanObject, parameter);
                        }
                    }
                }
            }
        }
    }
}
