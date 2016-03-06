/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-18 下午12:57:11
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.inject.value.InjectType;

/**
 * @author absir
 * 
 */
public abstract class InjectInvokerObserver extends InjectInvoker {

	/** injectType */
	InjectType injectType;

	/**
	 * @param injectType
	 */
	public InjectInvokerObserver(InjectType injectType) {
		this.injectType = injectType;
	}

	/**
	 * @param beanFactory
	 * @param beanObject
	 */
	public void invoke(BeanFactory beanFactory, Object beanObject) {
		invokeImpl(beanObject, parameter(beanFactory));
	}

	/**
	 * @param beanFactory
	 * @return
	 */
	protected abstract Object parameter(BeanFactory beanFactory);

	/**
	 * @param beanObject
	 * @param parameter
	 */
	protected abstract void invokeImpl(Object beanObject, Object parameter);

	/**
	 * @return
	 */
	public InjectObserver getInjectObserver() {
		if (injectType == InjectType.ObServed || injectType == InjectType.ObServeRealed) {
			return getInjectObserverImpl();
		}

		return null;
	}

	/**
	 * @param injectType
	 * @return
	 */
	protected InjectObserver getInjectObserverImpl() {
		return null;
	}
}
