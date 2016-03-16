/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-17 上午11:44:29
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;

import java.lang.ref.SoftReference;

public class BeanDefineSoftReference extends BeanDefineAbstract {

    protected SoftReference<Object> softReference;

    private Class<?> beanType;

    public BeanDefineSoftReference(Class<?> beanType, Object beanObject) {
        this(beanType, null, beanObject);
    }

    public BeanDefineSoftReference(Class<?> beanType, String beanName, Object beanObject) {
        this.beanType = beanType;
        this.beanName = BeanDefineType.getBeanName(beanName, beanObject.getClass());
        this.softReference = new SoftReference<Object>(beanObject);
    }

    @Override
    public Class<?> getBeanType() {
        return beanType;
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        return softReference.get();
    }

    @Override
    public BeanScope getBeanScope() {
        return BeanScope.SOFTREFERENCE;
    }

    @Override
    public Object getBeanComponent() {
        return null;
    }
}
