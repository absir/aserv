/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-5-14 上午10:36:41
 */
package com.absir.context.core;

import java.io.Serializable;

/**
 * @author absir
 * 
 */
public abstract class ContextBeano<ID extends Serializable> extends ContextBean<ID> {

	/** keyClass */
	Class<?> keyClass;

	/**
	 * @return
	 */
	public Class<?> getKeyClass() {
		return keyClass == null ? getClass() : keyClass;
	}

}
