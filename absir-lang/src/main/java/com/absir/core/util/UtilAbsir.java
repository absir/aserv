/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-14 下午1:31:09
 */
package com.absir.core.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class UtilAbsir {

    public static final int DAY_SHORT = 24 * 3600;

    public static final long DAY_TIME = 24 * 3600000;

    public static final long WEEK_TIME = 7 * DAY_TIME;

    public static final int shortTime(long time) {
        return (int) (time / 1000);
    }

    public static String getId(Class<?> cls, Serializable id) {
        return cls.getName() + '@' + id;
    }

    public static Object getToken(Object id, Map<?, ?> tokenMap) {
        Object token = tokenMap.get(id);
        if (token == null) {
            synchronized (tokenMap) {
                token = tokenMap.get(id);
                if (token == null) {
                    token = new Object();
                    ((Map) tokenMap).put(id, token);
                }
            }
        }

        return token;
    }

    public static Object clearToken(Object id, Map<?, ?> tokenMap) {
        synchronized (tokenMap) {
            return tokenMap.remove(id);
        }
    }

    public static Object getToken(Class<?> cls, Serializable id, Map<?, ?> tokenMap) {
        return getToken(getId(cls, id), tokenMap);
    }

    public static Throwable forCauseThrowable(Throwable e) {
        while (true) {
            Class<?> cls = e.getClass();
            if (cls == InvocationTargetException.class) {
                e = e.getCause();
                continue;
            }

            break;
        }

        return e;
    }

    public static void throwNoRuntimeType(RuntimeException e, Class<? extends Throwable> runtimeType) {
        Throwable throwable = e;
        while (throwable != null) {
            if (throwable.getClass() == runtimeType) {
                return;
            }

            throwable = throwable.getCause();
        }

        throw e;
    }
}
