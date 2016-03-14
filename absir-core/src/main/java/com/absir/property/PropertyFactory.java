/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-2-12 下午4:08:01
 */
package com.absir.property;

/**
 * @author absir
 */
public interface PropertyFactory {

    /**
     * @param property
     * @return
     */
    public PropertyConvert getPropertyConvert(Property property);

    /**
     * @author absir
     */
    public interface Void extends PropertyFactory {

    }
}
