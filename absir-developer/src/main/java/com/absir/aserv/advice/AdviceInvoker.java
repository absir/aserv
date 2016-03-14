/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月10日 下午5:43:08
 */
package com.absir.aserv.advice;

/**
 * @author absir
 */
public abstract class AdviceInvoker {

    /**
     * @param value
     * @return
     * @throws Throwable
     */
    public abstract Object invoke(Object value) throws Throwable;
}
