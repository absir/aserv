package com.absir.context.core;

import com.absir.context.core.value.JaStep;
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

    protected List<Method> methods;

    protected Logger logger;

    public static <T> ContextCalls<T>[] ContextCallsArray(Class<T> contextClass, Logger logger, Class<? extends Annotation>... annotationClasses) {
        int length = annotationClasses.length;
        ContextCalls<T>[] calls = new ContextCalls[length];
        for (int i = 0; i < length; i++) {
            calls[i] = annotationClasses[i] == JaStep.class ? new ContextCallsStep<T>() : new ContextCalls<T>();
        }

        Annotation annotation;
        for (Method method : contextClass.getDeclaredMethods()) {
            for (int i = 0; i < length; i++) {
                annotation = method.getAnnotation(annotationClasses[i]);
                if (annotation != null) {
                    calls[i].initMethod(method, annotation);
                }
            }
        }

        for (int i = 0; i < length; i++) {
            calls[i].initLogger(logger);
        }

        return calls;
    }

    protected ContextCalls() {

    }

    protected void initMethod(Method method, Annotation annotation) {
        if (methods == null) {
            methods = new ArrayList<Method>();
        }

        method.setAccessible(true);
        methods.add(method);
    }

    protected void initLogger(Logger logger) {
        if (methods != null) {
            methods = Collections.unmodifiableList(methods);
        }

        this.logger = logger == null ? LOGGER : logger;
    }

    public ContextCalls(Class<T> contextClass, Class<? extends Annotation> annotationClass, Logger logger) {
        Annotation annotation;
        for (Method method : contextClass.getDeclaredMethods()) {
            annotation = method.getAnnotation(annotationClass);
            if (annotation != null) {
                initMethod(method, annotation);
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
            for (Method method : methods) {
                try {
                    if (method.getReturnType() == boolean.class) {
                        if ((Boolean) method.invoke(target, params)) {
                            rTrue = true;
                        }

                    } else {
                        method.invoke(target, params);
                    }

                } catch (Throwable e) {
                    logger.error("ContextCalls do error at " + method + " : " + Arrays.toString(params), e);
                }
            }
        }

        return rTrue;
    }

}
