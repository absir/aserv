/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-2-14 上午10:44:58
 */
package com.absir.orm.transaction;

@SuppressWarnings("rawtypes")
public interface ISessionContext {

    public TransactionContext get(String name);

    public ISessionHolder open(ISessionHolder holderBefore, TransactionAttribute transactionAttribute, TransactionSession transactionSession);
}
