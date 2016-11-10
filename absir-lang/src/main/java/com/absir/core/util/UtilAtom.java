/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-15 上午11:00:51
 */
package com.absir.core.util;

import com.absir.core.base.Environment;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UtilAtom {

    protected int atomic;

    protected Lock lock = new ReentrantLock();

    protected Condition condition = lock.newCondition();

    public int getAtomic() {
        return atomic;
    }

    public Condition getCondition() {
        return condition;
    }

    public synchronized void increment() {
        ++atomic;
    }

    public synchronized void decrement() {
        if (--atomic == 0) {
            lock.lock();
            condition.signal();
            lock.unlock();
        }
    }

    public void await() {
        if (atomic > 0) {
            lock.lock();
            //UtilDump.TimeoutException exception = null;
            try {
                if (atomic > 0) {
                    //exception = UtilDump.addTimeoutException(3000);
                    condition.await();
                }

            } catch (Exception e) {
                Environment.throwable(e);
            }

//            if (exception != null) {
//                exception.complete();
//            }

            lock.unlock();
        }
    }
}
