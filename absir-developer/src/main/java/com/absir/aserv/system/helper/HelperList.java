/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-21 下午12:44:44
 */
package com.absir.aserv.system.helper;

import java.util.Collection;
import java.util.List;

/**
 * @author absir
 * 
 */
public class HelperList {

	/**
	 * @param list
	 * @param index
	 * @param targets
	 */
	public static <T> void listTargets(List<T> list, int count, Collection<T> targets) {
		listTargets(list, null, count, targets);
	}

	/**
	 * @param list
	 * @param target
	 * @param count
	 * @param targets
	 */
	public static <T> void listTargets(List<T> list, T target, int count, Collection<T> targets) {
		int index = target == null ? 0 : list.indexOf(target);
		if (index < 0) {
			index = 0;
		}

		int size = list.size();
		int last = index + size;
		for (int i = index; i < last; i++) {
			index = i >= size ? i - size : i;
			targets.add(list.get(index));
			if (--count <= 0) {
				break;
			}
		}
	}

}
