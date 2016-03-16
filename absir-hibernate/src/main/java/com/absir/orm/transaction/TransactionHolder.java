/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-4 上午10:14:34
 */
package com.absir.orm.transaction;

public abstract class TransactionHolder implements ISessionHolder {

    public static final int READONLY_FLAG = 0x01;

    public static final int READONLY_EQ_FLAG = READONLY_FLAG << 1;

    public static final int NESTED_FLAG = READONLY_EQ_FLAG << 1;

    public static final int NESTED_EQ_FLAG = NESTED_FLAG << 1;

    public static final int REQUIRED_FLAG = NESTED_EQ_FLAG << 1;

    public static final int REQUIRED_EQ_FLAG = REQUIRED_FLAG << 1;

    protected int flag;

    protected Class<?>[] rollback;

    protected long timeout;

    public TransactionHolder() {
    }

    public TransactionHolder(ISessionHolder holderBefore, TransactionAttribute transactionAttribute) {
        if (holderBefore == null || transactionAttribute.isNested()) {
            flag |= NESTED_FLAG;

        } else {
            if (holderBefore.isReadOnly() == transactionAttribute.isReadOnly()) {
                flag |= READONLY_EQ_FLAG;
            }

            // if (holderBefore.isRequired()) {
            // flag |= REQUIRED_FLAG;
            // flag |= REQUIRED_EQ_FLAG;
            //
            // } else
            if (holderBefore.isRequired() == transactionAttribute.isRequired()) {
                flag |= REQUIRED_EQ_FLAG;
            }

            // recard holderBefore timeout
            if (transactionAttribute.getTimeout() > 0) {
                timeout = holderBefore.getTimeout();
                if (timeout < 0 || timeout == transactionAttribute.getTimeout()) {
                    timeout = 0;
                }
            }
        }

        if (transactionAttribute.isReadOnly()) {
            flag |= READONLY_FLAG;
        }

        if (transactionAttribute.isRequired()) {
            flag |= REQUIRED_FLAG;
        }

        // self rollback set
        rollback = transactionAttribute.getRollback();
    }

    @Override
    public boolean isReadOnly() {
        return (flag & READONLY_FLAG) != 0;
    }

    @Override
    public boolean isRequired() {
        return (flag & REQUIRED_FLAG) != 0;
    }

    public int getFlag() {
        return flag;
    }

    public long getTimeout() {
        return timeout;
    }

    public Class<?>[] getRollback() {
        return rollback;
    }
}
