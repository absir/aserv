/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-15 下午12:10:49
 */
package com.absir.server.route;

import com.absir.server.in.Input;

/**
 * @author absir
 * 
 */
public abstract class RouteEntity {

	/** route */
	Object route;

	/** routeEntry */
	RouteEntry routeEntry;

	/**
	 * @return the route
	 */
	public Object getRoute() {
		return route;
	}

	/**
	 * @return the routeEntry
	 */
	public RouteEntry getRouteEntry() {
		return routeEntry;
	}

	/**
	 * @return
	 */
	public abstract Class<?> getRouteType();

	/**
	 * @param input
	 * @return
	 */
	public abstract Object getRouteBean(Input input);
}
