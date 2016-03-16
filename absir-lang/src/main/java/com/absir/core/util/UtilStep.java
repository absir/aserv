/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月26日 上午11:10:30
 */
package com.absir.core.util;

import com.absir.core.base.Environment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class UtilStep extends Thread {

    private int status;

    private long sleepTime;

    private List<IStep> steps = new LinkedList<UtilStep.IStep>();

    private List<IStep> addSteps;

    public UtilStep(boolean closed, long sleep) {
        status = closed ? 0 : 1;
        if (sleep < 1000) {
            sleep = 1000;
        }

        sleepTime = sleep;
    }

    public static UtilStep openUtilStep(boolean daemon, String name, long sleep) {
        return openUtilStep(daemon, name, false, sleep);
    }

    public static UtilStep openUtilStep(boolean daemon, String name, boolean closed, long sleep) {
        UtilStep utilStep = new UtilStep(closed, sleep);
        utilStep.setDaemon(daemon);
        utilStep.setName(name);
        utilStep.start();
        return utilStep;
    }

    @Override
    public synchronized void start() {
        if (status < 2) {
            status += 2;
            super.start();
        }
    }

    public synchronized void close() {
        if (status == 2) {
            status = 0;
        }
    }

    public synchronized void addStep(IStep step) {
        if (addSteps == null) {
            addSteps = new ArrayList<IStep>();
        }

        addSteps.add(step);
    }

    @Override
    public void run() {
        while (Environment.isStarted() && status > 1) {
            try {
                Thread.sleep(sleepTime);

            } catch (Throwable e) {
                break;
            }

            long contextTime = getContextTime();
            Iterator<IStep> iterator = steps.iterator();
            while (iterator.hasNext()) {
                try {
                    if (iterator.next().stepDone(contextTime)) {
                        iterator.remove();
                    }

                } catch (Throwable e) {
                    logThrowable(e);
                }
            }

            if (addSteps != null) {
                synchronized (this) {
                    steps.addAll(addSteps);
                    addSteps = null;
                }
            }
        }

        status = status == 2 ? 0 : 1;
    }

    protected long getContextTime() {
        return UtilContext.getCurrentTime();
    }

    protected void logThrowable(Throwable e) {
        if (Environment.getEnvironment() == Environment.DEVELOP) {
            e.printStackTrace();
        }
    }

    public interface IStep {

        public boolean stepDone(long contextTime);

    }

}
