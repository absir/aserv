/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月17日 上午11:19:39
 */
package com.absir.log.api;

import com.absir.aserv.system.service.BeanService;
import com.absir.bean.basis.Base;
import com.absir.log.bean.JLoginLog;
import com.absir.log.bean.JPayLog;
import com.absir.log.bean.JServerLog;
import com.absir.server.value.Body;
import com.absir.server.value.Server;

/**
 * @author absir
 *
 */
@Base
@Server
public class api_log extends ApiMaster {

	/**
	 * @param log
	 */
	public void login(@Body JLoginLog log) {
		BeanService.ME.persist(log);
	}

	/**
	 * @param log
	 */
	public void pay(@Body JPayLog log) {
		BeanService.ME.persist(log);
	}

	/**
	 * @param log
	 */
	public void server(@Body JServerLog log) {
		BeanService.ME.persist(log);
	}

}
