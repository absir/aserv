/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-13 下午8:30:53
 */
package com.absir.context.config;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.core.BeanDefineAbstract;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAccessor;

import java.lang.reflect.Type;

public class BeanDefineReference extends BeanDefineAbstract {

    private boolean required;

    private String beanName;

    private String propertyPath;

    public BeanDefineReference(String name, String required) {
        if (name != null) {
            String[] names = name.split("\\.", 2);
            beanName = names[0];
            if (names.length > 1) {
                propertyPath = names[1];
            }
        }

        this.required = KernelDyna.to(required, boolean.class);
    }

    @Override
    public Class<?> getBeanType() {
        return null;
    }

    @Override
    public BeanScope getBeanScope() {
        return null;
    }

    @Override
    public Object getBeanComponent() {
        return null;
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        return getBeanReference(beanFactory, beanName, null);
    }

    public Object getBeanReference(BeanFactory beanFactory, String paramName, Type parameterType) {
        Object beanObject = beanFactory.getBeanObject(KernelString.isEmpty(beanName) ? paramName : beanName, parameterType, required);
        if (!KernelString.isEmpty(propertyPath)) {
            beanObject = UtilAccessor.getAccessorObj(beanObject, propertyPath);
        }

        return beanObject;
    }

    public boolean isRequired() {
        return required;
    }
}
