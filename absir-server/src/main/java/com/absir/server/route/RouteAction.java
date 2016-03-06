/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-24 上午9:53:45
 */
package com.absir.server.route;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author absir
 * 
 */
public class RouteAction {

	/** urlDecode */
	private boolean urlDecode;

	/** routeEntity */
	private RouteEntity routeEntity;

	/** routeEntry */
	private RouteEntry routeEntry;

	/** routeMethod */
	private RouteMethod routeMethod;

	/** routeView */
	private String routeView;

	/**
	 * @param urlDecode
	 * @param routeEntity
	 * @param routeEntry
	 * @param routeMethod
	 * @param parameterPathNames
	 * @param parameterPathIndexs
	 * @param parameterAnnotations
	 */
	public RouteAction(boolean urlDecode, RouteEntity routeEntity, RouteEntry routeEntry, RouteMethod routeMethod, String[] parameterPathNames, List<Integer> parameterPathIndexs,
			List<Annotation[]> parameterAnnotations) {
		this.urlDecode = urlDecode;
		this.routeEntity = routeEntity;
		this.routeEntry = routeEntry;
		this.routeMethod = routeMethod;
	}

	/**
	 * @return the urlDecode
	 */
	public boolean isUrlDecode() {
		return urlDecode;
	}

	/**
	 * @return the routeEntity
	 */
	public RouteEntity getRouteEntity() {
		return routeEntity;
	}

	/**
	 * @return the routeEntry
	 */
	public RouteEntry getRouteEntry() {
		return routeEntry;
	}

	/**
	 * @return the routeMethod
	 */
	public RouteMethod getRouteMethod() {
		return routeMethod;
	}

	/**
	 * @return the routeView
	 */
	public String getRouteView() {
		return routeView;
	}

	/**
	 * @param routeView
	 *            the routeView to set
	 */
	public void setRouteView(String routeView) {
		this.routeView = routeView;
	}
}
