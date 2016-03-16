/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-14 下午3:35:10
 */
package com.absir.core.kernel;

import java.util.regex.Pattern;

public class KernelPattern {

    public static Pattern[] getPatterns(String[] strs, int flags) {
        if (strs == null) {
            return null;
        }

        int length = strs.length;
        if (length == 0) {
            return null;
        }

        Pattern[] patterns = new Pattern[length];
        for (int i = 0; i < length; i++) {
            patterns[i] = Pattern.compile(strs[i], flags);
        }

        return patterns;
    }

    public static boolean matchPatterns(String str, Pattern[] patterns) {
        if (patterns == null) {
            return false;
        }

        for (Pattern pattern : patterns) {
            if (pattern.matcher(str).find()) {
                return true;
            }
        }

        return false;
    }
}
