/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-6 下午5:34:05
 */
package com.absir.aserv.system.service.utils;

import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.service.SecurityService;
import com.absir.server.on.OnPut;

/**
 * @author absir
 * 
 */
public abstract class SecurityServiceUtils {

	/**
	 * @return
	 */
	public static JiUserBase getUserBase() {
		OnPut onPut = OnPut.get();
		return onPut == null ? null : SecurityService.ME.getUserBase(onPut.getInput());
	}
}
