/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-13 下午4:47:43
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;

public class BeanDefineSingletonType extends BeanDefineAbstract {

    protected Class<?> beanType;

    protected Object beanObject;

    public BeanDefineSingletonType(Class<?> beanType, Object beanObject) {
        this(beanType, null, beanObject);
    }

    public BeanDefineSingletonType(Class<?> beanType, String beanName, Object beanObject) {
        this.beanType = beanType;
        this.beanName = BeanDefineType.getBeanName(beanName, beanObject.getClass());
        this.beanObject = beanObject;
    }

    @Override
    public Class<?> getBeanType() {
        return beanType;
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        return beanObject;
    }

    @Override
    public BeanScope getBeanScope() {
        return BeanScope.SINGLETON;
    }

    @Override
    public Object getBeanComponent() {
        return null;
    }
}
