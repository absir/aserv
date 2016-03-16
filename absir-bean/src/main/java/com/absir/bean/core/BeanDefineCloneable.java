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

public class BeanDefineCloneable extends BeanDefineAbstract {

    CloneTemplate<?> beanObject;

    public BeanDefineCloneable(CloneTemplate<?> beanObject) {
        this(null, beanObject);
    }

    public BeanDefineCloneable(String beanName, CloneTemplate<?> beanObject) {
        this.beanName = BeanDefineType.getBeanName(beanName, beanObject.getClass());
        this.beanObject = beanObject;
    }

    @Override
    public Class<?> getBeanType() {
        return beanObject.getClass();
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
