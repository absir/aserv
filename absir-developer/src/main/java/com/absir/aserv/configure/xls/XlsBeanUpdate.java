/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-29 上午11:03:12
 */
package com.absir.aserv.configure.xls;

import com.absir.aserv.system.bean.proxy.JiUpdate;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * @author absir
 * 
 */
public class XlsBeanUpdate<T extends Serializable> extends XlsBean<T> implements JiUpdate {

	/** updateTime */
	protected transient long updateTime;

	/**
	 * @return the updateTime
	 */
	@JsonIgnore
	public long getUpdateTime() {
		return updateTime;
	}
}
