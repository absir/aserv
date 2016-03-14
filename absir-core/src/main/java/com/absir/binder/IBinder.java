/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月18日 上午11:22:56
 */
package com.absir.binder;

import com.absir.property.PropertyData;

/**
 * @author absir
 */
public interface IBinder {

    /**
     * @param name
     * @param value
     * @param propertyData
     * @param binderData
     */
    public void bind(String name, Object value, PropertyData propertyData, BinderData binderData);

}
