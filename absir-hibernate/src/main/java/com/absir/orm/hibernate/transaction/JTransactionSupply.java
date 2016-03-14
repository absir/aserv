/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-3 下午7:08:00
 */
package com.absir.orm.hibernate.transaction;

import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelObject;
import com.absir.orm.hibernate.SessionFactoryBean;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.ISessionContext;
import com.absir.orm.transaction.ITransactionSupply;
import com.absir.orm.transaction.TransactionContext;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author absir
 *
 */
@SuppressWarnings("rawtypes")
@Bean
public class JTransactionSupply implements ITransactionSupply {

    /*
     * (non-Javadoc)
     *
     * @see com.absir.orm.transaction.ITransactionSupply#getTransactionContext()
     */
    @Override
    public TransactionContext getTransactionContext() {
        SessionFactoryBean sessionFactoryBean = SessionFactoryUtils.get();
        return sessionFactoryBean == null ? null : getSessionContext(null, sessionFactoryBean.getSessionFactory());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.orm.transaction.ITransactionSupply#getNameMapTransactionContext
     * ()
     */
    @Override
    public Map<String, TransactionContext> getNameMapTransactionContext() {
        SessionFactoryBean sessionFactoryBean = SessionFactoryUtils.get();
        if (sessionFactoryBean == null) {
            return null;
        }

        Map<String, TransactionContext> nameMapTransactionContext = new HashMap<String, TransactionContext>();
        for (String name : sessionFactoryBean.getNameMapSessionFactoryNames()) {
            TransactionContext transactionContext = getSessionContext(name, sessionFactoryBean.getNameMapSessionFactory(name));
            if (transactionContext != null) {
                nameMapTransactionContext.put(transactionContext.getName(), transactionContext);
            }
        }

        return nameMapTransactionContext;
    }

    /**
     * @param name
     * @param sessionFactory
     * @return
     */
    private TransactionContext getSessionContext(String name, SessionFactory sessionFactory) {
        if (sessionFactory != null) {
            Object currentSessionContext = KernelObject.declaredGet(sessionFactory, "currentSessionContext");
            if (currentSessionContext != null && currentSessionContext instanceof ISessionContext) {
                return ((ISessionContext) currentSessionContext).get(name);
            }
        }

        return null;
    }

}
