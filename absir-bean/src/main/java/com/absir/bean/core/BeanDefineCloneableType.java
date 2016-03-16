/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-17 下午8:18:24
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.core.kernel.KernelLang.CloneTemplate;

public class BeanDefineCloneableType extends BeanDefineAbstract {

    Class<?> beanType;

    CloneTemplate<?> beanObject;

    public BeanDefineCloneableType(Class<?> beanType, CloneTemplate<?> beanObject) {
        this(beanType, null, beanObject);
    }

    public BeanDefineCloneableType(Class<?> beanType, String beanName, CloneTemplate<?> beanObject) {
        this.beanType = beanType;
        this.beanName = BeanDefineType.getBeanName(beanName, beanObject.getClass());
        this.beanObject = beanObject;
    }

    @Override
    public Class<?> getBeanType() {
        return beanType;
    }

    @Override
    public BeanScope getBeanScope() {
        return BeanScope.PROTOTYPE;
    }

    @Override
    public Object getBeanComponent() {
        return beanObject;
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        return beanObject.clone();
    }
}
