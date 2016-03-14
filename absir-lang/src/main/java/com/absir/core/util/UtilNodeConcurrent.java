/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-1-23 下午8:46:00
 */
package com.absir.core.util;

/**
 * @author absir
 */
public class UtilNodeConcurrent<T> extends UtilNode<T> {

    /**
     *
     */
    public UtilNodeConcurrent() {
    }

    /**
     * @param element
     */
    public UtilNodeConcurrent(T element) {
        super(element);
    }

    /**
     * @param node
     */
    public void beforeAdd(UtilNodeConcurrent<T> node) {
        if (previous != null) {
            synchronized (previous) {
                previous.next = node;
            }
        }

        synchronized (node) {
            node.previous = previous;
            node.next = this;
        }

        synchronized (this) {
            previous = node;
        }
    }

    /**
     * @param node
     */
    public void afterAdd(UtilNodeConcurrent<T> node) {
        if (next != null) {
            synchronized (previous) {
                next.previous = node;
            }
        }

        synchronized (node) {
            node.previous = this;
            node.next = next;
        }

        synchronized (this) {
            next = node;
        }
    }

    /**
     *
     */
    public synchronized void remove() {
        if (previous != null) {
            synchronized (previous) {
                previous.next = next;
            }
        }

        if (next != null) {
            synchronized (next) {
                next.previous = previous;
            }
        }
    }
}
