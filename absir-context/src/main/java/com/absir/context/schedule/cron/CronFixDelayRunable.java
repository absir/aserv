/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-24 下午2:31:28
 */
package com.absir.context.schedule.cron;

import com.absir.context.schedule.ScheduleRunableAbstract;

import java.util.Date;

public class CronFixDelayRunable extends ScheduleRunableAbstract {

    private long fixDelay;

    public CronFixDelayRunable(Runnable runnable, long fixDelay) {
        super(runnable);
        this.fixDelay = fixDelay;
    }

    public long getFixDelay() {
        return fixDelay;
    }

    public void setFixDelay(long fixDelay) {
        this.fixDelay = fixDelay;
    }

    @Override
    public long getNextScheduleTime(Date date) {
        if (fixDelay <= 0) {
            return 0;
        }

        long minTime = date.getTime();
        while (nextTime < minTime) {
            nextTime += fixDelay;
        }

        return nextTime;
    }

    public CronFixDelayRunable transformCronFixDelayRunable(long fixDelay) {
        this.fixDelay = 0;
        return new CronFixDelayRunable(runnable, fixDelay);
    }
}
