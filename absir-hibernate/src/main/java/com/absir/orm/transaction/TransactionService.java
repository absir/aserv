/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-3 上午10:07:02
 */
package com.absir.orm.transaction;

import com.absir.aop.AopMethodDefineAbstract;
import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.orm.transaction.value.Transaction;
import com.absir.orm.transaction.value.Transactions;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
@Bean
@Basis
public class TransactionService extends AopMethodDefineAbstract<TransactionInterceptor, TransactionManager, String> {

    private TransactionContext transactionContext;

    private Map<String, TransactionContext> nameMapTransactionContext;

    @Inject(type = InjectType.Selectable)
    protected void initTransactionSupplies(ITransactionSupply[] transactionSupplies) {
        for (ITransactionSupply transactionSupply : transactionSupplies) {
            TransactionContext transactionContext = transactionSupply.getTransactionContext();
            if (transactionContext != null) {
                this.transactionContext = transactionContext;
            }

            Map<String, TransactionContext> nameMapTransactionContext = transactionSupply
                    .getNameMapTransactionContext();
            if (nameMapTransactionContext != null) {
                for (Entry<String, TransactionContext> entry : nameMapTransactionContext.entrySet()) {
                    setNameMapTransactionContext(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public TransactionContext getTransactionContext() {
        return transactionContext;
    }

    public TransactionContext getNameMapTransactionContext(String name) {
        return name == null ? transactionContext
                : nameMapTransactionContext == null ? null : nameMapTransactionContext.get(name);
    }

    private void setNameMapTransactionContext(String name, TransactionContext transactionContext) {
        if (nameMapTransactionContext == null) {
            synchronized (this) {
                if (nameMapTransactionContext == null) {
                    nameMapTransactionContext = new HashMap<String, TransactionContext>();
                }
            }
        }

        nameMapTransactionContext.put(name, transactionContext);
    }

    public void addTransactionAttributeDefault(TransactionAttribute transactionAttribute) {
        if (transactionContext != null) {
            transactionContext.add(transactionAttribute, true);
        }

        if (nameMapTransactionContext != null) {
            for (Entry<String, TransactionContext> entry : nameMapTransactionContext.entrySet()) {
                entry.getValue().add(transactionAttribute, true);
            }
        }
    }

    public Throwable closeAllCurrent(Throwable e, Throwable throwable) {
        if (transactionContext != null) {
            throwable = transactionContext.closeCurrent(e, throwable);
        }

        if (nameMapTransactionContext != null) {
            for (Entry<String, TransactionContext> entry : nameMapTransactionContext.entrySet()) {
                throwable = entry.getValue().closeCurrent(e, throwable);
            }
        }

        return throwable;
    }

    @Override
    public TransactionInterceptor getAopInterceptor(BeanDefine beanDefine, Object beanObject) {
        return new TransactionInterceptor();
    }

    @Override
    public String getVariable(TransactionInterceptor aopInterceptor, BeanDefine beanDefine, Object beanObject) {
        return beanObject instanceof ITransactionName ? ((ITransactionName) beanObject).getTransactionName() : null;
    }

    @Override
    public void setAopInterceptor(TransactionManager interceptor, TransactionInterceptor aopInterceptor,
                                  Class<?> beanType, Method method, Method beanMethod) {
        aopInterceptor.getMethodMapInterceptor().put(beanMethod, interceptor);
    }

    @Override
    public TransactionManager getAopInterceptor(String variable, Class<?> beanType) {
        Transactions transactions = BeanConfigImpl.getTypeAnnotation(beanType, Transactions.class);
        if (transactions != null) {
            TransactionManager transactionManager = new TransactionManager();
            for (Transaction transaction : transactions.value()) {
                transactionManager.setTransactionAttribute(transaction, variable);
            }

            return transactionManager;
        }

        Transaction transaction = BeanConfigImpl.getTypeAnnotation(beanType, Transaction.class);
        if (transaction != null) {
            TransactionManager transactionManager = new TransactionManager();
            transactionManager.setTransactionAttribute(transaction, variable);
            return transactionManager;
        }

        Transactional transactional = BeanConfigImpl.getTypeAnnotation(beanType, Transactional.class);
        if (transactional != null) {
            TransactionManager transactionManager = new TransactionManager();
            transactionManager.setTransactionAttribute(transactional, variable);
            return transactionManager;
        }

        return null;
    }

    @Override
    public TransactionManager getAopInterceptor(TransactionManager interceptor, String variable, Class<?> beanType,
                                                Method method) {
        Transactions transactions = BeanConfigImpl.getMethodAnnotation(method, Transactions.class, true);
        if (transactions == null) {
            Transaction transaction = BeanConfigImpl.getMethodAnnotation(method, Transaction.class, true);
            if (transaction == null) {
                Transactional transactional = BeanConfigImpl.getMethodAnnotation(method, Transactional.class, true);
                if (transactional != null) {
                    if (interceptor == null) {
                        interceptor = new TransactionManager();
                    }

                    interceptor.setTransactionAttribute(transactional, variable);
                }

            } else {
                if (interceptor == null) {
                    interceptor = new TransactionManager();
                }

                interceptor.setTransactionAttribute(transaction, variable);
            }

        } else {
            if (interceptor == null) {
                interceptor = new TransactionManager();
            }

            for (Transaction transaction : transactions.value()) {
                interceptor.setTransactionAttribute(transaction, variable);
            }
        }

        if (interceptor != null) {
            interceptor.unmodifiable();
            if (interceptor.isEmpty()) {
                interceptor = null;
            }
        }

        return interceptor;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
