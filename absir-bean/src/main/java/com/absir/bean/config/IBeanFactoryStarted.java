/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-19 下午2:03:34
 */
package com.absir.bean.config;

import com.absir.bean.basis.BeanFactory;
import com.absir.core.kernel.KernelList.Orderable;

/**
 * @author absir
 * 
 */
public interface IBeanFactoryStarted extends Orderable {

	/**
	 * @param beanFactory
	 */
	public void started(BeanFactory beanFactory);
}
