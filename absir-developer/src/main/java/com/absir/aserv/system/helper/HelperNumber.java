/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.system.helper;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author absir
 * 
 */
public class HelperNumber extends NumberUtils {

	/**
	 * @param a
	 * @param aE
	 * @param b
	 * @param bE
	 * @return
	 */
	public static boolean isNoCross(int a, int aE, int b, int bE) {
		return (a < b && aE < b) || (a > bE && aE > bE);
	}
}
