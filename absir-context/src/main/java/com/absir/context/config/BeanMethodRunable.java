/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-24 下午2:36:16
 */
package com.absir.context.config;

import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.InjectMethod;

public class BeanMethodRunable implements Runnable {

    private Object beanObject;

    private InjectMethod injectMethod;

    public BeanMethodRunable(Object beanObject, InjectMethod injectMethod) {
        this.beanObject = beanObject;
        this.injectMethod = injectMethod;
    }

    public Object getBeanObject() {
        return beanObject;
    }

    public InjectMethod getInjectMethod() {
        return injectMethod;
    }

    @Override
    public void run() {
        injectMethod.invoke(BeanFactoryUtils.get(), beanObject);
    }
}
