/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-15 下午12:51:25
 */
package com.absir.server.route.entity;

import com.absir.server.in.Input;
import com.absir.server.route.RouteEntity;

/**
 * @author absir
 * 
 */
public class EntitySingleton extends RouteEntity {

	/** beanObject */
	private Object beanObject;

	/**
	 * @param beanObject
	 */
	public EntitySingleton(Object beanObject) {
		this.beanObject = beanObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.route.RouteEntity#getRouteType()
	 */
	@Override
	public Class<?> getRouteType() {
		return beanObject.getClass();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.server.route.RouteEntity#getRouteBean(com.absir.server.in.Input
	 * )
	 */
	@Override
	public Object getRouteBean(Input input) {
		return beanObject;
	}

}
