/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014年10月22日 下午12:50:39
 */
package com.absir.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author absir
 */
@SuppressWarnings("unchecked")
public class UtilQueue<T> {

    /**
     * index
     */
    protected int index;

    /**
     * length
     */
    protected int capacity;

    /**
     * queues
     */
    protected Object[] queues;

    /**
     * circle
     */
    protected boolean circle;

    /**
     * current
     */
    protected int current;

    /**
     * @param capacity
     */
    public UtilQueue(int capacity) {
        this.capacity = capacity;
        queues = new Object[capacity];
    }

    /**
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * 返回长度
     *
     * @return
     */
    public int getLength() {
        return index == current ? circle ? capacity : 0 : index > current ? (index - current) : (capacity - current + index);
    }

    /**
     * 清除数据
     */
    public synchronized void clear() {
        circle = false;
        index = current = 0;
    }

    /**
     * @param element
     */
    public synchronized void addElement(T element) {
        queues[index] = element;
        if (++index >= capacity) {
            circle = true;
            index = 0;
        }
    }

    /**
     * @return
     */
    public synchronized T readElement() {
        if (index == current) {
            if (circle) {
                circle = false;

            } else {
                return null;
            }
        }

        T element = (T) queues[current];
        queues[current] = null;
        if (++current >= capacity) {
            current = 0;
        }

        return element;
    }

    /**
     * @param max
     * @return
     */
    public synchronized List<T> readElements(int max) {
        int length = 0;
        if (index == current) {
            if (circle) {
                length = getLength();
                circle = false;

            } else {
                return null;
            }

        } else {
            length = getLength();
        }

        if (length > max) {
            length = max;
        }

        List<T> elements = new ArrayList<T>(length);
        while (max-- > 0) {
            elements.add((T) queues[current]);
            queues[current] = null;
            if (++current >= capacity) {
                current = 0;
            }

            if (index == current) {
                break;
            }
        }

        return elements;
    }
}
