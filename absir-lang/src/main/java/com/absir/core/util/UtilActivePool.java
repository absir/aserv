/**
 * Copyright 2015 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2015年10月31日 下午1:58:21
 */
package com.absir.core.util;

import com.absir.core.kernel.KernelLang.ObjectTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author absir
 */
public class UtilActivePool {

    /**
     * index
     */
    private int index;

    /**
     * activeMap
     */
    private Map<Integer, ObjectTemplate<Integer>> activeMap = new HashMap<Integer, ObjectTemplate<Integer>>();

    /**
     * @return
     */
    public synchronized ObjectTemplate<Integer> addObject() {
        boolean maxed = false;
        while (activeMap.containsKey(index)) {
            if (index >= Integer.MAX_VALUE) {
                if (maxed) {
                    return null;
                }

                maxed = true;
                index = 0;

            } else {
                index++;
            }
        }

        ObjectTemplate<Integer> template = new ObjectTemplate<Integer>(index);
        activeMap.put(index, template);
        return template;
    }

    /**
     * @param index
     */
    public synchronized void remove(Integer index) {
        if (index == null) {
            return;
        }

        ObjectTemplate<Integer> value = activeMap.remove(index);
        if (value != null) {
            value.object = null;
        }
    }

    /**
     *
     */
    public synchronized void clear() {
        for (ObjectTemplate<Integer> value : activeMap.values()) {
            value.object = null;
        }

        activeMap.clear();
    }

}
