/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-4-25 上午10:00:06
 */
package com.absir.core.base;

import java.io.Serializable;

/**
 * @author absir
 */
@SuppressWarnings("rawtypes")
public abstract class ElementCompare<ID extends Serializable, V extends ElementCompare> extends Element<ID> implements Comparable<V> {

    /**
     * deleted
     */
    protected boolean deleted;

    /**
     * @return the deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * 删除对象
     */
    public void remove() {
        deleted = true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public final int compareTo(V o) {
        int compare = compareRank(o);
        if (compare == 0 && !(deleted && o.deleted)) {
            compare = 1;
        }

        return compare;
    }

    /**
     * @param o
     * @return
     */
    public abstract int compareRank(V o);
}
