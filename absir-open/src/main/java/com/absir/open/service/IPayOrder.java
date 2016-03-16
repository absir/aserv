/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年9月8日 下午4:49:09
 */
package com.absir.open.service;

import com.absir.open.bean.JPayTrade;

import java.util.Map;

public interface IPayOrder {

    public Object order(JPayTrade payTrade, Map<String, Object> paramMap) throws Exception;

}
