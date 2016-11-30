/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-14 上午10:03:48
 */
package com.absir.open.service;

import com.absir.open.bean.JPayTrade;

import java.lang.reflect.TypeVariable;
import java.util.Map;

public interface IPayInterface<T> {

    public static final TypeVariable<?> TYPE_VARIABLE = IPayInterface.class.getTypeParameters()[0];

    public T getConfigure();

    public String order(T configure, JPayTrade payTrade, String prepare, Map<String, Object> paramMap) throws Exception;

    public String validator(T configure, String tradeNo, String tradeReceipt, String platformData, String goodsId, int goodsNumber, float amount, boolean sandbox, String[] moreDatas) throws Exception;

}
