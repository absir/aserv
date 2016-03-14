/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-12-18 下午12:06:09
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.core.kernel.KernelObject;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author absir
 */
public class InjectObserverSoftObject extends InjectObserverClass {

    /**
     * beanType
     */
    Class<?> beanType;

    /**
     * @param beanType
     */
    public InjectObserverSoftObject(Class<?> beanType) {
        this.beanType = beanType;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return KernelObject.hashCode(beanType);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
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

    /**
     * @param beanFactory
     * @param beanDefine
     * @param register
     */
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
