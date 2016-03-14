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

/**
 * @author absir
 */
@SuppressWarnings("rawtypes")
public class TransactionManager {

    /**
     * transactionAttribute
     */
    private TransactionAttribute transactionAttribute;

    /**
     * transactionAttributes
     */
    private Collection<Entry<TransactionContext, TransactionAttribute>> transactionAttributes;

    /**
     * transactionAttributeDefault
     */
    private TransactionAttribute transactionAttributeDefault;

    /**
     * @return the transactionAttribute
     */
    public TransactionAttribute getTransactionAttribute() {
        return transactionAttribute;
    }

    /**
     * @param transactionAttribute the transactionAttribute to set
     */
    public void setTransactionAttribute(TransactionAttribute transactionAttribute) {
        if (TransactionUtils.get().getTransactionContext() != null) {
            this.transactionAttribute = transactionAttribute;
        }
    }

    /**
     * @param transaction
     * @param name
     */
    public void setTransactionAttribute(Transaction transaction, String name) {
        setTransactionAttribute(name == null ? transaction.name() : name, transaction.readOnly(), transaction.rollback(), transaction.nested(), transaction.required(), transaction.timeout());
    }

    /**
     * @param transactional
     * @param name
     */
    public void setTransactionAttribute(Transactional transactional, String name) {
        setTransactionAttribute(name, false, transactional.rollbackOn(), transactional.value() == TxType.REQUIRES_NEW, transactional.value() != TxType.NEVER, 0);
    }

    /**
     * @param transactionName
     * @param readonly
     * @param rollback
     * @param nested
     * @param required
     * @param timeout
     */
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

    /**
     * @param transactionContext
     * @param transactionAttribute
     */
    public void setTransactionAttribute(TransactionContext transactionContext, TransactionAttribute transactionAttribute) {
        if (transactionAttributes == null) {
            transactionAttributes = new HashSet<Entry<TransactionContext, TransactionAttribute>>();
        }

        transactionAttributes.add(new ObjectEntry<TransactionContext, TransactionAttribute>(transactionContext, transactionAttribute));
    }

    /**
     *
     */
    public void unmodifiable() {
        if (transactionAttributes != null) {
            if (transactionAttributes.isEmpty()) {
                transactionAttributes = null;

            } else {
                transactionAttributes = Collections.unmodifiableList(new ArrayList<Entry<TransactionContext, TransactionAttribute>>(transactionAttributes));
            }
        }
    }

    /**
     * @return
     */
    public boolean isEmpty() {
        return transactionAttribute == null && transactionAttributeDefault == null && transactionAttributes == null;
    }

    /**
     * @return the transactionAttributeDefault
     */
    public TransactionAttribute getTransactionAttributeDefault() {
        return transactionAttributeDefault;
    }

    /**
     * @param transactionAttributeDefault the transactionAttributeDefault to set
     */
    public void setTransactionAttributeDefault(TransactionAttribute transactionAttributeDefault) {
        this.transactionAttributeDefault = transactionAttributeDefault;
    }

    /**
     *
     */
    public void open() {
        TransactionUtils.open(transactionAttribute, transactionAttributes, transactionAttributeDefault);
    }

    /**
     * @param e
     * @return
     */
    public Throwable close(Throwable e) {
        return TransactionUtils.close(transactionAttribute, transactionAttributes, transactionAttributeDefault, e);
    }
}
