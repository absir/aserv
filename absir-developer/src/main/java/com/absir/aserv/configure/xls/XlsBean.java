/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-27 下午12:00:21
 */
package com.absir.aserv.configure.xls;

import java.io.Serializable;

/**
 * @author absir
 * 
 */
@SuppressWarnings("unchecked")
public class XlsBean<T extends Serializable> extends XlsBase {

	/**
	 * @return the id
	 */
	public T getId() {
		return (T) id;
	}
}
