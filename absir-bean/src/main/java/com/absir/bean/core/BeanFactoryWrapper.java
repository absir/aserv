/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
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

public class BeanFactoryWrapper implements BeanFactory {

    private BeanFactory beanFactory;

    public BeanFactoryWrapper(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    public BeanConfig getBeanConfig() {
        return beanFactory.getBeanConfig();
    }

    @Override
    public Object getBeanObject(String beanName) {
        return beanFactory.getBeanObject(beanName);
    }

    @Override
    public <T> T getBeanObject(Class<T> beanType) {
        return beanFactory.getBeanObject(beanType);
    }

    @Override
    public <T> T getBeanObject(String beanName, Class<T> beanType) {
        return beanFactory.getBeanObject(beanName, beanType);
    }

    @Override
    public <T> T getBeanObject(String beanName, Class<T> beanType, boolean forcible) {
        return beanFactory.getBeanObject(beanName, beanType, forcible);
    }

    @Override
    public Object getBeanObject(String beanName, Type beanType, boolean forcible) {
        return beanFactory.getBeanObject(beanName, beanType, forcible);
    }

    @Override
    public <T> List<T> getBeanObjects(Class<T> beanType) {
        return beanFactory.getBeanObjects(beanType);
    }

    @Override
    public BeanDefine getBeanDefine(String beanName) {
        return beanFactory.getBeanDefine(beanName);
    }

    @Override
    public BeanDefine getBeanDefine(String beanName, Class<?> beanType) {
        return beanFactory.getBeanDefine(beanName, beanType);
    }

    @Override
    public List<BeanDefine> getBeanDefines(Class<?> beanType) {
        return beanFactory.getBeanDefines(beanType);
    }

    @Override
    public Map<String, BeanDefine> getBeanDefineMap(Class<?> beanType) {
        return beanFactory.getBeanDefineMap(beanType);
    }

    @Override
    public BeanDefine processBeanDefine(BeanDefine beanDefine) {
        return beanFactory.processBeanDefine(beanDefine);
    }

    @Override
    public void processBeanObject(BeanScope beanScope, BeanDefine beanDefine, Object beanObject) {
        beanFactory.processBeanObject(beanScope, beanDefine, beanObject);
    }

    @Override
    public void processBeanObject(BeanScope beanScope, BeanDefine beanDefine, Object beanObject, Object beanProxy) {
        beanFactory.processBeanObject(beanScope, beanDefine, beanObject, beanProxy);
    }

    @Override
    public <T> List<T> getSoftReferenceBeans(Class<T> beanType) {
        return beanFactory.getSoftReferenceBeans(beanType);
    }

    @Override
    public List<Object> getSoftReferenceBeans(FilterTemplate<Object> filter) {
        return beanFactory.getSoftReferenceBeans(filter);
    }

    @Override
    public BeanDefine registerBeanObject(Object beanObject) {
        return beanFactory.registerBeanObject(beanObject);
    }

    @Override
    public BeanDefine registerBeanObject(String beanName, Object beanObject) {
        return beanFactory.registerBeanObject(beanName, beanObject);
    }

    @Override
    public BeanDefine registerBeanObject(String beanName, BeanScope beanScope, Object beanObject) {
        return beanFactory.registerBeanObject(beanName, beanScope, beanObject);
    }

    @Override
    public BeanDefine registerBeanObject(Class<?> beanType, String beanName, BeanScope beanScope, Object beanObject) {
        return beanFactory.registerBeanObject(beanType, beanName, beanScope, beanObject);
    }

    @Override
    public void registerBeanDefine(BeanDefine beanDefine) {
        beanFactory.registerBeanDefine(beanDefine);
    }

    @Override
    public void unRegisterBeanObject(Object beanObject) {
        beanFactory.unRegisterBeanObject(beanObject);
    }

    @Override
    public void unRegisterBeanObject(String beanName) {
        beanFactory.unRegisterBeanObject(beanName);
    }

    @Override
    public void unRegisterBeanObject(String beanName, Object beanObject) {
        beanFactory.unRegisterBeanObject(beanName, beanObject);
    }

    @Override
    public void unRegisterBeanType(Class<?>... beanTypes) {
        beanFactory.unRegisterBeanType(beanTypes);
    }

    @Override
    public void unRegisterWithoutBeanType(Class<?>... beanTypes) {
        beanFactory.unRegisterWithoutBeanType(beanTypes);
    }

    @Override
    public void unRegisterBeanDefine(BeanDefine beanDefine) {
        beanFactory.unRegisterBeanDefine(beanDefine);
    }

    @Override
    public void registerBeanSoftObject(Object beanObject) {
        beanFactory.registerBeanSoftObject(beanObject);
    }

    @Override
    public void unRegisterBeanSoftObject(Object beanObject) {
        beanFactory.unRegisterBeanSoftObject(beanObject);
    }

}
