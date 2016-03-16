/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-2-13 下午4:38:14
 */
package com.absir.orm.transaction;

import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.orm.hibernate.SessionFactoryUtils;
import org.hibernate.SessionFactory;

import java.util.Collection;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
@Inject
public abstract class TransactionUtils {

    private static TransactionService transactionService = BeanFactoryUtils.get(TransactionService.class);

    public static TransactionService get() {
        return transactionService;
    }

    public static TransactionContext getTransactionContext(SessionFactory sessionFactory) {
        return transactionService.getNameMapTransactionContext(SessionFactoryUtils.get().getSessionFactoryMapName(sessionFactory));
    }

    public static void open(TransactionAttribute transactionAttribute, Collection<Entry<TransactionContext, TransactionAttribute>> transactionAttributes,
                            TransactionAttribute transactionAttributeDefault) {
        if (transactionAttribute != null) {
            transactionService.getTransactionContext().add(transactionAttribute);
        }

        if (transactionAttributes != null) {
            for (Entry<TransactionContext, TransactionAttribute> entry : transactionAttributes) {
                entry.getKey().add(entry.getValue());
            }
        }

        if (transactionAttributeDefault != null) {
            transactionService.addTransactionAttributeDefault(transactionAttributeDefault);
        }
    }

    public static Throwable close(TransactionAttribute transactionAttribute, Collection<Entry<TransactionContext, TransactionAttribute>> transactionAttributes,
                                  TransactionAttribute transactionAttributeDefault, Throwable e) {
        if (transactionAttributeDefault == null) {
            Throwable throwable = null;
            if (transactionAttribute != null) {
                throwable = transactionService.getTransactionContext().closeCurrent(e, throwable);
            }

            if (transactionAttributes != null) {
                for (Entry<TransactionContext, TransactionAttribute> entry : transactionAttributes) {
                    throwable = entry.getKey().closeCurrent(e, throwable);
                }
            }

            return throwable;

        } else {
            return transactionService.closeAllCurrent(e, null);
        }
    }
}
