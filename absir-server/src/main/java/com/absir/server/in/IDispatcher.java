/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-16 下午8:19:28
 */
package com.absir.server.in;

import com.absir.server.on.OnPut;

/**
 * @author absir
 * 
 */
public interface IDispatcher<T> {

	/**
	 * @param req
	 * @return
	 */
	public InMethod getInMethod(T req);

	/**
	 * @param uri
	 * @param req
	 * @return
	 */
	public String decodeUri(String uri, T req);

	/**
	 * @param input
	 * @param routeBean
	 * @return
	 */
	public OnPut onPut(Input input, Object routeBean);

	/**
	 * @param routeBean
	 * @param onPut
	 * @throws Exception
	 */
	public void resolveReturnedValue(Object routeBean, OnPut onPut) throws Throwable;

	/**
	 * @param e
	 * @param routeBean
	 * @param onPut
	 * @return
	 * @throws Exception
	 */
	public boolean returnThrowable(Throwable e, Object routeBean, OnPut onPut) throws Throwable;
}
