/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-24 下午9:16:58
 */
package com.absir.core.util;

import com.absir.core.base.Element;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UtilPool<K, V extends Element<? extends K>> {

    private Map<K, V> poolMap;

    private Collection<V> poolCollection;

    public UtilPool(Collection<V> poolCollection) {
        this(null, poolCollection);
    }

    public UtilPool(Map<K, V> poolMap, Collection<V> poolCollection) {
        if (poolMap == null) {
            poolMap = new HashMap<K, V>();
        }

        this.poolMap = poolMap;
        this.poolCollection = poolCollection;
    }

    public Map<K, V> getPoolMap() {
        return poolMap;
    }

    public Collection<V> getPoolCollection() {
        return poolCollection;
    }

    public V get(K key) {
        return poolMap.get(key);
    }

    public boolean add(K key, V value) {
        if (poolCollection.add(value)) {
            poolMap.put(key, value);
            return true;
        }

        return false;
    }

    public V remove(K key) {
        V value = poolMap.remove(key);
        if (value != null) {
            poolCollection.remove(value);
        }

        return value;
    }

    public void addForce(K key, V value) {
        remove(key);
        add(key, value);
    }

    public PoolIterator<K, V> iterator() {
        return new PoolIterator<K, V>(this);
    }

    private static class PoolIterator<K, V extends Element<? extends K>> implements Iterator<V> {

        private Map<K, V> poolMap;

        private V element;

        private Iterator<V> iterator;

        public PoolIterator(UtilPool<K, V> pool) {
            this.poolMap = pool.poolMap;
            this.iterator = pool.poolCollection.iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public V next() {
            element = iterator.next();
            return element;
        }

        public void remove() {
            element.remove();
            iterator.remove();
            poolMap.remove(element.getId());
        }
    }
}
