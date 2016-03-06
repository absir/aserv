/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-4-23 下午3:21:33
 */
package com.absir.open.service;

import com.absir.open.bean.JPayTrade;

/**
 * @author absir
 * 
 */
public interface IPayProccessor {

	/**
	 * @param payTrade
	 * @return
	 */
	public Object proccess(JPayTrade payTrade) throws Exception;

}
