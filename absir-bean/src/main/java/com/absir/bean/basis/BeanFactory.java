/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-20 下午3:17:55
 */
package com.absir.bean.basis;

import com.absir.core.kernel.KernelLang.FilterTemplate;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public interface BeanFactory extends BeanSupply {

    public BeanConfig getBeanConfig();

    public <T> T getBeanObject(String beanName, Class<T> beanType, boolean forcible);

    public Object getBeanObject(String beanName, Type beanType, boolean forcible);

    public <T> List<T> getBeanObjects(Class<T> beanType);

    public BeanDefine getBeanDefine(String beanName);

    public BeanDefine getBeanDefine(String beanName, Class<?> beanType);

    public List<BeanDefine> getBeanDefines(Class<?> beanType);

    public Map<String, BeanDefine> getBeanDefineMap(Class<?> beanType);

    public BeanDefine processBeanDefine(BeanDefine beanDefine);

    public void processBeanObject(BeanScope beanScope, BeanDefine beanDefine, Object beanObject);

    public void processBeanObject(BeanScope beanScope, BeanDefine beanDefine, Object beanObject, Object beanProxy);

    public <T> List<T> getSoftReferenceBeans(Class<T> beanType);

    public List<Object> getSoftReferenceBeans(FilterTemplate<Object> filter);

    public BeanDefine registerBeanObject(Object beanObject);

    public BeanDefine registerBeanObject(String beanName, Object beanObject);

    public BeanDefine registerBeanObject(String beanName, BeanScope beanScope, Object beanObject);

    public BeanDefine registerBeanObject(Class<?> beanType, String beanName, BeanScope beanScope, Object beanObject);

    public void registerBeanDefine(BeanDefine beanDefine);

    public void unRegisterBeanObject(Object beanObject);

    public void unRegisterBeanObject(String beanName);

    public void unRegisterBeanObject(String beanName, Object beanObject);

    public void unRegisterBeanType(Class<?>... beanTypes);

    public void unRegisterWithoutBeanType(Class<?>... beanTypes);

    public void unRegisterBeanDefine(BeanDefine beanDefine);

    public void registerBeanSoftObject(Object beanObject);

    public void unRegisterBeanSoftObject(Object beanObject);
}
