/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-2-13 下午4:38:14
 */
package com.absir.orm.transaction;

public class TransactionAttribute {

    private boolean readOnly;

    private Class<?>[] rollback;

    private boolean nested;

    private boolean required;

    private int timeout;

    public TransactionAttribute() {
    }

    public TransactionAttribute(boolean readonly, Class<?>[] rollback, boolean nested, boolean required, int timeout) {
        this.readOnly = readonly;
        this.rollback = rollback;
        this.nested = nested;
        this.timeout = timeout;
        if (required == false && ((rollback != null && rollback.length > 0) || timeout > 0)) {
            required = true;
        }

        this.required = required;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public Class<?>[] getRollback() {
        return rollback;
    }

    public boolean isNested() {
        return nested;
    }

    public boolean isRequired() {
        return required;
    }

    public int getTimeout() {
        return timeout;
    }
}
