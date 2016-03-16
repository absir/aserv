/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-7 上午9:46:37
 */
package com.absir.property;

import com.absir.core.kernel.KernelList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PropertyHolder {

    private int propertyDatas;

    private Map<String, PropertyData> nameMapPropertyData = new LinkedHashMap<String, PropertyData>();

    public int getPropertyDatas() {
        return propertyDatas;
    }

    public Map<String, PropertyData> getNameMapPropertyData() {
        return nameMapPropertyData;
    }

    protected boolean holded(int propertyIndex) {
        return (propertyDatas & (0x01 << propertyIndex)) != 0;
    }

    protected synchronized void doHolded(int propertyIndex, Class<?> beanClass, Map<String, Object> propertyMap, boolean propertyTree) {
        if (!holded(propertyIndex)) {
            propertyDatas |= 0x01 << propertyIndex;
            if (propertyTree) {
                List<PropertyContext> propertyContexts = new ArrayList(propertyMap.values());
                KernelList.sortOrderable(propertyContexts);
                for (PropertyContext propertyContext : propertyContexts) {
                    if (propertyContext.ignore) {
                        continue;
                    }

                    PropertyData propertyData = new PropertyData(beanClass, propertyContext.name, propertyContext.include, propertyContext.exclude, propertyContext.beanName,
                            propertyContext.factoryClass);
                    if (propertyData.getProperty().getAllow() > -2) {
                        nameMapPropertyData.put(propertyContext.name, propertyData);
                    }
                }
            }

            for (Entry<String, PropertyData> entry : nameMapPropertyData.entrySet()) {
                Object propertyData = propertyMap.get(entry.getKey());
                if (propertyData != null) {
                    Property property = entry.getValue().getProperty();
                    if (propertyTree) {
                        propertyData = ((PropertyContext) propertyData).getPropertyData(entry.getKey(), property);

                    } else {
                        propertyData = ((PropertyObject) propertyData).getPropertyData(entry.getKey(), property);
                    }
                }

                entry.getValue().getPropertyDatas()[propertyIndex] = propertyData;
            }
        }
    }
}
