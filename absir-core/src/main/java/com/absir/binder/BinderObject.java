/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-8 下午12:46:06
 */
package com.absir.binder;

import com.absir.property.Property;
import com.absir.property.PropertyObject;

public class BinderObject implements PropertyObject<Binder> {

    private Binder binder;

    public Binder getBinder() {
        return binder;
    }

    public void setBinder(Binder binder) {
        this.binder = binder;
    }

    @Override
    public Binder getPropertyData(String name, Property property) {
        return binder;
    }
}
