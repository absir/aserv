/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-12 下午3:19:01
 */
package com.absir.server.in;

public abstract class InMatcher {

    private byte[] mapping;

    private byte[] suffix;

    public InMatcher(String mapping, String suffix) {
        this.mapping = mapping.getBytes();
        this.suffix = suffix == null ? null : suffix.getBytes();
    }

    public byte[] getMapping() {
        return mapping;
    }

    public byte[] getSuffix() {
        return suffix;
    }

    public int getSuffixLength() {
        return suffix == null ? 0 : suffix.length;
    }

    public abstract int getParameterLength();

}
