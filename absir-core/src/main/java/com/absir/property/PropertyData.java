/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-6 下午5:30:37
 */
package com.absir.property;

public class PropertyData {

    private Property property;

    private Object[] propertyDatas;

    public PropertyData(Class<?> beanClass, String name, int include, int exclude, String beanName, Class<? extends PropertyFactory> factoryClass) {
        property = new Property(beanClass, name, include, exclude, beanName, factoryClass);
        propertyDatas = new Object[PropertySupply.getSupplySize()];
    }

    public Property getProperty() {
        return property;
    }

    public Object[] getPropertyDatas() {
        return propertyDatas;
    }
}
