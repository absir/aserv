/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-10 上午11:06:13
 */
package com.absir.aserv.dyna;

import com.absir.aserv.configure.xls.XlsBase;
import com.absir.aserv.configure.xls.XlsUtils;
import com.absir.aserv.system.helper.HelperString;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.bean.basis.Configure;
import com.absir.bean.inject.value.Started;
import com.absir.binder.BinderSupply;
import com.absir.binder.BinderUtils;
import com.absir.client.helper.HelperJson;
import com.absir.core.base.IBase;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.dyna.DynaConvert;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAbsir;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.property.PropertyData;
import com.absir.property.PropertyHolder;
import com.absir.property.PropertyUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
@Configure
public class DynaBinderUtils extends DynaBinder {

    public static <T> T getParamId(Object id, Class<T> paramType) {
        if (id.getClass() == String.class) {
            return getParamValue((String) id, paramType);
        }

        return to(id, paramType);
    }

    public static <T> T getParamValue(String param, Class<T> paramType) {
        if (param != null && KernelClass.isCustomClass(paramType)) {
            T paramValue = KernelClass.newInstance(paramType);
            if (paramValue != null) {
                String[] params = HelperString.split(param, '`');
                BinderSupply binderSupply = BinderUtils.getBinderSupply();
                PropertyHolder propertyHolder = PropertyUtils.getPropertyMap(paramType, binderSupply);
                int index = 0;
                for (Entry<String, PropertyData> entry : propertyHolder.getNameMapPropertyData().entrySet()) {
                    PropertyData propertyData = entry.getValue();
                    if (propertyData.getProperty().getAllow() == 0) {
                        propertyData.getProperty().getAccessor().set(paramValue,
                                binderSupply.bindValue(propertyData, params[index++], null, DynaBinder.INSTANCE, null));
                    }
                }

                return paramValue;
            }
        }

        return DynaBinder.to(param, paramType);
    }

    public static <T> T getParamObject(Object param, Class<T> paramType) {
        if (param != null && param.getClass() == String.class) {
            return getParamValue((String) param, paramType);
        }

        return to(param, paramType);
    }

    public static String getParamFromValue(Object paramValue) {
        if (paramValue != null && KernelClass.isCustomClass(paramValue.getClass())) {
            BinderSupply binderSupply = BinderUtils.getBinderSupply();
            PropertyHolder propertyHolder = PropertyUtils.getPropertyMap(paramValue.getClass(), binderSupply);
            List<Object> params = new ArrayList<Object>();
            for (Entry<String, PropertyData> entry : propertyHolder.getNameMapPropertyData().entrySet()) {
                PropertyData propertyData = entry.getValue();
                if (propertyData.getProperty().getAllow() == 0) {
                    Object param = binderSupply.bindValue(propertyData,
                            propertyData.getProperty().getAccessor().get(paramValue), String.class, null, null);
                    params.add(param == null ? "" : param);
                }
            }

            return KernelString.implode(params, '`');
        }

        return DynaBinder.to(paramValue, String.class);
    }

    @Started
    private static void started() {
        DynaBinder.INSTANCE.addConvert(new DynaConvert() {

            @Override
            public Object to(Object obj, String name, Class<?> toClass, BreakException breakException)
                    throws Exception {
                if (breakException == null) {
                    if (XlsBase.class.isAssignableFrom(toClass)) {
                        Object xlsBase = XlsUtils.findXlsBean((Class<? extends XlsBase>) toClass, obj);
                        if (xlsBase == null) {
                            throw new BreakException();
                        }

                        return xlsBase;
                    }
                }

                return null;
            }

            @Override
            public Object mapTo(Map<?, ?> map, String name, Class<?> toClass, BreakException breakException)
                    throws Exception {
                return null;
            }
        });

        DynaBinder.INSTANCE.addConvert(new DynaConvert() {

            @Override
            public Object to(Object obj, String name, Class<?> toClass, BreakException breakException)
                    throws Exception {
                if (breakException == null) {
                    if (name == null) {
                        name = SessionFactoryUtils.getEntityNameNull(toClass);
                    }

                    if (name != null) {
                        Object entityObject = CrudServiceUtils.find(name, obj, null);
                        if (entityObject == null) {
                            throw new BreakException();
                        }

                        return entityObject;
                    }
                }

                return null;
            }

            @Override
            public Object mapTo(Map<?, ?> map, String name, Class<?> toClass, BreakException breakException)
                    throws Exception {
                return null;
            }
        });
    }

    public static boolean is(Class cls) {
        return KernelClass.isBasicClass(cls) || Date.class.isAssignableFrom(cls) || Enum.class.isAssignableFrom(cls);
    }

    public static <T> T to(Object obj, Class<T> toClass) {
        return toIBase(obj, toClass, false);
    }

    public static <T> T toIBase(Object obj, Class<T> toClass, boolean iBase) {
        if (obj == null) {
            return KernelDyna.nullTo(toClass);
        }

        if (toClass.isAssignableFrom(obj.getClass())) {
            return (T) obj;
        }

        if (toClass.isAssignableFrom(String.class)) {
            if (iBase && obj instanceof IBase) {
                obj = ((IBase) obj).getId();
            }

            if (is(obj.getClass())) {
                if (obj.getClass() == Boolean.class) {
                    obj = (Boolean) obj ? "1" : "0";

                } else if (obj.getClass() == Float.class) {
                    obj = UtilAbsir.floatIntValue((Float) obj).toString();

                } else if (obj.getClass() == Double.class) {
                    obj = UtilAbsir.doubleIntValue((Double) obj).toString();
                }

            } else {
                return (T) HelperJson.encodeNull(obj);
            }

        } else {
            if (iBase) {
                if (IBase.class.isAssignableFrom(toClass)) {
                    T toObject = DynaBinder.to(obj, toClass);
                    if (toObject != null) {
                        return toObject;
                    }

                } else {
                    iBase = false;
                }
            }

            if (obj instanceof String && !is(toClass)) {
                return HelperJson.decodeNull((String) obj, toClass);
            }

            if (iBase) return null;
        }

        return DynaBinder.to(obj, toClass);
    }

    public static Object toType(Object obj, Type toType) {
        return toTypeIBase(obj, toType, false);
    }

    public static Object toTypeIBase(Object obj, Type toType, boolean iBase) {
        if (toType instanceof Class) {
            return toIBase(obj, (Class) toType, iBase);
        }

        if (obj instanceof String) {
            return HelperJson.decodeNull((String) obj, toType);
        }

        return DynaBinder.INSTANCE.bind(obj, null, toType);
    }

    public static <T> T getMapValue(Map map, Object name, Class<T> toClass) {
        Object obj = map.get(name);
        if (obj != null) {
            T toObject = to(obj, toClass);
            if (toObject != obj) {
                map.put(name, toObject);
            }

            return toObject;
        }

        return null;
    }

    public static Object getMapValue(Map map, Object name, Type toType) {
        Object obj = map.get(name);
        if (obj != null) {
            Object toObject = toType(obj, toType);
            if (toObject != obj) {
                map.put(name, toObject);
            }

            return toObject;
        }

        return null;
    }
}
