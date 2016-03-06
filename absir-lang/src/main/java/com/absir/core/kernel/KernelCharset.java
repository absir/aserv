/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-10 下午11:01:58
 */
package com.absir.core.kernel;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * @author absir
 * 
 */
public class KernelCharset {

	/** UTF8 */
	public static final Charset UTF8 = Charset.forName("UTF-8");

	/** defaultCharset */
	private static Charset defaultCharset;

	/** defaultEncoder */
	private static CharsetEncoder defaultEncoder;

	/** defaultDecoder */
	private static CharsetDecoder defaultDecoder;

	/**
	 * @return the default
	 */
	public static Charset getDefault() {
		if (defaultCharset == null) {
			defaultCharset = UTF8;
		}

		return defaultCharset;
	}

	/**
	 * @param default
	 *            the default to set
	 */
	public static void setDefault(Charset charset) {
		defaultCharset = charset;
	}

	/**
	 * @return the defaultEncoder
	 */
	public static CharsetEncoder getDefaultEncoder() {
		if (defaultEncoder == null) {
			defaultEncoder = getDefault().newEncoder();
		}

		return defaultEncoder;
	}

	/**
	 * @return the defaultDecoder
	 */
	public static CharsetDecoder getDefaultDecoder() {
		if (defaultDecoder == null) {
			defaultDecoder = getDefault().newDecoder();
		}

		return defaultDecoder;
	}
}
