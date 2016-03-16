/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-17 上午10:03:38
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.core.kernel.KernelLang.CloneTemplate;
import com.absir.core.kernel.KernelObject;

public abstract class BeanDefineAbstract implements BeanDefine {

    protected String beanName;

    public static BeanDefine getBeanDefine(Class<?> beanType, String beanName, Object beanObject, BeanScope beanScope,
                                           BeanDefine beanDefine) {
        Class<?> beanClass = beanObject.getClass();
        if (beanScope == BeanScope.PROTOTYPE) {
            if (beanObject instanceof CloneTemplate) {
                return beanType == beanClass ? new BeanDefineCloneable(beanName, (CloneTemplate<?>) beanObject)
                        : new BeanDefineCloneableType(beanType, beanName, (CloneTemplate<?>) beanObject);
            }

            if (beanDefine != null) {
                return new BeanDefinePrototype(beanDefine);
            }

        } else if (beanScope == BeanScope.SOFTREFERENCE) {
            return new BeanDefineSoftReference(beanClass, beanName, beanObject);
        }

        return beanType == beanClass ? new BeanDefineSingleton(beanName, beanObject) : new BeanDefineSingletonType(beanType,
                beanName, beanObject);
    }

    @Override
    public String getBeanName() {
        return beanName;
    }

    @Override
    public int hashCode() {
        return KernelObject.hashCode(getBeanComponent());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof BeanDefine)) {
            return false;
        }

        if (getBeanComponent() == null) {
            return ((BeanDefine) obj).getBeanComponent() == null;

        } else {
            return KernelObject.equals(getBeanComponent(), ((BeanDefine) obj).getBeanComponent());
        }
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory, BeanDefine beanDefineRoot, BeanDefine beanDefineWrapper) {
        Object beanObject = getBeanObject(beanFactory);
        if (beanDefineRoot != null) {
            beanObject = beanDefineRoot.getBeanProxy(beanObject, beanDefineRoot, beanFactory);
        }

        return beanObject;
    }

    @Override
    public Object getBeanProxy(Object beanObject, BeanDefine beanDefineRoot, BeanFactory beanFactory) {
        return beanObject;
    }
}
