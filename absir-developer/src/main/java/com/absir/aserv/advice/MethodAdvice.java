/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年9月10日 下午4:56:46
 */
package com.absir.aserv.advice;

import java.lang.reflect.Method;

import com.absir.aop.AopProxyHandler;

/**
 * @author absir
 *
 */
public abstract class MethodAdvice implements IMethodAdvice {

	/** order */
	private int order;

	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * @param order
	 *            the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.advice.IMethodAdvice#before(com.absir.aserv.advice
	 * .AdviceInvoker, java.lang.Object, java.lang.reflect.Method,
	 * java.lang.Object[])
	 */
	@Override
	public Object before(AdviceInvoker invoker, Object proxy, Method method, Object[] args) throws Throwable {
		return AopProxyHandler.VOID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.advice.IMethodAdvice#after(java.lang.Object,
	 * java.lang.Object, java.lang.reflect.Method, java.lang.Object[],
	 * java.lang.Throwable)
	 */
	@Override
	public Object after(Object proxy, Object returnValue, Method method, Object[] args, Throwable e) throws Throwable {
		return returnValue;
	}
}
