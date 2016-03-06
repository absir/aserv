/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年9月10日 下午4:56:46
 */
package com.absir.aserv.advice;

import java.lang.reflect.Method;

import com.absir.core.kernel.KernelList.Orderable;

/**
 * @author absir
 *
 */
public interface IMethodAdvice extends Orderable {

	/**
	 * @param beanType
	 * @param method
	 * @return
	 */
	public boolean matching(Class<?> beanType, Method method);

	/**
	 * @param invoker
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	public Object before(AdviceInvoker invoker, Object proxy, Method method, Object[] args) throws Throwable;

	/**
	 * @param proxy
	 * @param returnValue
	 * @param method
	 * @param args
	 * @param e
	 * @return
	 * @throws Throwable
	 */
	public Object after(Object proxy, Object returnValue, Method method, Object[] args, Throwable e) throws Throwable;

}
