package com.absir.core.util;

/**
 * @author absir
 */
public class UtilElement<T> {

    /**
     * element
     */
    protected T element;

    /**
     * next
     */
    protected UtilElement<T> next;

    /**
     * @return the element
     */
    public T getElement() {
        return element;
    }

    /**
     * @return the next
     */
    public UtilElement<T> getNext() {
        return next;
    }

    /**
     * @param element
     */
    public void insert(UtilElement<T> element) {
        element.next = next;
        next = element;
    }

    /**
     *
     */
    public void removeNext() {
        if (next != null) {
            next = next.next;
        }
    }

}
