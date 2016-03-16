/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月22日 下午2:12:52
 */
package com.absir.core.util;

import java.util.concurrent.atomic.AtomicInteger;

public class UtilFuture<T> {

    private AtomicInteger futureCount = new AtomicInteger();

    private T bean;

    public T getBean() {
        return getBean(0);
    }

    public void setBean(T bean) {
        this.bean = bean;
        if (futureCount.incrementAndGet() <= 0) {
            synchronized (this) {
                notify();
            }
        }
    }

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
