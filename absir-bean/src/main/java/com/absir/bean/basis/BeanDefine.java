/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-13 下午2:29:06
 */
package com.absir.bean.basis;

/**
 * @author absir
 * 
 */
public interface BeanDefine {

	/**
	 * @return
	 */
	public Class<?> getBeanType();

	/**
	 * @return
	 */
	public String getBeanName();

	/**
	 * @return
	 */
	public BeanScope getBeanScope();

	/**
	 * @return
	 */
	public Object getBeanComponent();

	/**
	 * @param beanFactory
	 * @return
	 */
	public Object getBeanObject(BeanFactory beanFactory);

	/**
	 * @param beanFactory
	 * @param beanDefineRoot
	 * @param beanDefineWrapper
	 * @return
	 */
	public Object getBeanObject(BeanFactory beanFactory, BeanDefine beanDefineRoot, BeanDefine beanDefineWrapper);

	/**
	 * @param beanObject
	 * @param beanDefineRoot
	 * @param beanFactory
	 * @return
	 */
	public Object getBeanProxy(Object beanObject, BeanDefine beanDefineRoot, BeanFactory beanFactory);
}
