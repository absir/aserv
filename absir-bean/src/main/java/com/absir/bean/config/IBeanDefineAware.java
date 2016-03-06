/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-14 上午9:39:50
 */
package com.absir.bean.config;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.core.kernel.KernelList.Orderable;

/**
 * @author absir
 * 
 */
public interface IBeanDefineAware extends Orderable {

	/**
	 * @param beanFactory
	 * @param beanDefine
	 */
	public void registerBeanDefine(BeanFactoryImpl beanFactory, BeanDefine beanDefine);

	/**
	 * @param beanFactory
	 * @param beanDefine
	 */
	public void unRegisterBeanDefine(BeanFactoryImpl beanFactory, BeanDefine beanDefine);

	/**
	 * @param beanFactory
	 * @param beanDefine
	 */
	public void replaceRegisterBeanDefine(BeanFactoryImpl beanFactory, BeanDefine beanDefine);
}
