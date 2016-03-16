/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-24 下午2:31:28
 */
package com.absir.context.schedule.cron;

import java.util.Date;

public class CronExpressionRunable extends CronFixDelayRunable {

    private CronSequenceGenerator cronSequenceGenerator;

    public CronExpressionRunable(Runnable runnable, long fixDelay, String expression) {
        super(runnable, fixDelay);
        cronSequenceGenerator = new CronSequenceGenerator(expression);
    }

    public CronSequenceGenerator getCronSequenceGenerator() {
        return cronSequenceGenerator;
    }

    @Override
    public void start(Date date) {
        nextTime += cronSequenceGenerator.next(nextTime > 0 ? new Date(date.getTime() + nextTime) : date).getTime();
    }

    @Override
    public long getNextScheduleTime(Date date) {
        return cronSequenceGenerator.next(date).getTime() + getFixDelay();
    }
}
