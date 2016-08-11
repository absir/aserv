/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-5 下午2:31:23
 */
package com.absir.core.kernel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class KernelMap {

    public static <K, V> K key(Map<K, V> map) {
        if (map != null) {
            Iterator<Entry<K, V>> iterator = map.entrySet().iterator();
            if (iterator.hasNext()) {
                return iterator.next().getKey();
            }
        }

        return null;
    }

    public static <K, V> V value(Map<K, V> map) {
        if (map != null) {
            Iterator<Entry<K, V>> iterator = map.entrySet().iterator();
            if (iterator.hasNext()) {
                return iterator.next().getValue();
            }
        }

        return null;
    }

    public static <K, V> Entry<K, V> entry(Map<K, V> map) {
        if (map != null) {
            Iterator<Entry<K, V>> iterator = map.entrySet().iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
        }

        return null;
    }

    public static <V> V get(Map<?, V> map, Object key) {
        if (key == null) {
            return null;
        }

        return map.get(key);
    }

    public static <K> K getKey(Map<K, ?> map, Object value) {
        for (Entry<K, ?> entry : map.entrySet()) {
            if (KernelObject.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }

        return null;
    }

    public static void copy(Map<Object, Object> map, Map<Object, Object> to) {
        for (Entry<Object, Object> entry : map.entrySet()) {
            to.put(entry.getKey(), entry.getValue());
        }
    }

    public static Map<Object, Object> newMap(Object key, Object value) {
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put(key, value);
        return map;
    }

    public static Map<Object, Object> newMapKeysValues(Object... keysValues) {
        Map<Object, Object> map = new HashMap<Object, Object>();
        int len = keysValues.length;
        for (int i = 1; i < len; i += 2) {
            map.put(keysValues[i - 1], keysValues[i]);
        }

        return map;
    }
}
