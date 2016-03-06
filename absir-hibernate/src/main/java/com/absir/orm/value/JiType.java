/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-29 下午1:45:55
 */
package com.absir.orm.value;

import java.util.Properties;

import org.hibernate.type.Type;

/**
 * @author absir
 * 
 */
public interface JiType {

	/**
	 * @param typeClass
	 * @param parameters
	 * @return
	 */
	public Type byClass(Class<?> typeClass, Properties parameters);
}
