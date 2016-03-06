/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-14 下午1:52:40
 */
package com.absir.context.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.context.core.compare.CompareAbstract;
import com.absir.property.Property;
import com.absir.property.PropertyData;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Inject
public class ContextMap {

	/** CONTEXT_MAP_SUPPLY */
	public static final ContextMapSupply CONTEXT_MAP_SUPPLY = BeanFactoryUtils.get(ContextMapSupply.class);

	/** obj */
	private transient Object obj;

	/** group */
	private transient int group;

	/** dataMap */
	private Map<String, Object> dataMap;

	/** propertyDatas */
	private transient Map<String, PropertyData> propertyDatas;

	/** propertyCompares */
	private transient List<Object> propertyCompares = new ArrayList<Object>();

	/**
	 * @param obj
	 */
	public ContextMap(Object obj) {
		this(obj, 0);
	}

	/**
	 * @param obj
	 */
	public ContextMap(Object obj, int group) {
		this.obj = obj;
		this.group = group;
		propertyDatas = CONTEXT_MAP_SUPPLY.getPropertyMap(obj.getClass());
		for (Entry<String, PropertyData> entry : propertyDatas.entrySet()) {
			Property property = entry.getValue().getProperty();
			if (property.getAllow() >= 0 && property.allow(group)) {
				CompareAbstract compareAbstract = CONTEXT_MAP_SUPPLY.getPropertyObject(entry.getValue());
				if (compareAbstract != null) {
					propertyCompares.add(compareAbstract.getCompare(property.getAccessor().get(obj)));
				}
			}
		}
	}

	/**
	 * @param name
	 * @param data
	 */
	public void put(String name, Object data) {
		if (dataMap == null) {
			dataMap = new HashMap<String, Object>();
		}

		dataMap.put(name, data);
	}

	/**
	 * @return
	 */
	public Map<String, Object> comparedMap() {
		Map<String, Object> comparedMap = new HashMap<String, Object>();
		int index = 0;
		for (Entry<String, PropertyData> entry : propertyDatas.entrySet()) {
			Property property = entry.getValue().getProperty();
			if (property.getAllow() >= 0 && property.allow(group)) {
				CompareAbstract compareAbstract = CONTEXT_MAP_SUPPLY.getPropertyObject(entry.getValue());
				if (compareAbstract != null) {
					Object compare = propertyCompares.get(index);
					Object value = property.getAccessor().get(obj);
					if (!compareAbstract.compareTo(compare, value)) {
						comparedMap.put(entry.getKey(), value);
						propertyCompares.set(index, compareAbstract.getCompare(value));
					}

					++index;
				}
			}
		}

		if (dataMap != null) {
			comparedMap.putAll(dataMap);
			dataMap = null;
		}

		return comparedMap;
	}
}
