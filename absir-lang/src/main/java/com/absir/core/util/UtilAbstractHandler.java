/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.core.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.absir.core.kernel.KernelLang;

/**
 * @author absir
 * 
 */
public abstract class UtilAbstractHandler implements InvocationHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 * java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		String methodName = method.getName();
		if (method.getParameterTypes().length == 0) {
			if (methodName.equals("toString")) {
				return toString();

			} else if (methodName.equals("hashCode")) {
				return hashCode();
			}
		}

		if (method.getParameterTypes().length == 1) {
			if (methodName.equals("equals")) {
				return args.length == 1 ? equals(proxy, args[0]) : equals(proxy, args);
			}
		}

		Object result = invoke(proxy, methodName, args);
		if (result == KernelLang.NULL_OBJECT) {
			result = invokeMethod(proxy, method, args);
		}

		if (result == KernelLang.NULL_OBJECT) {
			new NoSuchMethodException(methodName).printStackTrace();
		}

		return null;
	}

	/**
	 * @param proxy
	 * @param arg
	 * @return
	 */
	protected boolean equals(Object proxy, Object arg) {
		return proxy.equals(arg);
	}

	/**
	 * @param proxy
	 * @param methodName
	 * @param args
	 * @return
	 */
	protected Object invoke(Object proxy, String methodName, Object[] args) {
		return KernelLang.NULL_OBJECT;
	}

	/**
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 */
	protected Object invokeMethod(Object proxy, Method method, Object[] args) {
		return KernelLang.NULL_OBJECT;
	}
}
