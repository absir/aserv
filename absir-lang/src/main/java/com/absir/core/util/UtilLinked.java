/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年10月24日 上午9:54:54
 */
package com.absir.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author absir
 *
 */
public class UtilLinked<T> {

	/** list */
	private LinkedList<T> list = new LinkedList<T>();

	/** addList */
	private List<T> addList = new ArrayList<T>();

	/** removeList */
	private List<T> removeList = new ArrayList<T>();

	/**
	 * @param element
	 */
	public synchronized void add(T element) {
		addList.add(element);
		removeList.remove(element);
	}

	/**
	 * @param element
	 */
	public synchronized void remove(T element) {
		if (!addList.remove(element)) {
			removeList.add(element);
		}
	}

	/**
	 * @return
	 */
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

	/**
	 * @return
	 */
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

	/**
	 * @return
	 */
	public Iterator<T> iterator() {
		return list.iterator();
	}

	/**
	 * @return the list
	 */
	public LinkedList<T> getList() {
		return list;
	}
}
