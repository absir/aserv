/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年9月29日 上午10:26:47
 */
package com.absir.core.util;

public class UtilElementConcurrent<T> extends UtilElement<T> {

    @Override
    public synchronized void insert(UtilElement<T> element) {
        synchronized (element) {
            element.next = next;
        }

        next = element;
    }

    @Override
    public synchronized void removeNext() {
        if (next != null) {
            synchronized (next) {
                next = next.next;
            }
        }
    }

}
