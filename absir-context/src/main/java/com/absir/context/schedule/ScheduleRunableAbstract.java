/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-23 下午12:51:31
 */
package com.absir.context.schedule;

import com.absir.core.util.UtilSchelduer.NextRunableDelay;

import java.util.Date;

/**
 * @author absir
 */
public abstract class ScheduleRunableAbstract extends NextRunableDelay implements ScheduleRunable {

    /**
     * runnable
     */
    protected Runnable runnable;

    /**
     * @param runnable
     */
    public ScheduleRunableAbstract(Runnable runnable) {
        this.runnable = runnable;
    }

    /**
     * @return the runnable
     */
    public Runnable getRunnable() {
        return runnable;
    }

    /**
     * @param date
     */
    public final void run(Date date) {
        nextTime = getNextScheduleTime(date);
        runnable.run();
    }

    /**
     * @return
     */
    public abstract long getNextScheduleTime(Date date);
}
