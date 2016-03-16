/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-6 下午5:30:37
 */
package com.absir.property;

import com.absir.core.kernel.KernelList.Orderable;
import com.absir.core.kernel.KernelString;
import com.absir.property.value.Prop;

@SuppressWarnings("rawtypes")
public class PropertyContext implements Orderable {

    PropertyObject propertyObject;

    String name;

    int order;

    int include;

    int exclude;

    String beanName;

    boolean ignore;

    Class<? extends PropertyFactory> factoryClass;

    @Override
    public int getOrder() {
        return order;
    }

    public void prop(Prop prop) {
        if (prop != null) {
            if (!KernelString.isEmpty(prop.name())) {
                name = prop.name();
            }

            order = prop.orderProp() ? prop.order() : (order + prop.order());
            include = prop.includeProp() ? prop.include() : (include | prop.include());
            exclude = prop.excludeProp() ? prop.exclude() : (exclude | prop.exclude());
            if (prop.ignore() != 0) {
                ignore = prop.ignore() > 0 ? true : false;
            }

            if (prop.factoryClass() != PropertyFactory.class) {
                if (prop.factoryClass() == PropertyFactory.Void.class) {
                    factoryClass = null;

                } else {
                    factoryClass = prop.factoryClass();
                }
            }
        }
    }

    public Object getPropertyObject() {
        return propertyObject;
    }

    public Object getPropertyData(String name, Property property) {
        return propertyObject == null ? null : propertyObject.getPropertyData(name, property);
    }

    public String getName() {
        return name;
    }

    public int getInclude() {
        return include;
    }

    public int getExclude() {
        return exclude;
    }

    public String getBeanName() {
        return beanName;
    }
}
