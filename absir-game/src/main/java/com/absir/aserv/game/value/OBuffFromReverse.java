/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-6 上午10:01:15
 */
package com.absir.aserv.game.value;

import com.absir.core.kernel.KernelClass;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class OBuffFromReverse<T, O extends OObject> extends OBuffReverse<O> implements IBuffFrom<T> {

    private Class<T> formType;

    public OBuffFromReverse() {
        formType = KernelClass.argumentClass(formType);
    }

    @Override
    public boolean supportsFrom(Object from) {
        return from == null ? isFromNullable() : formType.isAssignableFrom(formType.getClass());
    }

    public boolean isFromNullable() {
        return false;
    }
}
