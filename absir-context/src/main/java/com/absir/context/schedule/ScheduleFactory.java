/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-23 下午12:49:50
 */
package com.absir.context.schedule;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.IMethodInject;
import com.absir.bean.inject.InjectMethod;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Started;
import com.absir.bean.inject.value.Stopping;
import com.absir.bean.inject.value.Value;
import com.absir.context.config.BeanMethodRunable;
import com.absir.context.schedule.cron.CronExpressionRunable;
import com.absir.context.schedule.cron.CronFixDelayRunable;
import com.absir.context.schedule.value.Schedule;
import com.absir.core.base.Environment;
import com.absir.core.util.UtilSchelduer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author absir
 */
@Bean
public class ScheduleFactory extends UtilSchelduer<ScheduleRunable> implements IMethodInject<Schedule> {

    /**
     * LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleFactory.class);

    @Value(value = "schedule.max")
    private long scheduleMax = 300000;

    /**
     *
     */
    @Started
    protected void started() {
        start();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.core.util.UtilSchelduer#logThrowable(java.lang.Throwable)
     */
    @Override
    protected void logThrowable(Throwable e) {
        LOGGER.error("failed!", e);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.core.util.UtilSchelduer#getMaxSleepTime()
     */
    @Override
    protected long getMaxSleepTime() {
        return scheduleMax;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.inject.IMethodInject#isRequired()
     */
    @Override
    public boolean isRequired() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.inject.IMethodInject#getInjects(com.absir.bean.basis.
     * BeanScope , com.absir.bean.basis.BeanDefine, java.lang.reflect.Method)
     */
    @Override
    public Schedule getInjects(BeanScope beanScope, BeanDefine beanDefine, Method method) {
        return method.getAnnotation(Schedule.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.inject.IMethodInject#setInjectMethod(java.lang.Object,
     * java.lang.reflect.Method, java.lang.Object,
     * com.absir.bean.inject.InjectMethod)
     */
    @Override
    public void setInjectMethod(Schedule inject, Method method, Object beanObject, InjectMethod injectMethod) {
        ScheduleRunableAbstract scheduleRunable;
        BeanMethodRunable beanMethodRunable = new BeanMethodRunable(beanObject, injectMethod);
        if ("".equals(inject.cron())) {
            scheduleRunable = new CronFixDelayRunable(beanMethodRunable, inject.fixedDelay());

        } else {
            scheduleRunable = new CronExpressionRunable(beanMethodRunable, inject.fixedDelay(), inject.cron());
        }

        if (BeanFactoryUtils.getEnvironment().compareTo(Environment.DEBUG) <= 0) {
            LOGGER.info(beanObject + " => " + injectMethod.getMethod());
        }

        scheduleRunable.setNextTime(inject.initialDelay());
        addNextRunableNode(scheduleRunable);
    }

    /**
     * 关闭线程
     */
    @Stopping
    protected void stopping() {
        stopNow();
    }
}
