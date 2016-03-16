/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-3 上午11:04:41
 */
package com.absir.orm.hibernate.transaction;

import com.absir.core.kernel.KernelClass;
import com.absir.orm.transaction.*;
import org.hibernate.*;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;

@SuppressWarnings({"rawtypes", "serial"})
public class JSessionContext implements CurrentSessionContext, ISessionContext {

    private SessionFactoryImplementor sessionFactory;

    private JTransactionContext transactionContext;

    public JSessionContext(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public static ISessionHolder open(SessionFactory sessionFactory, ISessionHolder sessionHolder, TransactionAttribute transactionAttribute, final JTransactionSession jTransactionSession) {
        TransactionHolder transactionHolder = new TransactionHolder(sessionHolder, transactionAttribute) {

            @Override
            public void close(Throwable e) {
                boolean nested = (flag & TransactionHolder.NESTED_FLAG) != 0;
                JSession jSession = nested ? jTransactionSession.closeCurrentSession() : jTransactionSession.getCurrentSession();
                if (jSession != null) {
                    if (nested) {
                        try {
                            if (!isReadOnly()) {
                                Transaction transaction = jSession.getTransaction();
                                if (transaction != null) {
                                    if (e == null || rollback == null || !KernelClass.isAssignableFrom(rollback, e.getClass())) {
                                        transaction.commit();

                                    } else {
                                        transaction.rollback();
                                    }

                                } else if (!isRequired()) {
                                    jSession.getSession().flush();
                                }
                            }

                        } finally {
                            // close session
                            jSession.getSession().close();
                        }

                    } else {
                        if ((flag & READONLY_EQ_FLAG) == 0) {
                            if (isReadOnly()) {
                                jSession.getSession().setFlushMode(FlushMode.COMMIT);
                                jSession.getSession().setDefaultReadOnly(false);

                            } else {
                                jSession.getSession().setFlushMode(FlushMode.MANUAL);
                                jSession.getSession().setDefaultReadOnly(true);
                            }
                        }

                        if (timeout != 0) {
                            jSession.getTransaction().setTimeout((int) timeout);
                        }
                    }
                }
            }
        };

        int flag = transactionHolder.getFlag();
        boolean nested = (flag & TransactionHolder.NESTED_FLAG) != 0;
        JSession jSession;
        if (nested) {
            jSession = new JSession(sessionFactory.openSession());
            jTransactionSession.openCurrentSession(jSession);

        } else {
            jSession = jTransactionSession.getCurrentSession();
        }

        if (nested || (flag & TransactionHolder.READONLY_EQ_FLAG) == 0) {
            if (transactionHolder.isReadOnly()) {
                jSession.getSession().setFlushMode(FlushMode.MANUAL);
                jSession.getSession().setDefaultReadOnly(true);

            } else {
                jSession.getSession().setFlushMode(FlushMode.COMMIT);
                jSession.getSession().setDefaultReadOnly(false);
            }
        }

        if (nested || (flag & TransactionHolder.REQUIRED_EQ_FLAG) == 0) {
            if (transactionHolder.isRequired() || !transactionHolder.isReadOnly()) {
                Transaction transaction = jSession.openTransaction();
                int timeout = transactionAttribute.getTimeout();
                if (timeout > 0) {
                    transaction.setTimeout(timeout);
                }
            }
        }

        return transactionHolder;
    }

    @Override
    public Session currentSession() throws HibernateException {
        if (transactionContext == null) {
            throw new HibernateException("No TransactionContext configured!");
        }

        JTransactionSession transactionSession = transactionContext.getTransactionSession();
        if (transactionSession == null) {
            throw new HibernateException("No transactionSession configured!");
        }

        transactionSession.open(this);
        JSession session = transactionSession.getCurrentSession();
        return session == null ? null : session.getSession();
    }

    @Override
    public TransactionContext get(String name) {
        if (transactionContext == null) {
            transactionContext = new JTransactionContext(name);
        }

        return transactionContext;
    }

    @Override
    public ISessionHolder open(ISessionHolder sessionHolder, TransactionAttribute transactionAttribute, TransactionSession transactionSession) {
        return open(sessionFactory, sessionHolder, transactionAttribute, (JTransactionSession) transactionSession);
    }
}
