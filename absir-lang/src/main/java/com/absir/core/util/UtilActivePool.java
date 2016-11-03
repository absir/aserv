/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月31日 下午1:58:21
 */
package com.absir.core.util;

import com.absir.core.kernel.KernelByte;
import com.absir.core.kernel.KernelLang.ObjectTemplate;

import java.util.HashMap;
import java.util.Map;

public class UtilActivePool {

    private int index;

    private Map<Integer, ObjectTemplate<Integer>> activeMap = new HashMap<Integer, ObjectTemplate<Integer>>();

    public synchronized ObjectTemplate<Integer> addObject() {
        boolean maxed = false;
        while (true) {
            index++;
            if (index >= KernelByte.VARINTS_4_LENGTH) {
                if (maxed) {
                    return null;
                }

                maxed = true;
                index = 1;
            }

            if (!activeMap.containsKey(index)) {
                break;
            }
        }

        ObjectTemplate<Integer> template = new ObjectTemplate<Integer>(index);
        activeMap.put(index, template);
        return template;
    }

    public synchronized void remove(Integer index) {
        if (index == null) {
            return;
        }

        ObjectTemplate<Integer> value = activeMap.remove(index);
        if (value != null) {
            value.object = null;
        }
    }

    public synchronized void clear() {
        for (ObjectTemplate<Integer> value : activeMap.values()) {
            value.object = null;
        }

        activeMap.clear();
    }

}
