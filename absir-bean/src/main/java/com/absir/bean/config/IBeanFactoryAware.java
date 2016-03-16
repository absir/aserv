/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-17 下午4:36:51
 */
package com.absir.bean.config;

import com.absir.bean.core.BeanFactoryImpl;
import com.absir.core.kernel.KernelList.Orderable;

public interface IBeanFactoryAware extends Orderable {

    public void beforeRegister(BeanFactoryImpl beanFactory);

    public void afterRegister(BeanFactoryImpl beanFactory);
}
