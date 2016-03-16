/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-22 下午2:34:41
 */
package com.absir.context.core.compare;

public class ObjectHashSize extends ObjectHash {

    int size;

    public ObjectHashSize(int hashCode, int size) {
        super(hashCode);
        this.size = size;
    }

    @Override
    public int hashCode() {
        return hashCode + size;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof ObjectHashSize && hashCode == ((ObjectHash) obj).hashCode && size == ((ObjectHashSize) obj).size;
    }
}
