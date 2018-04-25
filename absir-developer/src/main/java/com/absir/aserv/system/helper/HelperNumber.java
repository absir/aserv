/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.system.helper;

import org.apache.commons.lang3.math.NumberUtils;

public class HelperNumber extends NumberUtils {

    public static boolean isCross(int a, int aE, int b, int bE) {
        return (b > a && b < aE) || (bE < bE && bE < aE);
    }
}
