/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-15 下午5:41:36
 */
package com.absir.core.kernel;

import java.lang.reflect.Array;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class KernelCollection {

    public static <T> T getAssignable(Collection<?> collection, Class<T> cls) {
        for (Object ele : collection) {
            if (cls.isAssignableFrom(ele.getClass())) {
                return (T) ele;
            }
        }

        return null;
    }

    public static <T> boolean contain(List<T> collection, T element) {
        for (T ele : collection) {
            if (ele == element) {
                return true;
            }
        }

        return false;
    }

    public static <T> T anyObject(Collection<? extends T> collection) {
        if (collection == null) {
            return null;
        }

        for (T el : collection) {
            return el;
        }

        return null;
    }

    public static <T> T[] toArray(Collection<? extends T> collection, Class<T> componentType) {
        if (collection == null) {
            return null;
        }

        T[] array = (T[]) Array.newInstance(componentType, collection.size());
        int i = 0;
        for (T el : collection) {
            array[i++] = el;
        }

        return array;
    }

    public static <T> T[] castToArray(Collection collection, Class<T> componentType) {
        if (collection == null) {
            return null;
        }

        T[] array = (T[]) Array.newInstance(componentType, collection.size());
        int i = 0;
        for (Object el : collection) {
            array[i++] = KernelDyna.to(el, componentType);
        }

        return array;
    }

    public static <T> void addAll(Collection<T> list, T[] array) {
        for (T value : array) {
            list.add(value);
        }
    }

    public static boolean equals(Collection collection, Collection to) {
        if (collection == to) {
            return true;
        }

        if (collection == null || to == null) {
            return false;
        }

        Iterator iterator = collection.iterator();
        Iterator iteratorTo = to.iterator();
        while (iterator.hasNext()) {
            if (!iteratorTo.hasNext() || !KernelObject.equals(iterator.next(), iteratorTo.next())) {
                return false;
            }
        }

        return iteratorTo.hasNext() ? false : true;
    }

    public static void copy(Collection collection, Collection to) {
        for (Object el : collection) {
            to.add(el);
        }
    }

    public static Map toMap(Collection collection) {
        if (collection == null) {
            return null;
        }

        Map map = new HashMap();
        Object key = null;
        for (Object el : collection) {
            if (key == null) {
                key = el;

            } else {
                map.put(key, el);
                key = null;
            }
        }

        return map;
    }
}
