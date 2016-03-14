/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-22 下午2:54:15
 */
package com.absir.context.core.compare;

import java.lang.reflect.Array;

/**
 * @author absir
 *
 */
public class CompareArray extends CompareAbstract<Object, ObjectHashSize> {

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.context.value.CompareAbstract#getCompareValue
     * (java.lang.Object)
     */
    @Override
    protected ObjectHashSize getCompareValue(Object value) {
        return new ObjectHashSize(value.hashCode(), Array.getLength(value));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.context.value.CompareAbstract#compareValue(java
     * .lang.Object, java.lang.Object)
     */
    @Override
    public boolean compareValue(ObjectHashSize compare, Object value) {
        return compare.size == Array.getLength(value) && compare.hashCode == value.hashCode();
    }
}
