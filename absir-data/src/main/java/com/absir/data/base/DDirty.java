/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月6日 上午10:00:09
 */
package com.absir.data.base;

import com.absir.data.value.IDirty;

public class DDirty implements IDirty {

    protected transient boolean dirty;

    public void setDirtyI(int index) {
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean isDirtyI(int index) {
        return dirty;
    }

    @Override
    public void setDirty() {
        dirty = true;
    }

    @Override
    public void clearDirty() {
        dirty = false;
    }

}
