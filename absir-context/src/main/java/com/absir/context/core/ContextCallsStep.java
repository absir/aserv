package com.absir.context.core;

import com.absir.context.core.value.JaStep;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContextCallsStep<T> extends ContextCalls<T> {

    protected List<Integer> stepIntervals;

    protected int[] stepIntervalArray;

    protected ContextCallsStep() {
    }

    public ContextCallsStep(Class<T> contextClass, Logger logger) {
        super(contextClass, JaStep.class, logger);
    }

    @Override
    protected boolean initMethod(Class<T> contextClass, Method method, Annotation annotation) {
        if (super.initMethod(contextClass, method, annotation)) {
            int interval = annotation.getClass() == JaStep.class ? ((JaStep) annotation).value() : 0;
            if (stepIntervals != null) {
                stepIntervals = new ArrayList<Integer>();
            }

            stepIntervals.add(interval);
            return true;
        }

        return false;
    }

    @Override
    protected void initLogger(Logger logger) {
        if (methods != null) {
            super.initLogger(logger);
            int size = methods.size();
            stepIntervalArray = new int[size];
            for (int i = 0; i < size; i++) {
                stepIntervalArray[i] = stepIntervals.get(i);
            }

            stepIntervals = null;
        }
    }

    public int[] createCallSteps() {
        if (stepIntervalArray == null) {
            return null;
        }

        return new int[stepIntervalArray.length];
    }

    public void doCallStep(T target, int[] steps, Object... params) {
        if (methods != null) {
            int size = methods.size();
            int shortTime = ContextUtils.getContextShortTime();
            for (int i = 0; i < size; i++) {
                if (steps[i] <= shortTime) {
                    steps[i] = shortTime + stepIntervalArray[i];
                    Method method = methods.get(i);
                    try {
                        method.invoke(target, params);

                    } catch (Throwable e) {
                        logger.error("ContextCalls do error at " + method + " : " + Arrays.toString(params), e);
                    }
                }
            }
        }
    }
}
