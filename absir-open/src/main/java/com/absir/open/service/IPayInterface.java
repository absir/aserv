/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-14 上午10:03:48
 */
package com.absir.open.service;

import java.lang.reflect.TypeVariable;

public interface IPayInterface<T> {

    public static final TypeVariable<?> TYPE_VARIABLE = IPayInterface.class.getTypeParameters()[0];

    public T getConfigure();

    public boolean validator(T configure, String tradeNo, String tradeReceipt, String platformData, float amount, boolean sandbox, String[] moreDatas) throws Exception;

}
