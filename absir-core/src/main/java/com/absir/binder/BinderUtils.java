/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-8 下午2:17:26
 */
package com.absir.binder;

import com.absir.bean.lang.ILangMessage;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelString;
import com.absir.property.PropertyData;
import com.absir.property.PropertyError;
import com.absir.property.PropertyHolder;
import com.absir.property.PropertyUtils;
import com.absir.validator.Validator;
import com.absir.validator.ValidatorSupply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("unchecked")
public class BinderUtils {

    public static BinderSupply getBinderSupply() {
        return BinderData.binderSupply;
    }

    public static ValidatorSupply getValidatorSupply() {
        return BinderData.validatorSupply;
    }

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

    public static Map<String, Object> getDataMap(Map<String, Object> propertyMap) {
        return (Map<String, Object>) getDataObject(propertyMap, null);
    }

    public static Object getDataObject(Map<String, Object> propertyMap, String from) {
        int length = from == null ? 0 : (from.length() + 1);
        Object dataObject = null;
        Object dataValue = null;
        DataObjectHandler handler = new DataObjectHandler();
        if (length != 0) {
            from += '.';
        }

        for (Entry<String, Object> entry : propertyMap.entrySet()) {
            String propertyPath = entry.getKey();
            if (length != 0) {
                if (length >= propertyPath.length() || !propertyPath.startsWith(from)) {
                    continue;

                } else {
                    propertyPath = propertyPath.substring(length);
                }
            }

            String[] propertyPaths = propertyPath.replace("[", ".[").split("\\.");
            dataValue = addDataObject(propertyPaths, 0, propertyPaths.length, entry.getValue(), dataObject, handler);
            if (dataObject == null) {
                dataObject = dataValue;
            }
        }

        if (dataObject == null && length == 0) {
            dataObject = propertyMap;
        }

        return dataObject;
    }

    private static Object addDataObject(String[] propertyPaths, int beganIndex, int endIndex, Object value, Object dataObject, DataObjectHandler handler) {
        if (beganIndex >= endIndex) {
            return value;
        }

        String propertyPath = propertyPaths[beganIndex++];
        int length = propertyPath.length();
        if (length > 1 && propertyPath.charAt(0) == '[') {
            if (length > 4 && propertyPath.charAt(1) == '@') {
                // 字典键值
                propertyPath = propertyPath.substring(2, length - 2);
                if (value != null && propertyPath.equals("!for_key") && value instanceof Object[]) {
                    Map<String, List<String>> pathMapKeys = handler.pathMapKeys;
                    if (pathMapKeys == null) {
                        pathMapKeys = new HashMap<String, List<String>>();
                        handler.pathMapKeys = pathMapKeys;
                    }

                    String path = KernelString.implodeOffset(propertyPaths, 0, beganIndex - 1, null, null, '.');
                    Object[] values = (Object[]) value;
                    List<String> keys = pathMapKeys.get(path);
                    if (keys == null) {
                        keys = new ArrayList<String>();
                        pathMapKeys.put(path, keys);
                    }

                    for (Object val : values) {
                        keys.add(DynaBinder.to(val, String.class));
                    }

                    return null;
                }

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
                        value = addDataObject(propertyPaths, beganIndex, endIndex, value, dataObject, handler);
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
                    if (value != null && value instanceof Object[]) {
                        int i = 0;
                        for (Object val : (Object[]) value) {
                            if (i < dataList.size()) {
                                addDataObject(propertyPaths, beganIndex, endIndex, val, dataList.get(i), handler);

                            } else {
                                dataList.add(addDataObject(propertyPaths, beganIndex, endIndex, val, null, handler));
                            }

                            i++;
                        }

                    } else {
                        dataList.add(addDataObject(propertyPaths, beganIndex, endIndex, value, null, handler));
                    }

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
        Map<String, List<String>> pathMapKeys = handler.pathMapKeys;
        if (pathMapKeys != null && value != null && propertyPath.equals("!!for_key") && value instanceof Object[]) {
            String path = KernelString.implodeOffset(propertyPaths, 0, beganIndex - 1, null, null, '.');
            List<String> keys = pathMapKeys.get(path);
            if (keys != null) {
                dataObject = dataMap.get(propertyPath);
                int i = 0;
                for (Object val : (Object[]) value) {
                    if (i >= keys.size()) {
                        break;
                    }

                    propertyPath = keys.get(i);
                    dataObject = dataMap.get(propertyPath);
                    value = addDataObject(propertyPaths, beganIndex, endIndex, val, dataObject, handler);
                    if (dataObject == null) {
                        dataMap.put(propertyPath, value);
                    }

                    i++;
                }

                return dataMap;
            }
        }

        dataObject = dataMap.get(propertyPath);
        value = addDataObject(propertyPaths, beganIndex, endIndex, value, dataObject, handler);
        if (dataObject == null) {
            dataMap.put(propertyPath, value);
        }

        return dataMap;
    }

    public static void validate(BinderResult binderResult, Object bean, ILangMessage langMessage, String... names) {
        ValidatorSupply validatorSupply = getValidatorSupply();
        PropertyHolder propertyHolder = PropertyUtils.getPropertyMap(bean.getClass(), validatorSupply);
        for (Entry<String, PropertyData> entry : propertyHolder.getNameMapPropertyData().entrySet()) {
            String name = entry.getKey();
            if (KernelArray.contain(names, name)) {
                Object value = entry.getValue().getProperty().getAccessor().get(bean);
                List<Validator> validators = validatorSupply.getPropertyObject(entry.getValue());
                if (validators != null) {
                    if (value == null) {
                        int last = binderResult.getPropertyErrors().size() - 1;
                        if (last >= 0 && binderResult.getPropertyErrors().get(last).getPropertyPath() == binderResult.getPropertyPath()) {
                            return;
                        }
                    }

                    for (Validator validator : validators) {
                        String errorMessage = validator.validate(value, langMessage);
                        if (errorMessage != null) {
                            PropertyError propertyError = new PropertyError();
                            propertyError.setPropertyPath(binderResult.getPropertyPath());
                            propertyError.setErrorMessage(errorMessage);
                            propertyError.setErrorObject(value);
                            binderResult.addPropertyError(propertyError);
                            break;
                        }
                    }
                }
            }
        }
    }

    protected static class DataObjectHandler {

        public Map<String, List<String>> pathMapKeys;
    }
}
