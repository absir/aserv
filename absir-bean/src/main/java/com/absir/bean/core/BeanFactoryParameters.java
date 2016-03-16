/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-8-26 下午1:38:50
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanFactory;
import com.absir.core.kernel.KernelClass;

@SuppressWarnings("unchecked")
public class BeanFactoryParameters extends BeanFactoryWrapper {

    private Object[] parameters;

    private int parameterIndex;

    public BeanFactoryParameters(BeanFactory beanFactory, Object[] parameters) {
        super(beanFactory);
        this.parameters = parameters;
    }

    public void resetParameterIndex() {
        parameterIndex = 0;
    }

    public void clearParameterIndex() {
        parameterIndex = -1;
    }

    @Override
    public <T> T getBeanObject(Class<T> beanType) {
        int length = parameters.length;
        for (int i = parameterIndex < 0 ? 0 : parameterIndex; i < length; i++) {
            Object parameter = parameters[parameterIndex];
            if (KernelClass.isMatchableFrom(beanType, parameter.getClass())) {
                if (parameterIndex >= 0) {
                    parameterIndex = i;
                }

                return (T) parameter;
            }
        }

        return getBeanFactory().getBeanObject(beanType);
    }
}
