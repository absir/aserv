/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-11 上午11:32:35
 */
package com.absir.aserv.system.helper;

import com.absir.core.kernel.KernelString;

import java.util.regex.Pattern;

public class HelperPattern {

    public static final Pattern EMAIL_PATTERN = Pattern.compile("[\\w]+@[\\w]+");

    public static final Pattern MOBILE_PATTERN = Pattern.compile("1[0-9]{10}");

    public static boolean isEmail(String input) {
        return !KernelString.isEmpty(input) && EMAIL_PATTERN.matcher(input).find();
    }

    public static boolean isMobilePhone(String input) {
        return !KernelString.isEmpty(input) && MOBILE_PATTERN.matcher(input).find();
    }
}
