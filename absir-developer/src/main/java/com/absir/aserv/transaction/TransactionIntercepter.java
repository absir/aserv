/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-10 下午6:45:36
 */
package com.absir.aserv.transaction;

import com.absir.orm.transaction.TransactionAttribute;
import com.absir.server.in.Input;
import com.absir.server.in.Interceptor;
import com.absir.server.on.OnPut;

import java.util.Iterator;

public class TransactionIntercepter implements Interceptor {

    private static final String TRANSACTION_STATUS_NAME = TransactionIntercepter.class.getName() + "@TRANSACTION_STATUS_NAME";

    public static TransactionStatus getTransactionStatus(Input input) {
        Object transactionStatus = input.getAttribute(TRANSACTION_STATUS_NAME);
        return transactionStatus == null || !(transactionStatus instanceof TransactionStatus) ? null : (TransactionStatus) transactionStatus;
    }

    public static void open(Input input, String transactionName, TransactionAttribute transactionAttribute) {
        if (transactionName == null) {
            return;
        }

        Object status = input.getAttribute(TRANSACTION_STATUS_NAME);
        if (status != null) {
            TransactionStatus transactionStatus = null;
            if (status == TransactionStatus.class) {
                transactionStatus = new TransactionStatus();
                input.setAttribute(TRANSACTION_STATUS_NAME, transactionStatus);
                transactionStatus.open(transactionName, transactionAttribute);

            }

            /*
             else if (status instanceof TransactionStatus) {
                transactionStatus = (TransactionStatus) status;
            }

            if (transactionStatus != null) {
                transactionStatus.open(transactionName, transactionAttribute);
            }
             */
        }
    }

    @Override
    public final OnPut intercept(Iterator<Interceptor> iterator, Input input) throws Throwable {
        Throwable e = null;
        try {
            input.setAttribute(TRANSACTION_STATUS_NAME, TransactionStatus.class);
            return interceptImpl(iterator, input);

        } catch (Throwable ex) {
            e = ex;
            throw e;

        } finally {
            TransactionStatus transactionStatus = getTransactionStatus(input);
            if (transactionStatus != null) {
                e = transactionStatus.closeCurrent(e);
                input.setAttribute(TRANSACTION_STATUS_NAME, null);
            }
        }
    }

    public OnPut interceptImpl(Iterator<Interceptor> iterator, Input input) throws Throwable {
        return input.intercept(iterator);
    }
}
