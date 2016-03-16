/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-5 下午2:31:23
 */
package com.absir.core.kernel;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class KernelEnum {

    public static Object[] values(Class<? extends Enum> cls) {
        return (Object[]) KernelObject.send(cls, "values");
    }

    public static <T> T[] toArray(Class<? extends Enum> cls, Class T) {
        return toArray(cls, "getValue", T);
    }

    public static <T> T[] toArray(Class<? extends Enum> cls, String valueMethodName, Class T) {
        try {
            Method method = cls.getMethod(valueMethodName);
            return toArray(cls, method, T);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T[] toArray(Class<? extends Enum> cls, Method valueMethod, Class T) {
        Enum[] enums = cls.getEnumConstants();
        int length = enums.length;
        T[] array = (T[]) Array.newInstance(T, length);
        try {
            for (int i = 0; i < length; i++) {
                array[i] = (T) valueMethod.invoke(enums[i]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return array;
    }
}
