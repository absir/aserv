/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-22 下午2:43:32
 */
package com.absir.context.core.compare;

/**
 * @author absir
 * 
 */
public class CompareField extends CompareAbstract<Object, Object> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.context.value.CompareAbstract#getCompareValue
	 * (java.lang.Object)
	 */
	@Override
	protected Object getCompareValue(Object value) {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.context.value.CompareAbstract#compareValue(java
	 * .lang.Object, java.lang.Object)
	 */
	@Override
	protected boolean compareValue(Object compare, Object value) {
		return compare.equals(value);
	}
}
