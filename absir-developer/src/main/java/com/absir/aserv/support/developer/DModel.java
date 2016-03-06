/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年7月9日 下午8:42:08
 */
package com.absir.aserv.support.developer;

import java.io.Serializable;

/**
 * @author absir
 *
 */
@SuppressWarnings("serial")
public class DModel implements Serializable {

	/** DEFAULT */
	public static final DModel DEFAULT = new DModel();

	/** filter */
	private boolean filter;

	/** desc */
	private boolean desc;

	/**
	 * @return the filter
	 */
	public boolean isFilter() {
		return filter;
	}

	/**
	 * @param filter
	 *            the filter to set
	 */
	public void setFilter(boolean filter) {
		this.filter = filter;
	}

	/**
	 * @return the desc
	 */
	public boolean isDesc() {
		return desc;
	}

	/**
	 * @param desc
	 *            the desc to set
	 */
	public void setDesc(boolean desc) {
		this.desc = desc;
	}
}
