package com.absir.context.log4j;

import com.absir.context.core.ContextUtils;
import com.absir.core.base.Environment;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by absir on 21/8/17.
 */
public class EnvFilter extends Filter {

    private int started;

    public int getStarted() {
        return started;
    }

    public void setStarted(int started) {
        this.started = started;
    }

    @Override
    public int decide(LoggingEvent loggingEvent) {
        return ContextUtils.getStartedCount() > started && Environment.getEnvironment().ordinal() - Environment.DEBUG.ordinal() > 0 ? -1 : 0;
    }

}
