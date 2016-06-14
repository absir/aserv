/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-6 上午10:02:43
 */
package com.absir.aserv.game.value;

import com.absir.core.kernel.KernelClass;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class OBuffFromRound<T, O extends OObject> extends OBuffRound<O> implements IBuffFrom<T> {

    private Class<T> formType;

    public OBuffFromRound() {
        formType = KernelClass.typeClass(getClass(), IBuffFrom.T_VARIABLE);
    }

    @Override
    public boolean supportsFrom(Object from) {
        return from == null ? isFromNullable() : formType.isAssignableFrom(formType.getClass());
    }

    public boolean isFromNullable() {
        return false;
    }
}
