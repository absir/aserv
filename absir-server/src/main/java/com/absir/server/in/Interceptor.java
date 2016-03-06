/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-24 上午9:33:27
 */
package com.absir.server.in;

import java.util.Iterator;

import com.absir.server.on.OnPut;

/**
 * @author absir
 * 
 */
public interface Interceptor {

	/**
	 * @param iterator
	 * @param input
	 * @return
	 * @throws Throwable
	 */
	public OnPut intercept(Iterator<Interceptor> iterator, Input input) throws Throwable;
}
