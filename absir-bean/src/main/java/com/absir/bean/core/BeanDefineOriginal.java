/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-27 下午5:37:41
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;

import java.lang.reflect.Modifier;

public class BeanDefineOriginal extends BeanDefineWrappered {

    private Object beanComponent;

    private Object beanObject;

    public BeanDefineOriginal(BeanDefine beanDefine) {
        super(beanDefine);
        beanComponent = beanDefine.getBeanComponent();
    }

    public static boolean isAbstractBeanType(Class<?> beanType) {
        return beanType.isInterface() || Modifier.isAbstract(beanType.getModifiers());
    }

    @Override
    public Object getBeanComponent() {
        return beanComponent;
    }

    @Override
    public BeanScope getBeanScope() {
        return BeanScope.SINGLETON;
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        if (beanObject == null) {
            Object bean = beanDefine.getBeanObject(beanFactory);
            if (beanObject == null) {
                beanObject = bean;
                beanDefine = BeanDefineAbstract.getBeanDefine(getBeanType(), getBeanName(), beanObject, getBeanScope(), beanDefine);
            }
        }

        return beanObject;
    }
}