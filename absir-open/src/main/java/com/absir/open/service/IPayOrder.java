/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年9月8日 下午4:49:09
 */
package com.absir.open.service;

import java.util.Map;

import com.absir.open.bean.JPayTrade;

/**
 * @author absir
 *
 */
public interface IPayOrder {

	/**
	 * @param payTrade
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Object order(JPayTrade payTrade, Map<String, Object> paramMap) throws Exception;

}
