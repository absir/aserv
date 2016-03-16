/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-23 下午8:46:00
 */
package com.absir.core.util;

import com.absir.core.kernel.KernelList.Orderable;

public class UtilNode<T> {

    protected T element;

    protected UtilNode<T> previous;

    protected UtilNode<T> next;

    public UtilNode() {
    }

    public UtilNode(T element) {
        this.element = element;
    }

    public static <T extends Orderable> void insertOrderableNode(UtilNode<T> orderableHeader, T orderable) {
        insertOrderableNode(orderableHeader, new UtilNode<T>(orderable));
    }

    public static <T extends Orderable> void insertOrderableNode(UtilNode<T> orderableHeader,
                                                                 UtilNode<T> orderableNode) {
        int order = orderableNode.element.getOrder();
        UtilNode<T> orderableNodeNext;
        while (true) {
            orderableNodeNext = orderableHeader.getNext();
            if (orderableNodeNext == null || order <= orderableNodeNext.getElement().getOrder()) {
                orderableHeader.afterAdd(orderableNode);
                break;
            }

            orderableHeader = orderableNodeNext;
        }
    }

    public static <T extends Orderable> void insertOrderableNodeFooter(UtilNode<T> orderableFooter, T orderable) {
        insertOrderableNodeFooter(orderableFooter, new UtilNode<T>(orderable));
    }

    public static <T extends Orderable> void insertOrderableNodeFooter(UtilNode<T> orderableFooter,
                                                                       UtilNode<T> orderableNode) {
        int order = orderableNode.element.getOrder();
        UtilNode<T> orderableNodePre;
        while (true) {
            if (orderableFooter.getElement() == null || orderableFooter.getElement().getOrder() <= order) {
                orderableFooter.afterAdd(orderableNode);
                break;
            }

            orderableNodePre = orderableFooter.getPrevious();
            if (orderableNodePre == null) {
                orderableFooter.beforeAdd(orderableNode);
                break;
            }

            orderableFooter = orderableNodePre;
        }
    }

    public static <T extends Orderable> void sortOrderableNode(UtilNode<T> orderableNode) {
        int order = orderableNode.getElement().getOrder();
        UtilNode<T> orderableNodeCompare = orderableNode.getNext();
        if (orderableNodeCompare == null || order <= orderableNodeCompare.getElement().getOrder()) {
            orderableNodeCompare = orderableNode;
            T orderableCompare;
            while (true) {
                orderableNodeCompare = orderableNodeCompare.getPrevious();
                orderableCompare = orderableNodeCompare.getElement();
                if (orderableCompare == null || order >= orderableCompare.getOrder()) {
                    orderableNodeCompare.afterInsert(orderableNode);
                    break;
                }
            }

        } else {
            UtilNode<T> orderableNodeNext = orderableNode.getNext();
            while (true) {
                if (order <= orderableNodeCompare.getElement().getOrder()) {
                    orderableNodeCompare.beforeInsert(orderableNode);
                    break;
                }

                orderableNodeNext = orderableNodeCompare.getNext();
                if (orderableNodeNext == null) {
                    orderableNodeCompare.afterInsert(orderableNode);
                    break;
                }

                orderableNodeCompare = orderableNodeNext;
            }
        }
    }

    public static <T extends Orderable> void sortOrderableNodeAll(UtilNode<T> orderableNodeHeader) {
        if (orderableNodeHeader.getElement() == null) {
            orderableNodeHeader = orderableNodeHeader.getNext();
            if (orderableNodeHeader == null) {
                return;
            }
        }

        while (orderableNodeHeader != null) {
            sortOrderableNode(orderableNodeHeader);
            orderableNodeHeader = orderableNodeHeader.getNext();
        }
    }

    public T getElement() {
        return element;
    }

    public void setElement(T element) {
        this.element = element;
    }

    public UtilNode<T> getPrevious() {
        return previous;
    }

    public UtilNode<T> getNext() {
        return next;
    }

    public void beforeAdd(UtilNode<T> node) {
        if (previous != null) {
            previous.next = node;
        }

        node.previous = previous;
        node.next = this;
        previous = node;
    }

    public void afterAdd(UtilNode<T> node) {
        if (next != null) {
            next.previous = node;
        }

        node.previous = this;
        node.next = next;
        next = node;
    }

    public void remove() {
        if (previous != null) {
            previous.next = next;
        }

        if (next != null) {
            next.previous = previous;
        }
    }

    public void beforeInsert(UtilNode<T> node) {
        node.remove();
        beforeAdd(node);
    }

    public void afterInsert(UtilNode<T> node) {
        node.remove();
        afterAdd(node);
    }
}
