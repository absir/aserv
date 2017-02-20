/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-14 下午3:56:24
 */
package com.absir.context.core;

import com.absir.core.base.Base;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelReflect;

import java.io.Serializable;
import java.lang.reflect.TypeVariable;

@SuppressWarnings("rawtypes")
public abstract class Context<ID extends Serializable> extends Base<ID> {

    public static final TypeVariable ID_VARIABLE = (TypeVariable) KernelReflect.declaredField(Context.class, "id")
            .getGenericType();

    private ID id;

    public static Class getIdType(Class<? extends Context> contextClass) {
        return KernelClass.rawClass(KernelClass.type(contextClass, ID_VARIABLE));
    }

    public ID getId() {
        return id;
    }

    protected void setId(ID id) {
        this.id = id;
    }

    protected abstract void initialize();

    public abstract boolean unInitializeDone();

    public abstract void unInitialize();
}