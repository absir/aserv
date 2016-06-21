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

    private List<T> addList;

    private List<T> removeList;

    public synchronized void add(T element) {
        if (addList == null) {
            addList = new ArrayList<T>();
        }

        addList.add(element);
        if (removeList != null) {
            removeList.remove(element);
        }
    }

    public synchronized void remove(T element) {
        if (addList == null || !addList.remove(element)) {
            if (removeList == null) {
                removeList = new ArrayList<T>();
            }

            removeList.add(element);
        }
    }

    public List<T> syncAdds() {
        List<T> adds = addList;
        if (adds == null || adds.isEmpty()) {
            return null;

        } else {
            synchronized (this) {
                addList = null;
                LinkedList<T> newList = new LinkedList<T>(list);
                newList.addAll(adds);
                list = newList;
            }

            return adds;
        }
    }

    public List<T> syncRemoves() {
        List<T> removes = removeList;
        if (removes == null || removes.isEmpty()) {
            return null;

        } else {
            synchronized (this) {
                removeList = null;
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
