/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-15 下午4:58:40
 */
package com.absir.core.base;

import com.absir.core.kernel.KernelObject;

import java.io.Serializable;

@SuppressWarnings("rawtypes")
public abstract class Base<ID extends Serializable> implements IBase<ID> {

    @Override
    public int hashCode() {
        return KernelObject.hashCode(getId());
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass() && KernelObject.equals(getId(), ((IBase) obj).getId());
    }

    @Override
    public String toString() {
        return getClass().getName() + '[' + getId() + ']';
    }

}
