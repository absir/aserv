/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-2-12 下午4:08:01
 */
package com.absir.property;

import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelString;

public class PropertyParams implements PropertyFactory {

    private static final PropertyConvert PARAMS_CONVERT = new PropertyConvert() {

        @Override
        public Object getValue(Object propertyValue) {
            return propertyValue = KernelString.implode(DynaBinder.to(propertyValue, Object[].class), ',');
        }

        @Override
        public Object getPropertyValue(Object value, String beanName) {
            if (value instanceof String) {
                value = ((String) value).split(",");
            }

            return value;
        }
    };

    @Override
    public PropertyConvert getPropertyConvert(Property property) {
        return PARAMS_CONVERT;
    }
}
