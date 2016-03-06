/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-22 下午2:54:42
 */
package com.absir.context.core.compare;

import java.util.Map;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public class CompareMap extends CompareAbstract<Map, ObjectHashSize> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.context.value.CompareAbstract#getCompareValue
	 * (java.lang.Object)
	 */
	@Override
	protected ObjectHashSize getCompareValue(Map value) {
		return new ObjectHashSize(value.hashCode(), value.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.context.value.CompareAbstract#compareValue(java
	 * .lang.Object, java.lang.Object)
	 */
	@Override
	protected boolean compareValue(ObjectHashSize compare, Map value) {
		return compare.size == value.size() && compare.hashCode == value.hashCode();
	}

}
