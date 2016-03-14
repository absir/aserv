/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-8-26 下午1:32:38
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanConfig;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.core.kernel.KernelLang.FilterTemplate;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author absir
 */
public class BeanFactoryWrapper implements BeanFactory {

    /**
     * beanFactory
     */
    private BeanFactory beanFactory;

    /**
     * @param beanFactory
     */
    public BeanFactoryWrapper(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * @return the beanFactory
     */
    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanFactory#getBeanConfig()
     */
    @Override
    public BeanConfig getBeanConfig() {
        return beanFactory.getBeanConfig();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanFactory#getBeanObject(java.lang.String)
     */
    @Override
    public Object getBeanObject(String beanName) {
        return beanFactory.getBeanObject(beanName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanFactory#getBeanObject(java.lang.Class)
     */
    @Override
    public <T> T getBeanObject(Class<T> beanType) {
        return beanFactory.getBeanObject(beanType);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanFactory#getBeanObject(java.lang.String,
     * java.lang.Class)
     */
    @Override
    public <T> T getBeanObject(String beanName, Class<T> beanType) {
        return beanFactory.getBeanObject(beanName, beanType);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanFactory#getBeanObject(java.lang.String,
     * java.lang.Class, boolean)
     */
    @Override
    public <T> T getBeanObject(String beanName, Class<T> beanType, boolean forcible) {
        return beanFactory.getBeanObject(beanName, beanType, forcible);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanFactory#getBeanObject(java.lang.String,
     * java.lang.reflect.Type, boolean)
     */
    @Override
    public Object getBeanObject(String beanName, Type beanType, boolean forcible) {
        return beanFactory.getBeanObject(beanName, beanType, forcible);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanFactory#getBeanObjects(java.lang.Class)
     */
    @Override
    public <T> List<T> getBeanObjects(Class<T> beanType) {
        return beanFactory.getBeanObjects(beanType);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanFactory#getBeanDefine(java.lang.String)
     */
    @Override
    public BeanDefine getBeanDefine(String beanName) {
        return beanFactory.getBeanDefine(beanName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanFactory#getBeanDefine(java.lang.String,
     * java.lang.Class)
     */
    @Override
    public BeanDefine getBeanDefine(String beanName, Class<?> beanType) {
        return beanFactory.getBeanDefine(beanName, beanType);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanFactory#getBeanDefines(java.lang.Class)
     */
    @Override
    public List<BeanDefine> getBeanDefines(Class<?> beanType) {
        return beanFactory.getBeanDefines(beanType);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanFactory#getBeanDefineMap(java.lang.Class)
     */
    @Override
    public Map<String, BeanDefine> getBeanDefineMap(Class<?> beanType) {
        return beanFactory.getBeanDefineMap(beanType);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#processBeanDefine(com.absir.bean.basis
     * .BeanDefine)
     */
    @Override
    public BeanDefine processBeanDefine(BeanDefine beanDefine) {
        return beanFactory.processBeanDefine(beanDefine);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#processBeanObject(com.absir.bean.basis
     * .BeanScope, com.absir.bean.basis.BeanDefine, java.lang.Object)
     */
    @Override
    public void processBeanObject(BeanScope beanScope, BeanDefine beanDefine, Object beanObject) {
        beanFactory.processBeanObject(beanScope, beanDefine, beanObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#processBeanObject(com.absir.bean.basis
     * .BeanScope, com.absir.bean.basis.BeanDefine, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void processBeanObject(BeanScope beanScope, BeanDefine beanDefine, Object beanObject, Object beanProxy) {
        beanFactory.processBeanObject(beanScope, beanDefine, beanObject, beanProxy);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#getSoftReferenceBeans(java.lang.Class)
     */
    @Override
    public <T> List<T> getSoftReferenceBeans(Class<T> beanType) {
        return beanFactory.getSoftReferenceBeans(beanType);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#getSoftReferenceBeans(com.absir.core
     * .kernel.KernelLang.FilterTemplate)
     */
    @Override
    public List<Object> getSoftReferenceBeans(FilterTemplate<Object> filter) {
        return beanFactory.getSoftReferenceBeans(filter);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#registerBeanObject(java.lang.Object)
     */
    @Override
    public BeanDefine registerBeanObject(Object beanObject) {
        return beanFactory.registerBeanObject(beanObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#registerBeanObject(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public BeanDefine registerBeanObject(String beanName, Object beanObject) {
        return beanFactory.registerBeanObject(beanName, beanObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#registerBeanObject(java.lang.String,
     * com.absir.bean.basis.BeanScope, java.lang.Object)
     */
    @Override
    public BeanDefine registerBeanObject(String beanName, BeanScope beanScope, Object beanObject) {
        return beanFactory.registerBeanObject(beanName, beanScope, beanObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanFactory#registerBeanObject(java.lang.Class,
     * java.lang.String, com.absir.bean.basis.BeanScope, java.lang.Object)
     */
    @Override
    public BeanDefine registerBeanObject(Class<?> beanType, String beanName, BeanScope beanScope, Object beanObject) {
        return beanFactory.registerBeanObject(beanType, beanName, beanScope, beanObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#registerBeanDefine(com.absir.bean.basis
     * .BeanDefine)
     */
    @Override
    public void registerBeanDefine(BeanDefine beanDefine) {
        beanFactory.registerBeanDefine(beanDefine);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#unRegisterBeanObject(java.lang.Object)
     */
    @Override
    public void unRegisterBeanObject(Object beanObject) {
        beanFactory.unRegisterBeanObject(beanObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#unRegisterBeanObject(java.lang.String)
     */
    @Override
    public void unRegisterBeanObject(String beanName) {
        beanFactory.unRegisterBeanObject(beanName);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#unRegisterBeanObject(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void unRegisterBeanObject(String beanName, Object beanObject) {
        beanFactory.unRegisterBeanObject(beanName, beanObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#unRegisterBeanType(java.lang.Class<?>[])
     */
    @Override
    public void unRegisterBeanType(Class<?>... beanTypes) {
        beanFactory.unRegisterBeanType(beanTypes);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#unRegisterWithoutBeanType(java.lang.
     * Class[])
     */
    @Override
    public void unRegisterWithoutBeanType(Class<?>... beanTypes) {
        beanFactory.unRegisterWithoutBeanType(beanTypes);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#unRegisterBeanDefine(com.absir.bean.
     * basis.BeanDefine)
     */
    @Override
    public void unRegisterBeanDefine(BeanDefine beanDefine) {
        beanFactory.unRegisterBeanDefine(beanDefine);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#registerBeanSoftObject(java.lang.Object)
     */
    @Override
    public void registerBeanSoftObject(Object beanObject) {
        beanFactory.registerBeanSoftObject(beanObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.basis.BeanFactory#unRegisterBeanSoftObject(java.lang.Object
     * )
     */
    @Override
    public void unRegisterBeanSoftObject(Object beanObject) {
        beanFactory.unRegisterBeanSoftObject(beanObject);
    }

}
