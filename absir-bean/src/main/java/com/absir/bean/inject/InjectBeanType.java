/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月24日 下午3:21:18
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanFactory;
import com.absir.core.kernel.KernelClass;

public class InjectBeanType extends InjectInvoker {

    private Class<?> beanType;

    public InjectBeanType(Class<?> beanType) {
        this.beanType = beanType;
    }

    @Override
    public void invoke(BeanFactory beanFactory, Object beanObject) {
        KernelClass.forName(beanType.getName());
    }
}
