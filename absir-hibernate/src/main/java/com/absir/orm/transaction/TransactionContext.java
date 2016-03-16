/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-2-13 下午4:38:14
 */
package com.absir.orm.transaction;

import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelObject;

public abstract class TransactionContext<T extends TransactionSession> {

    protected ThreadLocal<T> transactionSessionLocal = new ThreadLocal<T>();

    public String getName() {
        return null;
    }

    @Override
    public int hashCode() {
        return KernelObject.hashCode(getName());
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    public abstract T createTransactionSession();

    public T getTransactionSession() {
        return transactionSessionLocal.get();
    }

    public TransactionSession add(TransactionAttribute transactionAttribute) {
        return add(transactionAttribute, false);
    }

    public TransactionSession add(TransactionAttribute transactionAttribute, boolean addition) {
        T transactionSession = transactionSessionLocal.get();
        if (transactionSession == null) {
            transactionSession = createTransactionSession();
            transactionSessionLocal.set(transactionSession);
        }

        transactionSession.add(transactionAttribute, addition);
        return transactionSession;
    }

    public Throwable closeCurrent(Throwable e, Throwable throwable) {
        T transactionSession = transactionSessionLocal.get();
        if (transactionSession != null) {
            try {
                if (transactionSession.closeCurrent(e)) {
                    transactionSessionLocal.remove();
                }

            } catch (Throwable ex) {
                if (transactionSession.isCloseAll()) {
                    transactionSessionLocal.remove();
                }

                KernelLang.ThrowableMutil(ex, throwable == null ? e : throwable);
                throwable = ex;
            }
        }

        return throwable;
    }
}