/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-3-5 下午2:31:23
 */
package com.absir.core.kernel;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author absir
 * 
 */
public abstract class KernelMap {

	/**
	 * @param map
	 * @return
	 */
	public static <K, V> K key(Map<K, V> map) {
		if (map != null) {
			Iterator<Entry<K, V>> iterator = map.entrySet().iterator();
			if (iterator.hasNext()) {
				return iterator.next().getKey();
			}
		}

		return null;
	}

	/**
	 * @param map
	 * @return
	 */
	public static <K, V> V value(Map<K, V> map) {
		if (map != null) {
			Iterator<Entry<K, V>> iterator = map.entrySet().iterator();
			if (iterator.hasNext()) {
				return iterator.next().getValue();
			}
		}

		return null;
	}

	/**
	 * @param map
	 * @return
	 */
	public static <K, V> Entry<K, V> entry(Map<K, V> map) {
		if (map != null) {
			Iterator<Entry<K, V>> iterator = map.entrySet().iterator();
			if (iterator.hasNext()) {
				return iterator.next();
			}
		}

		return null;
	}

	/**
	 * @param map
	 * @param key
	 * @return
	 */
	public static <V> V get(Map<?, V> map, Object key) {
		if (key == null) {
			return null;
		}

		return map.get(key);
	}

	/**
	 * @param map
	 * @param value
	 * @return
	 */
	public static <K> K getKey(Map<K, ?> map, Object value) {
		for (Entry<K, ?> entry : map.entrySet()) {
			if (KernelObject.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}

		return null;
	}

	/**
	 * @param map
	 * @param to
	 */
	public static void copy(Map<Object, Object> map, Map<Object, Object> to) {
		for (Entry<Object, Object> entry : map.entrySet()) {
			to.put(entry.getKey(), entry.getValue());
		}
	}
}
