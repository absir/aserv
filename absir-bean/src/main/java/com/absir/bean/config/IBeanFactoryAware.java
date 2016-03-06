/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-17 下午4:36:51
 */
package com.absir.bean.config;

import com.absir.bean.core.BeanFactoryImpl;
import com.absir.core.kernel.KernelList.Orderable;

/**
 * @author absir
 * 
 */
public interface IBeanFactoryAware extends Orderable {

	/**
	 * @param beanFactory
	 */
	public void beforeRegister(BeanFactoryImpl beanFactory);

	/**
	 * @param beanFactory
	 */
	public void afterRegister(BeanFactoryImpl beanFactory);
}
