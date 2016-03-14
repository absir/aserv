/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-6-17 上午11:34:15
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;

/**
 * @author absir
 */
public class BeanDefineWrapper implements BeanDefine {

    /**
     * beanDefine
     */
    protected BeanDefine beanDefine;

    /**
     * @param beanDefine
     */
    public BeanDefineWrapper(BeanDefine beanDefine) {
        this.beanDefine = beanDefine;
    }

    /**
     * @return
     */
    public BeanDefine getBeanDefine() {
        return beanDefine;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return beanDefine.hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return beanDefine.equals(obj);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.android.bean.value.IBeanDefine#getBeanType()
     */
    @Override
    public Class<?> getBeanType() {
        return beanDefine.getBeanType();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.android.bean.value.IBeanDefine#getBeanName()
     */
    @Override
    public String getBeanName() {
        return beanDefine.getBeanName();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.android.bean.value.IBeanDefine#getBeanScope()
     */
    @Override
    public BeanScope getBeanScope() {
        return beanDefine.getBeanScope();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.android.bean.value.IBeanDefine#getBeanComponent()
     */
    @Override
    public Object getBeanComponent() {
        return beanDefine.getBeanComponent();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanDefine#getBeanObject(com.absir.bean.basis.
     * BeanFactory)
     */
    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        return getBeanObject(beanFactory, this, this);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanDefine#getBeanObject(com.absir.bean.basis.
     * BeanFactory, com.absir.bean.basis.BeanDefine,
     * com.absir.bean.basis.BeanDefine)
     */
    @Override
    public Object getBeanObject(BeanFactory beanFactory, BeanDefine beanDefineRoot, BeanDefine beanDefineWrapper) {
        return beanDefine.getBeanObject(beanFactory, beanDefineRoot, this);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanDefine#getBeanProxy(java.lang.Object,
     * com.absir.bean.basis.BeanDefine, com.absir.bean.basis.BeanFactory)
     */
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
