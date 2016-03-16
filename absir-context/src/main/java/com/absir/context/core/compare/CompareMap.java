/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-22 下午2:54:42
 */
package com.absir.context.core.compare;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class CompareMap extends CompareAbstract<Map, ObjectHashSize> {

    @Override
    protected ObjectHashSize getCompareValue(Map value) {
        return new ObjectHashSize(value.hashCode(), value.size());
    }

    @Override
    protected boolean compareValue(ObjectHashSize compare, Map value) {
        return compare.size == value.size() && compare.hashCode == value.hashCode();
    }

}
