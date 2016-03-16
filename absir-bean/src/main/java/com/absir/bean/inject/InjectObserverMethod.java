/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-17 下午8:20:48
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.core.BeanDefineMethod;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.bean.inject.value.InjectType;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelDyna;

public class InjectObserverMethod extends InjectObserver {

    InjectMethod injectMethod;

    Class<?>[] parameterTypes;

    Class<?>[] beanTypes;

    public InjectObserverMethod(InjectMethod injectMethod) {
        this.injectMethod = injectMethod;
        parameterTypes = injectMethod.method.getParameterTypes();
        int length = parameterTypes.length;
        beanTypes = new Class<?>[length];
        for (int i = 0; i < length; i++) {
            beanTypes[i] = BeanFactoryImpl.getBeanType(parameterTypes[i]);
        }
    }

    @Override
    public Object getComponent() {
        return injectMethod.method;
    }

    @Override
    public InjectType getInjectType() {
        return injectMethod.injectType;
    }

    @Override
    protected boolean support(BeanDefine beanDefine) {
        return KernelClass.isAssignableFrom(beanDefine.getBeanType(), beanTypes);
    }

    @Override
    public Object parameter(BeanFactory beanFactory, BeanDefine beanDefine) {
        return injectMethod.parameter(beanFactory, parameterTypes);
    }

    @Override
    public boolean observer(Object beanObject, Object parameter) {
        return KernelDyna.to(BeanDefineMethod.getBeanObject(beanObject, injectMethod.method, (Object[]) parameter), boolean.class);
    }
}
