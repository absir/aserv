/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-8 下午2:17:26
 */
package com.absir.binder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.absir.core.kernel.KernelDyna;
import com.absir.property.PropertyData;
import com.absir.validator.ValidatorSupply;

/**
 * @author absir
 * 
 */
@SuppressWarnings("unchecked")
public class BinderUtils {

	/**
	 * @return
	 */
	public static BinderSupply getBinderSupply() {
		return BinderData.binderSupply;
	}

	/**
	 * @return
	 */
	public static ValidatorSupply getValidatorSupply() {
		return BinderData.validatorSupply;
	}

	/**
	 * @param entity
	 * @return
	 */
	public static Map<String, Object> getEntityMap(Object entity) {
		if (entity == null) {
			return null;
		}

		Map<String, Object> entityMap = new HashMap<String, Object>();
		for (Entry<String, PropertyData> entry : BinderData.binderSupply.getPropertyMap(entity.getClass()).entrySet()) {
			PropertyData propertyData = entry.getValue();
			if (propertyData.getProperty().getAllow() >= 0) {
				entityMap.put(entry.getKey(), propertyData.getProperty().getAccessor().get(entity));
			}
		}

		return entityMap;
	}

	/**
	 * @param propertyMap
	 * @return
	 */
	public static Map<String, Object> getDataMap(Map<String, Object> propertyMap) {
		return (Map<String, Object>) getDataObject(propertyMap, null);
	}

	/**
	 * @param propertyMap
	 * @param from
	 * @return
	 */
	public static Object getDataObject(Map<String, Object> propertyMap, String from) {
		int length = from == null ? 0 : from.length();
		Object dataObject = null;
		Object dataValue = null;
		for (Entry<String, Object> entry : propertyMap.entrySet()) {
			String propertyPath = entry.getKey();
			if (length != 0) {
				if (length >= propertyPath.length()) {
					continue;

				} else {
					propertyPath = propertyPath.substring(length);
				}
			}

			String[] propertyPaths = propertyPath.replace("[", ".[").split("\\.");
			dataValue = addDataObject(propertyPaths, 0, propertyPaths.length, entry.getValue(), dataObject);
			if (dataObject == null) {
				dataObject = dataValue;
			}
		}

		if (dataObject == null && length == 0) {
			dataObject = propertyMap;
		}

		return dataObject;
	}

	/**
	 * @param propertyPaths
	 * @param value
	 * @param objectTemplate
	 */
	private static Object addDataObject(String[] propertyPaths, int beganIndex, int endIndex, Object value, Object dataObject) {
		if (beganIndex >= endIndex) {
			return value;
		}

		String propertyPath = propertyPaths[beganIndex++];
		int length = propertyPath.length();
		if (length > 1 && propertyPath.charAt(0) == '[') {
			if (length > 4 && propertyPath.charAt(1) == '\'') {
				// 字典键值
				propertyPath = propertyPath.substring(2, length - 2);

			} else {
				if (length > 2) {
					Integer index = KernelDyna.toInteger(propertyPath.substring(1, length - 1));
					if (index != null) {
						// 数组下标
						if (dataObject == null) {
							dataObject = new ArrayList<Object>();

						} else if (!(dataObject instanceof List)) {
							return dataObject;
						}

						List<Object> dataList = (List<Object>) dataObject;
						for (int i = dataList.size() - index; i <= 0; i++) {
							dataList.add(null);
						}

						dataObject = dataList.get(index);
						value = addDataObject(propertyPaths, beganIndex, endIndex, value, dataObject);
						if (dataObject == null) {
							dataList.set(index, value);
						}

						return dataList;
					}

				} else {
					// 连续数组
					if (dataObject == null) {
						dataObject = new ArrayList<Object>();

					} else if (!(dataObject instanceof List)) {
						return dataObject;
					}

					List<Object> dataList = (List<Object>) dataObject;
					dataList.add(addDataObject(propertyPaths, beganIndex, endIndex, value, null));
					return dataList;
				}

				return dataObject;
			}
		}

		// 添加到字典
		if (dataObject == null) {
			dataObject = new HashMap<String, Object>();

		} else if (!(dataObject instanceof Map)) {
			return dataObject;
		}

		Map<String, Object> dataMap = (Map<String, Object>) dataObject;
		dataObject = dataMap.get(propertyPath);
		value = addDataObject(propertyPaths, beganIndex, endIndex, value, dataObject);
		if (dataObject == null) {
			dataMap.put(propertyPath, value);
		}

		return dataMap;
	}
}
