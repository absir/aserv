/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-17 上午11:34:15
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;

public class BeanDefineWrapper implements BeanDefine {

    protected BeanDefine beanDefine;

    public BeanDefineWrapper(BeanDefine beanDefine) {
        this.beanDefine = beanDefine;
    }

    public BeanDefine getBeanDefine() {
        return beanDefine;
    }

    @Override
    public int hashCode() {
        return beanDefine.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return beanDefine.equals(obj);
    }

    @Override
    public Class<?> getBeanType() {
        return beanDefine.getBeanType();
    }

    @Override
    public String getBeanName() {
        return beanDefine.getBeanName();
    }

    @Override
    public BeanScope getBeanScope() {
        return beanDefine.getBeanScope();
    }

    @Override
    public Object getBeanComponent() {
        return beanDefine.getBeanComponent();
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        return getBeanObject(beanFactory, this, this);
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory, BeanDefine beanDefineRoot, BeanDefine beanDefineWrapper) {
        return beanDefine.getBeanObject(beanFactory, beanDefineRoot, this);
    }

    @Override
    public Object getBeanProxy(Object beanObject, BeanDefine beanDefineRoot, BeanFactory beanFactory) {
        return beanDefine.getBeanProxy(beanObject, beanDefineRoot, beanFactory);
    }

    /**
     * Retrench(简化)BeanDefine
     *
     * @return
     */
    public BeanDefine retrenchBeanDefine() {
        if (beanDefine instanceof BeanDefineWrapper) {
            beanDefine = ((BeanDefineWrapper) beanDefine).retrenchBeanDefine();
        }

        return this;
    }
}
