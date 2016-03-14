/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-1-15 上午11:00:51
 */
package com.absir.core.util;

import com.absir.core.base.Environment;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author absir
 */
public class UtilAtom {

    /**
     * atomic
     */
    protected int atomic;

    /**
     * lock
     */
    protected Lock lock = new ReentrantLock();

    /**
     * condition
     */
    protected Condition condition = lock.newCondition();

    /**
     * @return the atomic
     */
    public int getAtomic() {
        return atomic;
    }

    /**
     * @return the condition
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     *
     */
    public synchronized void increment() {
        ++atomic;
    }

    /**
     *
     */
    public synchronized void decrement() {
        if (--atomic == 0) {
            lock.lock();
            condition.signal();
            lock.unlock();
        }
    }

    /**
     *
     */
    public void await() {
        if (atomic > 0) {
            lock.lock();
            try {
                if (atomic > 0) {
                    condition.await();
                }

            } catch (Exception e) {
                if (Environment.getEnvironment() == Environment.DEVELOP) {
                    e.printStackTrace();
                }
            }

            lock.unlock();
        }
    }
}
