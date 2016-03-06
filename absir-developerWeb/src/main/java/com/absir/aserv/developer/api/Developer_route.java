/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.developer.api;

import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.transaction.TransactionIntercepter;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.base.Environment;
import com.absir.orm.transaction.value.Transaction;
import com.absir.server.in.Input;
import com.absir.server.value.Body;
import com.absir.server.value.Interceptors;
import com.absir.server.value.Mapping;
import com.absir.server.value.Param;
import com.absir.server.value.Server;
import com.absir.servlet.InputRequest;

/**
 * @author absir
 * 
 */
@Base(environment = Environment.DEVELOP)
@Interceptors(TransactionIntercepter.class)
@Mapping("/developer")
@Server
public class Developer_route {

	/**
	 * @return
	 */
	@Body
	public Object route(Input input) {
		if (input instanceof InputRequest) {
			return ((InputRequest) input).getRequest().getSession().getServletContext().getRealPath("");
		}

		return BeanFactoryUtils.getBeanConfig().getClassPath();
	}

	/**
	 * @param hql
	 * @return
	 */
	@Body
	public Object update(@Param String hql, Input input) {
		TransactionIntercepter.open(input, "", BeanService.TRANSACTION_READ_WRITE);
		return BeanDao.getSession().createQuery(hql).executeUpdate();
	}

	/**
	 * @param hql
	 */
	@Body
	@Transaction(readOnly = true)
	public Object list(@Param String hql, Input input) {
		TransactionIntercepter.open(input, "", BeanService.TRANSACTION_READ_WRITE);
		return BeanDao.getSession().createQuery(hql).list();
	}
}