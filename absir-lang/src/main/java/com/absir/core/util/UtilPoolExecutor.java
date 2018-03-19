package com.absir.core.util;

import com.absir.core.base.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class UtilPoolExecutor<S, E> implements Runnable {

    private int maxAdd;

    private int threadCount;

    private List<E> adds;

    public UtilPoolExecutor(int maxAdd, int threadCount) {
        this.maxAdd = maxAdd;
        this.threadCount = threadCount < 1 ? 1 : threadCount;
    }

    protected void addBefore(E element) {
    }

    protected ThreadPoolExecutor getThreadPoolExecutor() {
        return UtilContext.getThreadPoolExecutor();
    }

    public final synchronized void addRunElement(E element) {
        addBefore(element);
        if (adds == null) {
            adds = new ArrayList<E>();

        } else if (maxAdd > 0 && adds.size() >= maxAdd) {
            throw new RejectedExecutionException();
        }

        adds.add(element);
        if (threadCount > 0) {
            threadCount--;
            try {
                getThreadPoolExecutor().execute(this);

            } catch (Throwable e) {
                threadCount++;
                Environment.throwable(e);
            }
        }
    }

    protected abstract S runBefore();

    protected abstract void runFinally(S session);

    protected abstract void runElement(S session, E element);

    @Override
    public void run() {
        boolean befored = false;
        S session = null;
        List<E> elements;
        try {
            while (true) {
                synchronized (this) {
                    if (adds == null) {
                        break;
                    }

                    elements = adds;
                    adds = null;
                }

                if (!befored) {
                    befored = true;
                    session = runBefore();
                }

                for (E element : elements) {
                    runElement(session, element);
                }
            }

        } finally {
            threadCount++;
            if (befored) {
                runFinally(session);
            }
        }
    }

}
