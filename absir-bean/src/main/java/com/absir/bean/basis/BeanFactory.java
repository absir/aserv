/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-20 下午3:17:55
 */
package com.absir.bean.basis;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.absir.core.kernel.KernelLang.FilterTemplate;

/**
 * @author absir
 * 
 */
public interface BeanFactory extends BeanSupply {

	/**
	 * @return
	 */
	public BeanConfig getBeanConfig();

	/**
	 * @param beanName
	 * @param beanType
	 * @param forcible
	 * @return
	 */
	public <T> T getBeanObject(String beanName, Class<T> beanType, boolean forcible);

	/**
	 * @param beanName
	 * @param beanType
	 * @param forcible
	 * @return
	 */
	public Object getBeanObject(String beanName, Type beanType, boolean forcible);

	/**
	 * @param beanType
	 * @return
	 */
	public <T> List<T> getBeanObjects(Class<T> beanType);

	/**
	 * @param beanName
	 * @return
	 */
	public BeanDefine getBeanDefine(String beanName);

	/**
	 * @param beanName
	 * @param beanType
	 * @return
	 */
	public BeanDefine getBeanDefine(String beanName, Class<?> beanType);

	/**
	 * @param beanType
	 * @return
	 */
	public List<BeanDefine> getBeanDefines(Class<?> beanType);

	/**
	 * @param beanType
	 * @return
	 */
	public Map<String, BeanDefine> getBeanDefineMap(Class<?> beanType);

	/**
	 * @param beanDefine
	 * @return
	 */
	public BeanDefine processBeanDefine(BeanDefine beanDefine);

	/**
	 * @param beanScope
	 * @param beanDefine
	 * @param beanObject
	 */
	public void processBeanObject(BeanScope beanScope, BeanDefine beanDefine, Object beanObject);

	/**
	 * @param beanScope
	 * @param beanDefine
	 * @param beanObject
	 * @param beanProxy
	 */
	public void processBeanObject(BeanScope beanScope, BeanDefine beanDefine, Object beanObject, Object beanProxy);

	/**
	 * @param beanType
	 * @return
	 */
	public <T> List<T> getSoftReferenceBeans(Class<T> beanType);

	/**
	 * @param filter
	 * @return
	 */
	public List<Object> getSoftReferenceBeans(FilterTemplate<Object> filter);

	/**
	 * @param beanObject
	 * @return
	 */
	public BeanDefine registerBeanObject(Object beanObject);

	/**
	 * @param beanName
	 * @param beanObject
	 * @return
	 */
	public BeanDefine registerBeanObject(String beanName, Object beanObject);

	/**
	 * @param beanName
	 * @param beanScope
	 * @param beanObject
	 * @return
	 */
	public BeanDefine registerBeanObject(String beanName, BeanScope beanScope, Object beanObject);

	/**
	 * @param beanType
	 * @param beanName
	 * @param beanScope
	 * @param beanObject
	 * @return
	 */
	public BeanDefine registerBeanObject(Class<?> beanType, String beanName, BeanScope beanScope, Object beanObject);

	/**
	 * @param beanDefine
	 */
	public void registerBeanDefine(BeanDefine beanDefine);

	/**
	 * @param beanObject
	 */
	public void unRegisterBeanObject(Object beanObject);

	/**
	 * @param beanName
	 */
	public void unRegisterBeanObject(String beanName);

	/**
	 * @param beanName
	 * @param beanObject
	 */
	public void unRegisterBeanObject(String beanName, Object beanObject);

	/**
	 * @param beanTypes
	 */
	public void unRegisterBeanType(Class<?>... beanTypes);

	/**
	 * @param beanTypes
	 */
	public void unRegisterWithoutBeanType(Class<?>... beanTypes);

	/**
	 * @param beanDefine
	 */
	public void unRegisterBeanDefine(BeanDefine beanDefine);

	/**
	 * @param beanObject
	 */
	public void registerBeanSoftObject(Object beanObject);

	/**
	 * @param beanObject
	 */
	public void unRegisterBeanSoftObject(Object beanObject);
}
