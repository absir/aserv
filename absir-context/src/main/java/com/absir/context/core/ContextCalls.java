package com.absir.context.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ContextCalls<T> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ContextCalls.class);

    private List<Method> methods;

    private Logger logger;

    public static <T> ContextCalls<T>[] ContextCallsArray(Class<T> contextClass, Logger logger, Class<? extends Annotation>... annotationClasses) {
        int length = annotationClasses.length;
        ContextCalls<T>[] calls = new ContextCalls[length];
        for (int i = 0; i < length; i++) {
            calls[i] = new ContextCalls<T>();
        }

        for (Method method : contextClass.getDeclaredMethods()) {
            for (int i = 0; i < length; i++) {
                if (method.getAnnotation(annotationClasses[i]) != null) {
                    calls[i].initMethod(method);
                }
            }

        }

        for (int i = 0; i < length; i++) {
            calls[i].initLogger(logger);
        }

        return calls;
    }

    private ContextCalls() {

    }

    private void initMethod(Method method) {
        if (methods == null) {
            methods = new ArrayList<Method>();
        }

        method.setAccessible(true);
        methods.add(method);
    }

    private void initLogger(Logger logger) {
        if (methods != null) {
            methods = Collections.unmodifiableList(methods);
        }

        this.logger = logger == null ? LOGGER : logger;
    }

    public ContextCalls(Class<T> contextClass, Class<? extends Annotation> annotationClass, Logger logger) {
        for (Method method : contextClass.getDeclaredMethods()) {
            if (method.getAnnotation(annotationClass) != null) {
                initMethod(method);
            }
        }

        initLogger(logger);
    }

    public boolean hasCalls() {
        return methods != null;
    }

    public boolean doCalls(T target, Object... params) {
        boolean rTrue = false;
        if (methods != null) {
            for (Method methods : methods) {
                try {
                    if (methods.getReturnType() == boolean.class) {
                        if ((Boolean) methods.invoke(target, params)) {
                            rTrue = true;
                        }

                    } else {
                        methods.invoke(target, params);
                    }

                } catch (Throwable e) {
                    logger.error("ContextCalls do error at " + methods + " : " + Arrays.toString(params), e);
                }
            }
        }

        return rTrue;
    }

}
