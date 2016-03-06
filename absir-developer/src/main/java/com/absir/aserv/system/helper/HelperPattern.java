/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-11 上午11:32:35
 */
package com.absir.aserv.system.helper;

import java.util.regex.Pattern;

import com.absir.core.kernel.KernelString;

/**
 * @author absir
 * 
 */
public class HelperPattern {

	/** EMAIL_PATTERN */
	public static final Pattern EMAIL_PATTERN = Pattern.compile("[\\w]+@[\\w]+");

	public static boolean isEmail(String input) {
		return !KernelString.isEmpty(input) && EMAIL_PATTERN.matcher(input).find();
	}

	/** MOBILE_PATTERN */
	public static final Pattern MOBILE_PATTERN = Pattern.compile("1[0-9]{10}");

	/**
	 * @param input
	 * @return
	 */
	public static boolean isMobilePhone(String input) {
		return !KernelString.isEmpty(input) && MOBILE_PATTERN.matcher(input).find();
	}
}
