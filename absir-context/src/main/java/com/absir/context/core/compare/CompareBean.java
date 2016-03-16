/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-22 下午2:53:56
 */
package com.absir.context.core.compare;

public class CompareBean extends CompareAbstract<Object, Integer> {

    @Override
    protected Integer getCompareValue(Object value) {
        return value.hashCode();
    }

    @Override
    public boolean compareValue(Integer compare, Object value) {
        return compare == value.hashCode();
    }
}
