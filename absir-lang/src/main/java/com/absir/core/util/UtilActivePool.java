/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月31日 下午1:58:21
 */
package com.absir.core.util;

import com.absir.core.kernel.KernelByte;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

public class UtilActivePool {

    private int index;
    private Map<Integer, ActiveTemplate> activeMap = new HashMap<Integer, ActiveTemplate>();

    public synchronized ActiveTemplate addObject(Closeable closeable) {
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

        ActiveTemplate template = new ActiveTemplate();
        template.object = index;
        template.closeable = closeable;
        activeMap.put(index, template);
        return template;
    }

    public synchronized void remove(Integer index) {
        if (index == null) {
            return;
        }

        ActiveTemplate value = activeMap.remove(index);
        if (value != null) {
            value.object = null;
            UtilPipedStream.closeCloseable(value.closeable);
        }
    }

    public synchronized void clear() {
        for (ActiveTemplate value : activeMap.values()) {
            value.object = null;
            UtilPipedStream.closeCloseable(value.closeable);
        }

        activeMap.clear();
    }

    public static class ActiveTemplate {

        public Integer object;

        protected Closeable closeable;

    }

}
