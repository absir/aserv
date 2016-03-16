/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-17 下午1:23:18
 */
package com.absir.aserv.configure.xls;

import com.absir.aserv.system.helper.HelperString;
import com.absir.core.dyna.DynaBinder;

import java.lang.reflect.Field;
import java.util.Map;

public class XlsAccessorParamMap extends XlsAccessorParam {

    private Class<?> keyClass;

    private Class<?> valueClass;

    public XlsAccessorParamMap(Field field, Class<?> cls, Class<?> beanClass, Class<?> valueClass) {
        super(field, cls, beanClass);
        this.keyClass = beanClass;
        this.valueClass = valueClass;
    }

    @Override
    protected Object getParamValues(String value) {
        return DynaBinder.INSTANCE.bind(HelperString.paramMap(value, keyClass, valueClass), null, getField().getGenericType());
    }

    @Override
    protected String getValueParams(Object value) {
        return HelperString.paramMap((Map<?, ?>) value);
    }
}
