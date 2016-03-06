/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-16 下午4:33:41
 */
package com.absir.server.route;

import com.absir.server.on.OnPut;

/**
 * @author absir
 * 
 */
public class RouteException {

	/** exceptions */
	private Class<? extends Throwable>[] exceptions;

	/** routeMethod */
	private RouteMethod routeMethod;

	/**
	 * @param exceptions
	 * @param routeMethod
	 */
	public RouteException(Class<? extends Throwable>[] exceptions, RouteMethod routeMethod) {
		this.exceptions = exceptions;
		this.routeMethod = routeMethod;
	}

	/**
	 * @return the exceptions
	 */
	public Class<? extends Throwable>[] getExceptions() {
		return exceptions;
	}

	/**
	 * @return the routeMethod
	 */
	public RouteMethod getRouteMethod() {
		return routeMethod;
	}

	/**
	 * @param e
	 * @param routeBean
	 * @param onPut
	 * @return
	 * @throws Throwable
	 */
	public boolean invoke(Throwable e, Object routeBean, OnPut onPut) throws Throwable {
		for (Class<? extends Throwable> exception : exceptions) {
			if (exception.isAssignableFrom(e.getClass())) {
				routeMethod.invoke(routeBean, onPut);
				return true;
			}
		}

		return false;
	}
}
