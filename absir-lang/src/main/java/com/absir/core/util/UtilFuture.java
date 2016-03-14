/**
 * Copyright 2015 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2015年8月22日 下午2:12:52
 */
package com.absir.core.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author absir
 */
public class UtilFuture<T> {

    /**
     * futureCount
     */
    private AtomicInteger futureCount = new AtomicInteger();

    /**
     * bean
     */
    private T bean;

    /**
     * @return
     */
    public T getBean() {
        return getBean(0);
    }

    /**
     * @param bean the bean to set
     */
    public void setBean(T bean) {
        this.bean = bean;
        if (futureCount.incrementAndGet() <= 0) {
            synchronized (this) {
                notify();
            }
        }
    }

    /**
     * @return the bean
     */
    public T getBean(long timeout) {
        if (futureCount.decrementAndGet() < 0) {
            try {
                synchronized (this) {
                    wait(timeout < 0 ? 0 : timeout);
                }

            } catch (InterruptedException e) {
            }
        }

        return bean;
    }
}
