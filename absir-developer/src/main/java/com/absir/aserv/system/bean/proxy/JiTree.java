/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-1 上午9:36:38
 */
package com.absir.aserv.system.bean.proxy;

import java.util.Collection;

/**
 * @author absir
 * 
 */
public interface JiTree<T> {

	/**
	 * @return
	 */
	public Collection<T> getChildren();
}
