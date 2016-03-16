/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-17 下午8:20:34
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.bean.inject.value.InjectType;

public class InjectObserverField extends InjectObserver {

    InjectField injectField;

    Class<?> beanType;

    public InjectObserverField(InjectField injectField) {
        this.injectField = injectField;
        beanType = BeanFactoryImpl.getBeanType(injectField.field.getGenericType());
    }

    @Override
    public Object getComponent() {
        return injectField.field;
    }

    @Override
    public InjectType getInjectType() {
        return injectField.injectType;
    }

    @Override
    protected boolean support(BeanDefine beanDefine) {
        return beanType.isAssignableFrom(beanDefine.getBeanType());
    }

    @Override
    public Object parameter(BeanFactory beanFactory, BeanDefine beanDefine) {
        return beanFactory.getBeanObject(injectField.value, injectField.field.getGenericType(), false);
    }

    @Override
    public boolean observer(Object beanObject, Object parameter) {
        injectField.invokeImpl(beanObject, parameter);
        return false;
    }
}
