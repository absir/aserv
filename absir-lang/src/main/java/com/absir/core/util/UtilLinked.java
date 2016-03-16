/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年10月24日 上午9:54:54
 */
package com.absir.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class UtilLinked<T> {

    private LinkedList<T> list = new LinkedList<T>();

    private List<T> addList = new ArrayList<T>();

    private List<T> removeList = new ArrayList<T>();

    public synchronized void add(T element) {
        addList.add(element);
        removeList.remove(element);
    }

    public synchronized void remove(T element) {
        if (!addList.remove(element)) {
            removeList.add(element);
        }
    }

    public List<T> syncAdds() {
        List<T> adds = addList;
        if (adds.isEmpty()) {
            return null;

        } else {
            addList = new ArrayList<T>();
            synchronized (this) {
                LinkedList<T> newList = new LinkedList<T>(list);
                newList.addAll(adds);
                list = newList;
            }

            return adds;
        }
    }

    public List<T> syncRemoves() {
        List<T> removes = removeList;
        if (removes.isEmpty()) {
            return null;

        } else {
            removeList = new ArrayList<T>();
            synchronized (this) {
                LinkedList<T> newList = new LinkedList<T>(list);
                newList.removeAll(removes);
                list = newList;
            }

            return removes;
        }
    }

    /**
     * 同步数据
     */
    public void sync() {
        syncAdds();
        syncRemoves();
    }

    public Iterator<T> iterator() {
        return list.iterator();
    }

    public LinkedList<T> getList() {
        return list;
    }
}
