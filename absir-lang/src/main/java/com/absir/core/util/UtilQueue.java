/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年10月22日 下午12:50:39
 */
package com.absir.core.util;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class UtilQueue<T> {

    protected int index;

    protected int capacity;

    protected Object[] queues;

    protected boolean circle;

    protected int current;

    public UtilQueue(int capacity) {
        this.capacity = capacity;
        queues = new Object[capacity];
    }

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

    public synchronized void addElement(T element) {
        queues[index] = element;
        if (++index >= capacity) {
            circle = true;
            index = 0;
        }
    }

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
