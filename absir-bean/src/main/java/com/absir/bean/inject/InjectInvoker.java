/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-19 下午2:42:59
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanFactory;
import com.absir.core.kernel.KernelList.Orderable;

/**
 * @author absir
 * 
 */
public abstract class InjectInvoker implements Orderable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
	 */
	@Override
	public int getOrder() {
		return 0;
	}

	/**
	 * @param beanFactory
	 * @param beanObject
	 */
	public abstract void invoke(BeanFactory beanFactory, Object beanObject);
}
