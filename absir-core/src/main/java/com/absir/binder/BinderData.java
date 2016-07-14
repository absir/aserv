/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-7 下午2:15:53
 */
package com.absir.binder;

import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.property.Property;
import com.absir.property.PropertyData;
import com.absir.property.PropertyError;
import com.absir.validator.Validator;
import com.absir.validator.ValidatorSupply;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
@Inject
public class BinderData extends DynaBinder {

    protected static BinderSupply binderSupply = BeanFactoryUtils.get(BinderSupply.class);

    protected static ValidatorSupply validatorSupply = BeanFactoryUtils.get(ValidatorSupply.class);

    private BinderResult binderResult = new BinderResult();

    @Override
    public List<BinderConvert> getConverts() {
        return (List<BinderConvert>) converts;
    }

    public BinderResult getBinderResult() {
        return binderResult;
    }

    public void setBinderResult(BinderResult binderResult) {
        this.binderResult = binderResult;
    }

    @Override
    protected Object bindTo(Object obj, String name, Type toType) {
        Object toObject = null;
        BreakException breakException = null;
        for (BinderConvert binderConvert : getConverts()) {
            try {
                toObject = binderConvert.to(obj, name, toType, breakException);
                if (toObject != null) {
                    return toObject;
                }

            } catch (BreakException e) {
                breakException = e;

            } catch (Exception e) {
                break;
            }
        }

        return toObject;
    }

    @Override
    protected <T> T newInstance(Class<T> toClass) {
        T toObj = super.newInstance(toClass);
        if (toObj == null) {
            addPropertyError("Fail to instance", toClass);
        }

        return toObj;
    }

    @Override
    protected <T> T nullTo(Class<T> toClass, Object obj) {
        addPropertyError("Fail to convert", obj);
        return KernelDyna.nullTo(toClass);
    }

    @Override
    protected <T> T bindTo(Object obj, String name, Class<T> toClass) {
        Object toObject = null;
        BreakException breakException = null;
        for (BinderConvert binderConvert : getConverts()) {
            try {
                toObject = binderConvert.to(toObject, name, toClass, breakException);
                if (toObject != null) {
                    return (T) toObject;
                }

            } catch (BreakException e) {
                breakException = e;

            } catch (Exception e) {
                break;
            }
        }

        return super.bindTo(obj, name, toClass);
    }

    @Override
    protected <T> T bindConvert(Object obj, String name, Class<T> toClass) {
        return bindConvert(obj, name, toClass, DynaBinder.INSTANCE.getConverts(), new BreakException[1]);
    }

    @Override
    protected <T> T mapBindConvert(Map<?, ?> map, String name, Class<T> toClass) {
        BreakException[] breakExceptions = new BreakException[1];
        T toObject = mapBindConvert(map, name, toClass, converts, breakExceptions);
        if (toObject == null) {
            toObject = mapBindConvert(map, name, toClass, DynaBinder.INSTANCE.getConverts(), breakExceptions);
        }

        return toObject;
    }

    @Override
    public void mapBind(Map<?, ?> map, Object toObject) {
        if (map == null) {
            return;
        }

        if (toObject != null) {
            Map<String, PropertyData> propertyDatas = binderSupply.getPropertyMap(toObject.getClass());
            if (binderResult.isValidation()) {
                validatorSupply.getPropertyMap(toObject.getClass());
            }

            if (propertyDatas != null && !propertyDatas.isEmpty()) {
                String propertyPath = binderResult.getPropertyPath();
                String propertyPrefix = "".equals(propertyPath) ? "" : (propertyPath + '.');
                for (Entry<String, PropertyData> entry : propertyDatas.entrySet()) {
                    PropertyData propertyData = entry.getValue();
                    Property property = propertyData.getProperty();
                    if (property.getAllow() <= 0 && property.allow(binderResult.getGroup())) {
                        String name = entry.getKey();
                        Object value = map.get(name);
                        if (value == null) {
                            if (!map.containsKey(name)) {
                                continue;
                            }
                        }

                        binderResult.setPropertyPath(propertyPrefix + name);
                        if (!binderResult.allowPropertyPath()) {
                            continue;
                        }

                        bindValue(value, propertyData, property, toObject);
                        if (toObject instanceof IBinder) {
                            value = map.get(name + ":");
                            if (value == null) {
                                ((IBinder) toObject).bind(name, value, propertyData, this);
                            }
                        }
                    }
                }

                binderResult.setPropertyPath(propertyPath);
            }
        }
    }

    public <T> T arrayBind(Object[] array, String name, Class<T> toClass) {
        T toObject = newInstance(toClass);
        arrayBind(array, toObject);
        return toObject;
    }

    public void arrayBind(Object[] array, Object toObject) {
        if (array == null) {
            return;
        }

        if (toObject != null) {
            Map<String, PropertyData> propertyDatas = binderSupply.getPropertyMap(toObject.getClass());
            if (binderResult.isValidation()) {
                validatorSupply.getPropertyMap(toObject.getClass());
            }

            int index = 0;
            int length = array.length;
            if (propertyDatas != null && !propertyDatas.isEmpty()) {
                String propertyPath = binderResult.getPropertyPath();
                String propertyPrefix = "".equals(propertyPath) ? "" : (propertyPath + '.');
                for (Entry<String, PropertyData> entry : propertyDatas.entrySet()) {
                    if (index >= length) {
                        return;
                    }

                    PropertyData propertyData = entry.getValue();
                    Property property = propertyData.getProperty();
                    if (property.getAllow() <= 0 && property.allow(binderResult.getGroup())) {
                        String name = entry.getKey();
                        Object value = array[index++];
                        binderResult.setPropertyPath(propertyPrefix + name);
                        if (!binderResult.allowPropertyPath()) {
                            continue;
                        }

                        bindValue(value, propertyData, property, toObject);
                    }
                }

                binderResult.setPropertyPath(propertyPath);
            }
        }
    }

    protected void bindValue(Object value, PropertyData propertyData, Property property, Object toObject) {
        value = binderSupply.bindValue(propertyData, value, null, this, property.getAccessor().get(toObject));
        try {
            property.getAccessor().set(toObject, value);

        } catch (Throwable e) {
            addPropertyError("Fail Bind Value", value);
            return;
        }

        if (binderResult.isValidation()) {
            List<Validator> validators = validatorSupply.getPropertyObject(propertyData);
            if (validators != null) {
                if (value == null) {
                    int last = binderResult.getPropertyErrors().size() - 1;
                    if (last >= 0 && binderResult.getPropertyErrors().get(last).getPropertyPath() == binderResult.getPropertyPath()) {
                        return;
                    }
                }

                for (Validator validator : validators) {
                    String errorMessage = validator.validate(value);
                    if (errorMessage != null) {
                        addPropertyError(errorMessage, value);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected <T> T bindArray(Object obj, String name, Class<T> toClass, Type toType) {
        String propertyPath = binderResult.getPropertyPath();
        T toObj = super.bindArray(obj, name, toClass, toType);
        binderResult.setPropertyPath(propertyPath);
        return toObj;
    }

    @Override
    protected <T extends Collection> T bindCollection(Object obj, String name, Class<T> toClass, Type toType, Collection toObject) {
        String propertyPath = binderResult.getPropertyPath();
        T toObj = super.bindCollection(obj, name, toClass, toType, toObject);
        binderResult.setPropertyPath(propertyPath);
        return toObj;
    }

    @Override
    protected <T extends Map> T bindMap(Map obj, String name, String keyName, Class<T> toClass, Type toType, Type keyType, Map toObject) {
        String propertyPath = binderResult.getPropertyPath();
        T toObj = super.bindMap(obj, name, keyName, toClass, toType, keyType, toObject);
        binderResult.setPropertyPath(propertyPath);
        return toObj;
    }

    @Override
    protected void bindArrayTo(int index) {
        binderResult.setPropertyPath(binderResult.getPropertyPath() + '[' + index + ']');
    }

    @Override
    protected void bindMapTo(Object key) {
        binderResult.setPropertyPath(binderResult.getPropertyPath() + "['" + key + "']");
    }

    protected void addPropertyError(String errorMessage, Object errorObject) {
        PropertyError propertyError = new PropertyError();
        propertyError.setPropertyPath(binderResult.getPropertyPath());
        propertyError.setErrorMessage(errorMessage);
        propertyError.setErrorObject(errorObject);
        binderResult.addPropertyError(propertyError);
    }
}
