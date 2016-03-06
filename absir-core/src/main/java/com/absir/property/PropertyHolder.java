/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-7 上午9:46:37
 */
package com.absir.property;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.absir.core.kernel.KernelList;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PropertyHolder {

	/** propertyDatas */
	private int propertyDatas;

	/** nameMapPropertyData */
	private Map<String, PropertyData> nameMapPropertyData = new LinkedHashMap<String, PropertyData>();

	/**
	 * @return the propertyDatas
	 */
	public int getPropertyDatas() {
		return propertyDatas;
	}

	/**
	 * @return the nameMapPropertyData
	 */
	public Map<String, PropertyData> getNameMapPropertyData() {
		return nameMapPropertyData;
	}

	/**
	 * @param propertyIndex
	 * @return
	 */
	protected boolean holded(int propertyIndex) {
		return (propertyDatas & (0x01 << propertyIndex)) != 0;
	}

	/**
	 * @param propertyIndex
	 * @param propertyMap
	 * @param propertyTree
	 */
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
