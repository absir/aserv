/**
 * Copyright 2015 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2015年10月7日 下午4:05:35
 */
package com.absir.core.util;

import java.util.ArrayList;

/**
 * @author absir
 */
public class UtilObjectPool<T> {

    /**
     * min
     */
    protected int min;
    /**
     * max
     */
    protected int max;
    /**
     * pool
     */
    protected RangeList<T> pool;

    /**
     * @param minSize
     * @param maxSize
     */
    public UtilObjectPool(int minSize, int maxSize) {
        min = minSize;
        max = maxSize;
        pool = new RangeList<T>(maxSize);
    }

    /**
     * @return
     */
    public T getObject() {
        if (pool.isEmpty()) {
            return null;
        }

        synchronized (pool) {
            return pool.remove(pool.size() - 1);
        }
    }

    /**
     * @param obj
     */
    public void freeObject(T obj) {
        synchronized (pool) {
            if (pool.size() < max) {
                pool.add(obj);
            }
        }
    }

    /**
     * @param level(max255)
     */
    public void gc(int level) {
        if (pool.size() > min) {
            synchronized (pool) {
                int size = pool.size();
                int gcCount = size - min;
                if (level > 255 || level < 0) {
                    level = 255;
                }

                gcCount *= (level + 25) / 280.f;
                pool.removeRange(size - gcCount, size);
            }
        }
    }

    @SuppressWarnings("serial")
    public static class RangeList<T> extends ArrayList<T> {

        /**
         *
         */
        public RangeList() {
        }

        /**
         * @param initialCapacity
         */
        public RangeList(int initialCapacity) {
            super(initialCapacity);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.ArrayList#removeRange(int, int)
         */
        @Override
        public void removeRange(int fromIndex, int toIndex) {
            super.removeRange(fromIndex, toIndex);
        }

    }
}
