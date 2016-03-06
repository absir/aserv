/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年9月10日 下午4:54:30
 */
package com.absir.aserv.advice;

import java.lang.reflect.Method;

import com.absir.aop.AopProxyHandler;

/**
 * @author absir
 *
 */
public abstract class MethodBefore extends MethodAdvice {

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
		advice(proxy, method, args);
		return AopProxyHandler.VOID;
	}

	/**
	 * @param proxy
	 * @param method
	 * @param args
	 */
	public abstract void advice(Object proxy, Method method, Object[] args);

}
