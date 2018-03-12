package com.absir.context.core;

import com.absir.bean.core.BeanConfigImpl;
import com.absir.context.core.value.JaStep;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelReflect;
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

    public static <T> ContextCalls<T>[] ContextCallsArray(final Class<T> contextClass, Logger logger, final Class<? extends Annotation>... annotationClasses) {
        final int length = annotationClasses.length;
        final ContextCalls<T>[] calls = new ContextCalls[length];
        for (int i = 0; i < length; i++) {
            calls[i] = annotationClasses[i] == JaStep.class ? new ContextCallsStep<T>() : new ContextCalls<T>();
        }

        KernelReflect.doWithDeclaredMethods(contextClass, new KernelLang.CallbackBreak<Method>() {

            Annotation annotation;

            @Override
            public void doWith(Method method) throws KernelLang.BreakException {
                for (int i = 0; i < length; i++) {
                    annotation = method.getAnnotation(annotationClasses[i]);
                    if (annotation != null) {
                        calls[i].initMethod(contextClass, method, annotation);
                    }
                }
            }
        });

        for (int i = 0; i < length; i++) {
            calls[i].initLogger(logger);
        }

        return calls;
    }

    protected ContextCalls() {
    }

    public ContextCalls(final Class<T> contextClass, final Class<? extends Annotation> annotationClass, Logger logger) {
        KernelReflect.doWithDeclaredMethods(contextClass, new KernelLang.CallbackBreak<Method>() {

            Annotation annotation;

            @Override
            public void doWith(Method method) throws KernelLang.BreakException {
                annotation = method.getAnnotation(annotationClass);
                if (annotation != null) {
                    initMethod(contextClass, method, annotation);
                }
            }
        });

        initLogger(logger);
    }

    protected boolean initMethod(Class<T> contextClass, Method method, Annotation annotation) {
        Method realMethod = KernelReflect.realMethod(contextClass, method);
        if (realMethod == null && realMethod.getAnnotation(BeanConfigImpl.NoConfigure.class) != null) {
            return false;
        }

        if (methods == null) {
            methods = new ArrayList<Method>();

        } else if (methods.contains(realMethod)) {
            return false;
        }

        methods.add(realMethod);
        return true;
    }

    protected void initLogger(Logger logger) {
        if (methods != null) {
            methods = Collections.unmodifiableList(methods);
        }

        this.logger = logger == null ? LOGGER : logger;
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
