/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-3 下午6:46:05
 */
package com.absir.orm.transaction;

/**
 * @author absir
 */
public interface ISessionHolder {

    /**
     * @return
     */
    public boolean isRequired();

    /**
     * @return
     */
    public boolean isReadOnly();

    /**
     * @return
     */
    public long getTimeout();

    /**
     * @param e
     */
    public void close(Throwable e);
}
