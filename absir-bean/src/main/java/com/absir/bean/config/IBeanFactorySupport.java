/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-14 上午11:00:59
 */
package com.absir.bean.config;

import com.absir.bean.core.BeanFactoryImpl;

/**
 * @author absir
 * 
 */
public interface IBeanFactorySupport {

	/**
	 * @param beanFactory
	 * @return
	 */
	public boolean supports(BeanFactoryImpl beanFactory);

}
