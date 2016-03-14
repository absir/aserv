/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-15 下午4:58:40
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
public class Bean<ID extends Serializable> extends Base<ID> {

    /**
     * ID_VARIABLE
     */
    public static final TypeVariable ID_VARIABLE = (TypeVariable) KernelReflect.declaredField(Bean.class, "id").getGenericType();
    /**
     * id
     */
    private ID id;

    /**
     * @param beanClass
     * @return
     */
    public static Class getIdType(Class<? extends Bean> beanClass) {
        return KernelClass.rawClass(KernelClass.type(beanClass, ID_VARIABLE));
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
    public void setId(ID id) {
        this.id = id;
    }
}
