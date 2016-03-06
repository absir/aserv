/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-2-14 上午10:44:58
 */
package com.absir.orm.transaction;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public interface ISessionContext {

	/**
	 * @return
	 */
	public TransactionContext get(String name);

	/**
	 * @param holderBefore
	 * @param transactionAttribute
	 * @param transactionSession
	 * @return
	 */
	public ISessionHolder open(ISessionHolder holderBefore, TransactionAttribute transactionAttribute, TransactionSession transactionSession);
}
