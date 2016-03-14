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

/**
 * @author absir
 */
@SuppressWarnings("rawtypes")
public abstract class Context<ID extends Serializable> extends Base<ID> {

    /**
     * ID_VARIABLE
     */
    public static final TypeVariable ID_VARIABLE = (TypeVariable) KernelReflect.declaredField(Context.class, "id")
            .getGenericType();
    /**
     * id
     */
    private ID id;

    /**
     * @param contextClass
     * @return
     */
    public static Class getIdType(Class<? extends Context> contextClass) {
        return KernelClass.rawClass(KernelClass.type(contextClass, ID_VARIABLE));
    }

    /**
     * @return the id
     */
    public ID getId() {
        return id;
    }

    /**
     * @param id
     */
    protected void setId(ID id) {
        this.id = id;
    }

    /**
     *
     */
    protected abstract void initialize();

    /**
     * @return
     */
    public abstract boolean uninitializeDone();

    /**
     *
     */
    public abstract void uninitialize();
}