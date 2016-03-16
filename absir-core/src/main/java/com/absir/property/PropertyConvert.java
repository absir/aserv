/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-2-12 下午4:08:01
 */
package com.absir.property;

public interface PropertyConvert {

    public Object getValue(Object propertyValue);

    public Object getPropertyValue(Object value, String beanName);

}
