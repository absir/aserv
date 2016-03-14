/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年10月24日 上午11:51:48
 */
package com.absir.context.core;

import com.absir.context.bean.IStep;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author absir
 */
public class ContextStep implements Runnable {

    /**
     * cancel
     */
    private boolean cancel;

    /**
     * delay
     */
    private long delay;

    /**
     * deltaTime
     */
    private long deltaTime;

    /**
     * steps
     */
    private List<IStep> steps = new LinkedList<IStep>();

    /**
     * addSteps
     */
    private List<IStep> addSteps = new ArrayList<IStep>();

    /**
     * @param delay
     */
    public ContextStep(long delay) {
        this.delay = delay;
    }

    /**
     * @return the deltaTime
     */
    public long getDeltaTime() {
        return deltaTime;
    }

    /**
     * @param step
     */
    public synchronized void addStep(IStep step) {
        addSteps.add(step);
    }

    /**
     *
     */
    public void start() {
        ContextUtils.getThreadPoolExecutor().execute(this);
    }

    /**
     *
     */
    public void cancel() {
        cancel = true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            deltaTime = System.currentTimeMillis();
            while (!cancel) {
                List<IStep> adds = addSteps;
                if (!adds.isEmpty()) {
                    addSteps = new ArrayList<IStep>();
                    synchronized (this) {
                        steps.addAll(adds);
                    }
                }

                long contextTime = System.currentTimeMillis();
                deltaTime = contextTime - deltaTime;
                Iterator<IStep> iterator = steps.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().stepDone(contextTime)) {
                        iterator.remove();
                    }
                }

                deltaTime = contextTime;
                Thread.sleep(delay);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
