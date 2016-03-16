/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-13 下午2:48:58
 */
package com.absir.bean.core;

import com.absir.core.kernel.KernelObject;

import java.lang.ref.SoftReference;

public final class BeanSoftReference extends SoftReference<Object> {

    protected BeanSoftReference(Object bean) {
        super(bean);
    }

    @Override
    public int hashCode() {
        return KernelObject.hashCode(get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null) {
            if (o instanceof BeanSoftReference) {
                return KernelObject.equals(get(), ((BeanSoftReference) o).get());
            }

            return o.equals(get());
        }

        return false;
    }
}
