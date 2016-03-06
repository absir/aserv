/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-24 下午3:57:00
 */
package com.absir.aop;

import java.lang.reflect.Method;

import com.absir.bean.basis.BeanDefine;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AopMethodDefineAbstract<T extends AopInterceptorAbstract, K, D> implements AopMethodDefine<T, K, D> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aop.AopMethodDefine#getVariable(com.absir.aop.AopInterceptor,
	 * com.absir.bean.basis.BeanDefine, java.lang.Object)
	 */
	@Override
	public D getVariable(T aopInterceptor, BeanDefine beanDefine, Object beanObject) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aop.AopMethodDefine#isEmpty(com.absir.aop.AopInterceptor)
	 */
	@Override
	public boolean isEmpty(T aopInterceptor) {
		return !aopInterceptor.setunmodifiableMethodMapInterceptor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aop.AopMethodDefine#setAopInterceptor(java.lang.Object,
	 * com.absir.aop.AopInterceptor, java.lang.Class, java.lang.reflect.Method,
	 * java.lang.reflect.Method)
	 */
	@Override
	public void setAopInterceptor(K interceptor, T aopInterceptor, Class<?> beanType, Method method, Method beanMethod) {
		aopInterceptor.getMethodMapInterceptor().put(beanMethod, interceptor);
	}
}
