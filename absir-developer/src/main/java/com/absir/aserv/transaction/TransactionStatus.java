/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-11 下午12:19:53
 */
package com.absir.aserv.transaction;

import com.absir.core.kernel.KernelLang.ObjectEntry;
import com.absir.orm.transaction.TransactionAttribute;
import com.absir.orm.transaction.TransactionContext;
import com.absir.orm.transaction.TransactionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class TransactionStatus {

    private boolean transactionOpened;

    private TransactionAttribute transactionAttribute;

    private Set<String> transactionOpeneds;

    private Collection<Entry<TransactionContext, TransactionAttribute>> transactionAttributes;

    public void open(String transactionName, TransactionAttribute transactionAttribute) {
        if (transactionName == null || "".equals(transactionName)) {
            if (!transactionOpened) {
                this.transactionAttribute = transactionAttribute;
                TransactionUtils.get().getTransactionContext().add(transactionAttribute);
            }

        } else {
            if (transactionOpeneds == null || !transactionOpeneds.contains(transactionName)) {
                TransactionContext transactionContext = TransactionUtils.get().getNameMapTransactionContext(transactionName);
                if (transactionContext != null) {
                    transactionContext.add(transactionAttribute);
                }

                if (transactionOpeneds == null) {
                    transactionOpeneds = new HashSet<String>();
                    transactionAttributes = new ArrayList<Entry<TransactionContext, TransactionAttribute>>();
                }

                transactionOpeneds.add(transactionName);
                transactionAttributes.add(new ObjectEntry<TransactionContext, TransactionAttribute>(transactionContext, transactionAttribute));
            }
        }
    }

    public Throwable closeCurrent(Throwable e) {
        return TransactionUtils.close(transactionAttribute, transactionAttributes, null, e);
    }
}
