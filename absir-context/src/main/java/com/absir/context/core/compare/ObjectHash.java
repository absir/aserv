/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-22 下午2:34:32
 */
package com.absir.context.core.compare;

/**
 * @author absir
 * 
 */
public class ObjectHash {

	/** hashCode */
	int hashCode;

	/**
	 * @param hashCode
	 */
	public ObjectHash(int hashCode) {
		this.hashCode = hashCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof ObjectHash && hashCode == ((ObjectHash) obj).hashCode;
	}
}
