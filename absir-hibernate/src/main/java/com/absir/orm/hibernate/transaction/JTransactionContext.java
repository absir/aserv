/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-3 下午4:22:46
 */
package com.absir.orm.hibernate.transaction;

import com.absir.orm.transaction.TransactionContext;

public class JTransactionContext extends TransactionContext<JTransactionSession> {

    private String name;

    public JTransactionContext(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JTransactionSession createTransactionSession() {
        return new JTransactionSession();
    }
}
