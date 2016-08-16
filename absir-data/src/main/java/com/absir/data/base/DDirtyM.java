/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月6日 上午10:00:16
 */
package com.absir.data.base;

import com.absir.data.value.IDirty;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class DDirtyM implements IDirty {

    protected transient List<Integer> dirty;

    public void setDirtyI(int index) {
        if (index >= 0) {
            int size = (index >> 5) + 1;
            int pos = index & 0X1F;
            List<Integer> dirty = this.dirty;

            if (dirty == null) {
                dirty = new ArrayList<Integer>(size);
            }

            while (dirty.size() < size) {
                dirty.add(0);
            }

            size--;
            int d = dirty.get(size);
            d |= (0X01 << pos);
            dirty.set(size, d);
            this.dirty = dirty;
        }
    }

    public boolean isDirtyI(int index) {
        if (index >= 0) {
            int size = (index >> 5) + 1;
            int pos = index & 0X1F;
            List<Integer> dirty = this.dirty;
            if (dirty != null && dirty.size() >= size) {
                size--;
                int d = dirty.get(size);
                return (d & (0X01 << pos)) != 0;
            }
        }

        return false;
    }

    @JsonIgnore
    @Override
    public boolean isDirty() {
        return dirty != null;
    }

    @Override
    public void setDirty() {
        if (dirty == null) {
            dirty = new ArrayList<Integer>(1);
        }
    }

    @Override
    public void clearDirty() {
        dirty = null;
    }

}
