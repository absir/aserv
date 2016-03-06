/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-21 下午2:05:23
 */
package com.absir.aop;

import java.util.List;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public interface AopProxy {

	/**
	 * @return
	 */
	public Class<?> getBeanType();

	/**
	 * @return
	 */
	public Object getBeanObject();

	/**
	 * @return
	 */
	public List<AopInterceptor> getAopInterceptors();
}
