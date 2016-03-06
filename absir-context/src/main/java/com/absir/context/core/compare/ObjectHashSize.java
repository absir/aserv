/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-22 下午2:34:41
 */
package com.absir.context.core.compare;

/**
 * @author absir
 * 
 */
public class ObjectHashSize extends ObjectHash {

	/** size */
	int size;

	/**
	 * @param hashCode
	 * @param size
	 */
	public ObjectHashSize(int hashCode, int size) {
		super(hashCode);
		this.size = size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashCode + size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof ObjectHashSize && hashCode == ((ObjectHash) obj).hashCode && size == ((ObjectHashSize) obj).size;
	}
}
