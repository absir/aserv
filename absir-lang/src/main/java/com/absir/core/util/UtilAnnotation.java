/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-2 下午10:35:36
 */
package com.absir.core.util;

import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class UtilAnnotation {

    public static void copy(Annotation obj, Object copy) {
        final Class cls = copy.getClass();
        for (Method method : obj.getClass().getMethods()) {
            if (method.getParameterTypes().length == 0) {
                Field field = KernelReflect.declaredField(cls, method.getName());
                if (field != null && field.getType().isAssignableFrom(method.getReturnType())) {
                    KernelReflect.set(copy, field, KernelReflect.invoke(obj, method));
                }
            }
        }
    }

    public static Object defaultValue(Class<? extends Annotation> annotationClass, String value) {
        Method method = KernelReflect.declaredMethod(annotationClass, value);
        if (method != null) {
            return method.getDefaultValue();
        }

        return null;
    }

    public static <T extends Annotation> T clone(T annotation) {
        return clone(annotation, null);
    }

    public static <T extends Annotation> T clone(T annotation, Map<String, Object> memberValues) {
        if (memberValues == null) {
            memberValues = new HashMap<String, Object>();
        }

        for (Method method : annotation.getClass().getMethods()) {
            if (!memberValues.containsKey(method.getName())) {
                memberValues.put(method.getName(), KernelReflect.invoke(annotation, method));
            }
        }

        return (T) newInstance(annotation.getClass(), memberValues);
    }

    public static <T extends Annotation> T newInstance(Class<T> annotationClass, Map<String, Object> memberValues) {
        return (T) Proxy.newProxyInstance(AnnotationHandler.class.getClassLoader(), new Class[]{annotationClass}, new AnnotationHandler(annotationClass, memberValues));
    }

    public static <T extends Annotation> T newInstance(Class<T> annotationClass, Collection<?> values) {
        Map<String, Object> memberValues = new HashMap<String, Object>();
        int i = 0;
        Iterator<?> iterator = values.iterator();
        for (Method method : annotationClass.getMethods()) {
            if (iterator.hasNext()) {
                memberValues.put(method.getName(), DynaBinder.to(iterator.next(), method.getReturnType()));

            } else {
                break;
            }
        }

        return newInstance(annotationClass, memberValues);
    }

    public static <T extends Annotation> T newInstance(Class<T> annotationClass, Object[] values, int offset) {
        Map<String, Object> memberValues = new HashMap<String, Object>();
        int i = offset < 0 ? 0 : offset;
        int length = values.length;
        for (Method method : annotationClass.getMethods()) {
            if (i < length) {
                memberValues.put(method.getName(), DynaBinder.to(values[i], method.getReturnType()));

            } else {
                break;
            }

            i++;
        }

        return newInstance(annotationClass, memberValues);
    }

    public static class AnnotationHandler extends UtilAbstractHandler {

        private Class<? extends Annotation> annotationClass;

        private Map<String, Object> memberValues;

        public AnnotationHandler(Class<? extends Annotation> annotationClass, Map<String, Object> memberValues) {
            this.annotationClass = annotationClass;
            this.memberValues = memberValues;
        }

        @Override
        public Object invoke(Object object, String methodName, Object[] args) {
            if (memberValues.containsKey(methodName)) {
                Object result = memberValues.get(methodName);
                if (result.getClass().isArray()) {
                    result = KernelArray.clone(result);
                }

                return result;

            } else {
                Method method = KernelReflect.declaredMethod(annotationClass, methodName);
                if (method == null) {
                    return KernelLang.NULL_OBJECT;

                } else {
                    return method.getDefaultValue();
                }
            }
        }
    }
}
