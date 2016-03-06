/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月13日 下午4:16:10
 */
package com.absir.aserv.game.context;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.orm.transaction.value.Transaction;

/**
 * @author absir
 *
 */
@SuppressWarnings("rawtypes")
@Base
@Bean
public class ServerService {

	/** ME */
	public static final ServerService ME = BeanFactoryUtils.get(ServerService.class);

	/**
	 * 载入区对象
	 * 
	 * @param serverContext
	 * @return
	 */
	@Transaction(readOnly = true)
	public void load(JbServerContext serverContext) {
		serverContext.load();
	}

	/**
	 * 保存区对象
	 * 
	 * @param serverContext
	 */
	@Transaction
	public void save(JbServerContext serverContext) {
		serverContext.save();
	}

}
