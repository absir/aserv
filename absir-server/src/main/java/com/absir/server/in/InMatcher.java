/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-12 下午3:19:01
 */
package com.absir.server.in;

/**
 * @author absir
 * 
 */
public abstract class InMatcher {

	/** mapping */
	private byte[] mapping;

	/** suffix */
	private byte[] suffix;

	/**
	 * @param mapping
	 * @param suffix
	 */
	public InMatcher(String mapping, String suffix) {
		this.mapping = mapping.getBytes();
		this.suffix = suffix == null ? null : suffix.getBytes();
	}

	/**
	 * @return the mapping
	 */
	public byte[] getMapping() {
		return mapping;
	}

	/**
	 * @return the suffix
	 */
	public byte[] getSuffix() {
		return suffix;
	}

	/**
	 * @return
	 */
	public int getSuffixLength() {
		return suffix == null ? 0 : suffix.length;
	}

	/**
	 * @return
	 */
	public abstract int getParameterLength();
}
