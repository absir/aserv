package com.absir.context.log4j;

import com.absir.bean.inject.value.InjectOrder;
import com.absir.bean.inject.value.Started;
import com.absir.core.base.Environment;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.LoggerFactory;

/**
 * Created by absir on 21/8/17.
 */
public class EnvFilter extends Filter {

    private static int allStarted;

    @InjectOrder()
    @Started
    protected static void Started() {
        LoggerFactory.getLogger(EnvFilter.class).info("EnvFilter.allStarted = " + (++allStarted));
    }

    private int start;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    @Override
    public int decide(LoggingEvent loggingEvent) {
        return allStarted > start && Environment.getEnvironment().ordinal() - Environment.DEBUG.ordinal() > 0 ? -1 : 0;
    }

}
