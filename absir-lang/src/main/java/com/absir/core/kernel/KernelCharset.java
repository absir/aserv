/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-10 下午11:01:58
 */
package com.absir.core.kernel;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class KernelCharset {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    private static Charset defaultCharset;

    private static CharsetEncoder defaultEncoder;

    private static CharsetDecoder defaultDecoder;

    public static Charset getDefault() {
        if (defaultCharset == null) {
            defaultCharset = UTF8;
        }

        return defaultCharset;
    }

    public static void setDefault(Charset charset) {
        defaultCharset = charset;
    }

    public static CharsetEncoder getDefaultEncoder() {
        if (defaultEncoder == null) {
            defaultEncoder = getDefault().newEncoder();
        }

        return defaultEncoder;
    }

    public static CharsetDecoder getDefaultDecoder() {
        if (defaultDecoder == null) {
            defaultDecoder = getDefault().newDecoder();
        }

        return defaultDecoder;
    }
}
