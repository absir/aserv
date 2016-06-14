/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-14 下午3:15:31
 */
package com.absir.core.base;

import java.io.Serializable;
import java.lang.reflect.TypeVariable;

public interface IBase<ID extends Serializable> {

    public static final TypeVariable ID_VARIABLE = IBase.class.getTypeParameters()[0];

    public ID getId();

}
