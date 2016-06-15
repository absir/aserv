/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年10月22日 下午12:50:39
 */
package com.absir.core.util;

import com.absir.core.base.Environment;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UtilQueueBlock<T> extends UtilQueue<T> {

    protected boolean reading;

    protected Lock lock = new ReentrantLock();

    protected Condition condition = lock.newCondition();

    public UtilQueueBlock(int capacity) {
        super(capacity);
    }

    public void cancel() {
        clear();
        readingSignal();
    }

    ;

    @Override
    public void addElement(T element) {
        super.addElement(element);
        readingSignal();
    }

    protected void readingSignal() {
        lock.lock();
        if (reading) {
            reading = false;
            condition.signal();
        }

        lock.unlock();
    }

    protected void readingWaite() throws InterruptedException {
        lock.lock();
        reading = true;
        condition.await();
        lock.unlock();
    }

    @Override
    public T readElement() {
        T element;
        try {
            while (true) {
                element = super.readElement();
                if (element == null) {
                    readingWaite();

                } else {
                    return element;
                }
            }

        } catch (Exception e) {
            Environment.throwable(e);
        }

        return null;
    }

    @Override
    public List<T> readElements(int max) {
        List<T> elements;
        try {
            while (true) {
                elements = super.readElements(max);
                if (elements == null) {
                    readingWaite();

                } else {
                    return elements;
                }
            }

        } catch (Exception e) {
            Environment.throwable(e);
        }

        return null;
    }
}
