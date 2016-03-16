/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-3 下午2:57:22
 */
package com.absir.orm.transaction;

import com.absir.core.kernel.KernelLang.ObjectEntry;
import com.absir.orm.transaction.Transactional.TxType;
import com.absir.orm.transaction.value.Transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
public class TransactionManager {

    private TransactionAttribute transactionAttribute;

    private Collection<Entry<TransactionContext, TransactionAttribute>> transactionAttributes;

    private TransactionAttribute transactionAttributeDefault;

    public TransactionAttribute getTransactionAttribute() {
        return transactionAttribute;
    }

    public void setTransactionAttribute(TransactionAttribute transactionAttribute) {
        if (TransactionUtils.get().getTransactionContext() != null) {
            this.transactionAttribute = transactionAttribute;
        }
    }

    public void setTransactionAttribute(Transaction transaction, String name) {
        setTransactionAttribute(name == null ? transaction.name() : name, transaction.readOnly(), transaction.rollback(), transaction.nested(), transaction.required(), transaction.timeout());
    }

    public void setTransactionAttribute(Transactional transactional, String name) {
        setTransactionAttribute(name, false, transactional.rollbackOn(), transactional.value() == TxType.REQUIRES_NEW, transactional.value() != TxType.NEVER, 0);
    }

    public void setTransactionAttribute(String transactionName, boolean readonly, Class<?>[] rollback, boolean nested, boolean required, int timeout) {
        if (transactionName == null || "".equals(transactionName)) {
            setTransactionAttribute(new TransactionAttribute(readonly, rollback, nested, required, timeout));

        } else if ("*".equals(transactionName)) {
            setTransactionAttributeDefault(new TransactionAttribute(readonly, rollback, nested, required, timeout));

        } else {
            TransactionContext transactionContext = TransactionUtils.get().getNameMapTransactionContext(transactionName);
            if (transactionContext != null) {
                setTransactionAttribute(transactionContext, new TransactionAttribute(readonly, rollback, nested, required, timeout));
            }
        }
    }

    public void setTransactionAttribute(TransactionContext transactionContext, TransactionAttribute transactionAttribute) {
        if (transactionAttributes == null) {
            transactionAttributes = new HashSet<Entry<TransactionContext, TransactionAttribute>>();
        }

        transactionAttributes.add(new ObjectEntry<TransactionContext, TransactionAttribute>(transactionContext, transactionAttribute));
    }

    public void unmodifiable() {
        if (transactionAttributes != null) {
            if (transactionAttributes.isEmpty()) {
                transactionAttributes = null;

            } else {
                transactionAttributes = Collections.unmodifiableList(new ArrayList<Entry<TransactionContext, TransactionAttribute>>(transactionAttributes));
            }
        }
    }

    public boolean isEmpty() {
        return transactionAttribute == null && transactionAttributeDefault == null && transactionAttributes == null;
    }

    public TransactionAttribute getTransactionAttributeDefault() {
        return transactionAttributeDefault;
    }

    public void setTransactionAttributeDefault(TransactionAttribute transactionAttributeDefault) {
        this.transactionAttributeDefault = transactionAttributeDefault;
    }

    public void open() {
        TransactionUtils.open(transactionAttribute, transactionAttributes, transactionAttributeDefault);
    }

    public Throwable close(Throwable e) {
        return TransactionUtils.close(transactionAttribute, transactionAttributes, transactionAttributeDefault, e);
    }
}
