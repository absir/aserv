/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-22 下午2:54:30
 */
package com.absir.context.core.compare;

import java.util.Collection;

@SuppressWarnings("rawtypes")
public class CompareCollection extends CompareAbstract<Collection, ObjectHashSize> {

    @Override
    protected ObjectHashSize getCompareValue(Collection value) {
        return new ObjectHashSize(value.hashCode(), value.size());
    }

    @Override
    protected boolean compareValue(ObjectHashSize compare, Collection value) {
        return compare.size == value.size() && compare.hashCode == value.hashCode();
    }

}
