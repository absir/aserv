/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-22 下午2:43:32
 */
package com.absir.context.core.compare;

public class CompareField extends CompareAbstract<Object, Object> {

    @Override
    protected Object getCompareValue(Object value) {
        return value;
    }

    @Override
    protected boolean compareValue(Object compare, Object value) {
        return compare.equals(value);
    }
}
