/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-13 下午2:29:06
 */
package com.absir.bean.basis;

public interface BeanDefine {

    public Class<?> getBeanType();

    public String getBeanName();

    public BeanScope getBeanScope();

    public Object getBeanComponent();

    public Object getBeanObject(BeanFactory beanFactory);

    public Object getBeanObject(BeanFactory beanFactory, BeanDefine beanDefineRoot, BeanDefine beanDefineWrapper);

    public Object getBeanProxy(Object beanObject, BeanDefine beanDefineRoot, BeanFactory beanFactory);
}
