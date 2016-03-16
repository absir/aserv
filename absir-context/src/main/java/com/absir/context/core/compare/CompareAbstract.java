/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-22 下午2:56:18
 */
package com.absir.context.core.compare;

@SuppressWarnings("unchecked")
public abstract class CompareAbstract<V, C> {

    public final C getCompare(Object value) {
        return value == null ? null : (C) getCompareValue((V) value);
    }

    protected abstract C getCompareValue(V value);

    public final boolean compareTo(C compare, V value) {
        return compare == value || (compare != null && value != null && compareValue(compare, value));
    }

    protected abstract boolean compareValue(C compare, V value);
}
