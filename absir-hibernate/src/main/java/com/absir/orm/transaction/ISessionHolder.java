/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-3 下午6:46:05
 */
package com.absir.orm.transaction;

public interface ISessionHolder {

    public boolean isRequired();

    public boolean isReadOnly();

    public long getTimeout();

    public void close(Throwable e);
}
