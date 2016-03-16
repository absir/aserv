/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-22 下午2:34:32
 */
package com.absir.context.core.compare;

public class ObjectHash {

    int hashCode;

    public ObjectHash(int hashCode) {
        this.hashCode = hashCode;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof ObjectHash && hashCode == ((ObjectHash) obj).hashCode;
    }
}
