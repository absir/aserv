/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-22 下午2:54:15
 */
package com.absir.context.core.compare;

import java.lang.reflect.Array;

public class CompareArray extends CompareAbstract<Object, ObjectHashSize> {

    @Override
    protected ObjectHashSize getCompareValue(Object value) {
        return new ObjectHashSize(value.hashCode(), Array.getLength(value));
    }

    @Override
    public boolean compareValue(ObjectHashSize compare, Object value) {
        return compare.size == Array.getLength(value) && compare.hashCode == value.hashCode();
    }
}
