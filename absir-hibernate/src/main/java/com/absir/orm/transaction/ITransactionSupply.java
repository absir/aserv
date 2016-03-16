/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-3 下午7:06:33
 */
package com.absir.orm.transaction;

import java.util.Map;

@SuppressWarnings("rawtypes")
public interface ITransactionSupply {

    public TransactionContext getTransactionContext();

    public Map<String, TransactionContext> getNameMapTransactionContext();

}
