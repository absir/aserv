/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-14 上午9:39:50
 */
package com.absir.bean.config;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.core.kernel.KernelList.Orderable;

public interface IBeanDefineAware extends Orderable {

    public void registerBeanDefine(BeanFactoryImpl beanFactory, BeanDefine beanDefine);

    public void unRegisterBeanDefine(BeanFactoryImpl beanFactory, BeanDefine beanDefine);

    public void replaceRegisterBeanDefine(BeanFactoryImpl beanFactory, BeanDefine beanDefine);
}
