/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-25 下午3:40:40
 */
package com.absir.servlet;

import com.absir.binder.BinderData;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelArray.ArrayAccessor;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelDyna;
import com.absir.property.PropertyError;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class BinderRequest extends BinderData {

    private String parameterPath;

    @Override
    protected <T> T bindTo(Object obj, String name, Class<T> toClass) {
        if (obj == null) {
            return null;
        }

        if (obj.getClass() == String[].class) {
            String[] params = (String[]) obj;
            if (params.length > 0) {
                if (!(toClass.isArray() || Collection.class.isAssignableFrom(toClass))) {
                    obj = params[0];
                    if (String.class.isAssignableFrom(toClass)) {
                        return (T) obj;
                    }
                }
            }
        }

        return super.bindTo(obj, name, toClass);
    }

    @Override
    public <T> T bind(Object obj, String name, Type toType, T toObject) {
        if (toType == null) {
            return (T) obj;
        }

        if (obj == null) {
            return null;
        }

        if (obj.getClass() == String[].class) {
            String[] params = (String[]) obj;
            if (params.length > 0) {
                if (toType instanceof Class) {
                    Class<?> toClass = (Class<?>) toType;
                    if (!(toClass.isArray() || Collection.class.isAssignableFrom(toClass))) {
                        obj = params[0];
                    }

                } else if (!(toType instanceof ParameterizedType)) {
                    obj = params[0];
                }
            }
        }

        return super.bind(obj, name, toType, toObject);
    }

    @Override
    protected <T> T bindArray(Object obj, String name, Class<T> toClass, Type toType) {
        if (obj.getClass() == String[].class) {
            String[] params = (String[]) obj;
            if (params.length == 1) {
                Class<?> valueClass = null;
                if (toType instanceof Class) {
                    valueClass = (Class<?>) toType;
                    if (KernelClass.isBasicClass(valueClass)) {
                        Object value = KernelDyna.stringNull(params[0], toClass);
                        if (value != null) {
                            ArrayAccessor accessor = KernelArray.forComponentType(valueClass);
                            Object toObject = accessor.newInstance(0);
                            accessor.set(toObject, 0, value);
                            return (T) toObject;
                        }

                    } else {
                        valueClass = null;
                    }
                }

                if (valueClass == null) {
                    T toObject = super.bindArray(obj, name, toClass, toType);
                    List<PropertyError> propertyErrors = getBinderResult().getPropertyErrors();
                    int size = propertyErrors.size();
                    if (size == 0 || !propertyErrors.get(size - 1).getPropertyPath().equals(getBinderResult().getPropertyPath() + "[0]")) {
                        return toObject;
                    }

                    propertyErrors.remove(size - 1);
                }

                parameterPath = getBinderResult().getPropertyPath();
                T returnValue = super.bindArray(StringUtils.split(params[0], ','), name, toClass, toType);
                parameterPath = null;
                return returnValue;
            }
        }

        return super.bindArray(obj, name, toClass, toType);
    }

    @Override
    protected <T extends Collection> T bindCollection(Object obj, String name, Class<T> toClass, Type toType, Collection toObject) {
        if (obj.getClass() == String[].class) {
            String[] params = (String[]) obj;
            if (params.length == 1) {
                Class<?> valueClass = null;
                if (toType instanceof Class) {
                    valueClass = (Class<?>) toType;
                    if (KernelClass.isBasicClass(valueClass)) {
                        Object value = KernelDyna.stringNull(params[0], toClass);
                        if (value != null) {
                            toObject = toCollection(toClass, toObject);
                            if (toObject != null) {
                                toObject.add(value);
                            }

                            return (T) toObject;
                        }

                    } else {
                        valueClass = null;
                    }
                }

                if (valueClass == null) {
                    toObject = super.bindCollection(obj, name, toClass, toType, toObject);
                    List<PropertyError> propertyErrors = getBinderResult().getPropertyErrors();
                    int size = propertyErrors.size();
                    if (size == 0 || !propertyErrors.get(size - 1).getPropertyPath().equals(getBinderResult().getPropertyPath() + "[0]")) {
                        return (T) toObject;
                    }

                    propertyErrors.remove(size - 1);
                }

                parameterPath = getBinderResult().getPropertyPath();
                T returnValue = super.bindCollection(StringUtils.split(params[0], ','), name, toClass, toType, toObject);
                parameterPath = null;
                return returnValue;
            }
        }

        return super.bindCollection(obj, name, toClass, toType, toObject);
    }

    @Override
    protected void addPropertyError(String errorMessage, Object errorObject) {
        if (parameterPath == null) {
            super.addPropertyError(errorMessage, errorObject);

        } else {
            getBinderResult().rejectValue(parameterPath, errorMessage, errorObject);
        }
    }
}
