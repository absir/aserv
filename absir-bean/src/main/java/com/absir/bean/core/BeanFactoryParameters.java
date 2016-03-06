/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-8-26 下午1:38:50
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanFactory;
import com.absir.core.kernel.KernelClass;

/**
 * @author absir
 * 
 */
@SuppressWarnings("unchecked")
public class BeanFactoryParameters extends BeanFactoryWrapper {

	/** parameters */
	private Object[] parameters;

	/** parameterIndex */
	private int parameterIndex;

	/**
	 * @param beanFactory
	 */
	public BeanFactoryParameters(BeanFactory beanFactory, Object[] parameters) {
		super(beanFactory);
		this.parameters = parameters;
	}

	/**
	 * 
	 */
	public void resetParameterIndex() {
		parameterIndex = 0;
	}

	/**
	 * 
	 */
	public void clearParameterIndex() {
		parameterIndex = -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.core.BeanFactoryWrapper#getBeanObject(java.lang.String,
	 * java.lang.Class, boolean)
	 */
	@Override
	public <T> T getBeanObject(Class<T> beanType) {
		int length = parameters.length;
		for (int i = parameterIndex < 0 ? 0 : parameterIndex; i < length; i++) {
			Object parameter = parameters[parameterIndex];
			if (KernelClass.isMatchableFrom(beanType, parameter.getClass())) {
				if (parameterIndex >= 0) {
					parameterIndex = i;
				}

				return (T) parameter;
			}
		}

		return getBeanFactory().getBeanObject(beanType);
	}
}
