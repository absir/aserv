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

public abstract class ScheduleRunableAbstract extends NextRunableDelay implements ScheduleRunable {

    protected Runnable runnable;

    public ScheduleRunableAbstract(Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public final void run(Date date) {
        nextTime = getNextScheduleTime(date);
        runnable.run();
    }

    public abstract long getNextScheduleTime(Date date);
}
