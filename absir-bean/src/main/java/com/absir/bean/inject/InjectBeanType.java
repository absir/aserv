/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年7月24日 下午3:21:18
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanFactory;
import com.absir.core.kernel.KernelClass;

/**
 * @author absir
 *
 */
public class InjectBeanType extends InjectInvoker {

	/** beanType */
	private Class<?> beanType;

	/**
	 * @param beanType
	 */
	public InjectBeanType(Class<?> beanType) {
		this.beanType = beanType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.inject.InjectInvoker#invoke(com.absir.bean.basis.BeanFactory
	 * , java.lang.Object)
	 */
	@Override
	public void invoke(BeanFactory beanFactory, Object beanObject) {
		KernelClass.forName(beanType.getName());
	}
}
