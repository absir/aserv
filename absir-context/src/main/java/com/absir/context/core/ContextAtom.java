/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-15 上午10:05:23
 */
package com.absir.context.core;

import com.absir.core.base.Environment;
import com.absir.core.util.UtilAtom;

import java.util.concurrent.locks.Condition;

public class ContextAtom extends UtilAtom {

    private int maxAtom;

    private boolean maxAwait;

    private Condition maxCondition = lock.newCondition();

    public ContextAtom() {
        this(Integer.MAX_VALUE);
    }

    public ContextAtom(int maxAtom) {
        this.maxAtom = maxAtom;
    }

    @Override
    public void increment() {
        lock.lock();
        if (++atomic > maxAtom) {
            maxAwait = true;
            try {
                maxCondition.await();
                return;

            } catch (Exception e) {
                if (Environment.getEnvironment() == Environment.DEVELOP) {
                    e.printStackTrace();
                }
            }
        }

        lock.unlock();
    }

    @Override
    public void decrement() {
        lock.lock();
        if (--atomic == 0) {
            condition.signal();
        }

        if (maxAwait && atomic < maxAtom) {
            maxAwait = false;
            maxCondition.signal();
        }

        lock.unlock();
    }
}
