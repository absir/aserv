/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-4 上午11:27:46
 */
package com.absir.orm.hibernate.transaction;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class JSession {

    private Session session;

    private Transaction transaction;

    public JSession() {
    }

    public JSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public Transaction openTransaction() {
        if (transaction == null) {
            transaction = session.beginTransaction();
        }

        return transaction;
    }
}
