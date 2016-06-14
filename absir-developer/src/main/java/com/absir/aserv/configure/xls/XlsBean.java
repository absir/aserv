/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-27 下午12:00:21
 */
package com.absir.aserv.configure.xls;

import java.io.Serializable;
import java.lang.reflect.TypeVariable;

@SuppressWarnings("unchecked")
public class XlsBean<T extends Serializable> extends XlsBase {

    public static final TypeVariable ID_VARIABLE = XlsBean.class.getTypeParameters()[0];

    public T getId() {
        return (T) id;
    }
}
