package com.absir.core.util;

public class UtilElement<T> {

    protected T element;

    protected UtilElement<T> next;

    public T getElement() {
        return element;
    }

    public UtilElement<T> getNext() {
        return next;
    }

    public void insert(UtilElement<T> element) {
        element.next = next;
        next = element;
    }

    public void removeNext() {
        if (next != null) {
            next = next.next;
        }
    }

}
