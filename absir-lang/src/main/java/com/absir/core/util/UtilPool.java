/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-4-24 下午9:16:58
 */
package com.absir.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.absir.core.base.Element;

/**
 * @author absir
 * 
 */
public class UtilPool<K, V extends Element<? extends K>> {

	/** poolMap */
	private Map<K, V> poolMap;

	/** poolCollection */
	private Collection<V> poolCollection;

	/**
	 * @param poolCollection
	 */
	public UtilPool(Collection<V> poolCollection) {
		this(null, poolCollection);
	}

	/**
	 * @param poolMap
	 * @param poolCollection
	 */
	public UtilPool(Map<K, V> poolMap, Collection<V> poolCollection) {
		if (poolMap == null) {
			poolMap = new HashMap<K, V>();
		}

		this.poolMap = poolMap;
		this.poolCollection = poolCollection;
	}

	/**
	 * @return the poolMap
	 */
	public Map<K, V> getPoolMap() {
		return poolMap;
	}

	/**
	 * @return the poolCollection
	 */
	public Collection<V> getPoolCollection() {
		return poolCollection;
	}

	/**
	 * @param key
	 * @return
	 */
	public V get(K key) {
		return poolMap.get(key);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean add(K key, V value) {
		if (poolCollection.add(value)) {
			poolMap.put(key, value);
			return true;
		}

		return false;
	}

	/**
	 * @param key
	 * @return
	 */
	public V remove(K key) {
		V value = poolMap.remove(key);
		if (value != null) {
			poolCollection.remove(value);
		}

		return value;
	}

	/**
	 * @param key
	 * @param value
	 */
	public void addForce(K key, V value) {
		remove(key);
		add(key, value);
	}

	/**
	 * @author absir
	 * 
	 */
	private static class PoolIterator<K, V extends Element<? extends K>> implements Iterator<V> {

		/** poolMap */
		private Map<K, V> poolMap;

		/** key */
		private V element;

		/** iterator */
		private Iterator<V> iterator;

		/**
		 * @param iterator
		 */
		public PoolIterator(UtilPool<K, V> pool) {
			this.poolMap = pool.poolMap;
			this.iterator = pool.poolCollection.iterator();
		}

		/**
		 * @return
		 */
		public boolean hasNext() {
			return iterator.hasNext();
		}

		/**
		 * @return
		 */
		public V next() {
			element = iterator.next();
			return element;
		}

		/**
		 * @param key
		 */
		public void remove() {
			element.remove();
			iterator.remove();
			poolMap.remove(element.getId());
		}
	}

	/**
	 * @return
	 */
	public PoolIterator<K, V> iterator() {
		return new PoolIterator<K, V>(this);
	}
}
