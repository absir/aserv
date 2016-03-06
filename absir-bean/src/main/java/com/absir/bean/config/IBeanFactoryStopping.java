/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-9 下午4:00:53
 */
package com.absir.bean.config;

import com.absir.bean.basis.BeanFactory;
import com.absir.core.kernel.KernelList.Orderable;

/**
 * @author absir
 * 
 */
public interface IBeanFactoryStopping extends Orderable {

	/**
	 * @param beanFactory
	 */
	public void stopping(BeanFactory beanFactory);

}
