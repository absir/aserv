/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-20 上午10:54:37
 */
package com.absir.server.route;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.core.BeanDefineWrappered;
import com.absir.server.on.OnScope;

/**
 * @author absir
 * 
 */
public class RouteDefine extends BeanDefineWrappered {

	/** onScope */
	private OnScope onScope;

	/**
	 * @param beanDefine
	 * @param onScope
	 */
	public RouteDefine(BeanDefine beanDefine, OnScope onScope) {
		super(beanDefine);
		this.onScope = onScope;
	}

	/**
	 * @return the onScope
	 */
	public OnScope getOnScope() {
		return onScope;
	}
}
