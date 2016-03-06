/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-22 下午2:56:18
 */
package com.absir.context.core.compare;

/**
 * @author absir
 * 
 */
@SuppressWarnings("unchecked")
public abstract class CompareAbstract<V, C> {

	/**
	 * @param obj
	 * @return
	 */
	public final C getCompare(Object value) {
		return value == null ? null : (C) getCompareValue((V) value);
	}

	/**
	 * @param value
	 * @return
	 */
	protected abstract C getCompareValue(V value);

	/**
	 * @param compare
	 * @param value
	 * @return
	 */
	public final boolean compareTo(C compare, V value) {
		return compare == value || (compare != null && value != null && compareValue(compare, value));
	}

	/**
	 * @param compare
	 * @param value
	 * @return
	 */
	protected abstract boolean compareValue(C compare, V value);
}
