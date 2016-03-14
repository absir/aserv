/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-9-22 下午1:38:58
 */
package com.absir.core.util;

import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author absir
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class UtilAccessor {

    /**
     * Accessor_Name_Map_Accessor
     */
    private static Map<String, Accessor> Accessor_Name_Map_Accessor = new HashMap<String, Accessor>();

    /**
     * @param obj
     * @param propertyPath
     * @return
     */
    public static Object get(Object obj, String propertyPath) {
        return getAccessorObj(obj, propertyPath).get(obj);
    }

    /**
     * @param obj
     * @param propertyPath
     * @param value
     */
    public static void set(Object obj, String propertyPath, Object value) {
        getAccessorObj(obj, propertyPath).set(obj, value);
    }

    /**
     * @param cls
     * @param property
     * @return
     */
    public static Accessor getAccessorProperty(Class<?> cls, String property) {
        return getAccessor(cls, property, KernelReflect.declaredField(cls, property));
    }

    /**
     * @param cls
     * @param field
     * @return
     */
    public static Accessor getAccessor(Class<?> cls, Field field) {
        return getAccessor(cls, field.getName(), field);
    }

    /**
     * @param cls
     * @param property
     * @param field
     * @return
     */
    public static Accessor getAccessor(Class<?> cls, String property, final Field field) {
        return getAccessor(cls, property, field, true);
    }

    /**
     * @param cls
     * @param property
     * @param field
     * @param cacheable
     * @return
     */
    public static Accessor getAccessor(Class<?> cls, String property, final Field field, boolean cacheable) {
        String accessName = getAccessorKey(cls.getName(), property);
        Accessor accessor = Accessor_Name_Map_Accessor.get(accessName);
        if (accessor == null) {
            if (cacheable) {
                synchronized (cls) {
                    accessor = Accessor_Name_Map_Accessor.get(accessName);
                    if (accessor == null) {
                        accessor = getAccessorField(cls, property, field);
                        Accessor_Name_Map_Accessor.put(accessName, accessor);
                    }
                }

            } else {
                accessor = getAccessorField(cls, property, field);
            }
        }

        return accessor;
    }

    /**
     * @param cls
     * @param property
     * @param field
     * @return
     */
    private static Accessor getAccessorField(Class<?> cls, String property, final Field field) {
        Class fieldType = field == null ? null : field.getType();
        final Method getter = KernelClass.declaredGetter(cls, property, fieldType, false);
        final Method setter = KernelClass.declaredSetter(cls, property, fieldType, true);
        if (field == null && getter == null && setter == null) {
            return null;
        }

        return new Accessor() {

            @Override
            public Object get(Object obj) {
                if (obj == null) {
                    return null;
                }

                return getter == null ? KernelReflect.get(obj, field) : KernelReflect.invoke(obj, getter);
            }

            @Override
            public boolean set(Object obj, Object value) {
                if (obj == null) {
                    return false;
                }

                return setter == null ? KernelReflect.set(obj, field, value) : KernelReflect.run(obj, setter, value);
            }

            @Override
            public Field getField() {
                return field;
            }

            @Override
            public Method getGetter() {
                return getter;
            }

            @Override
            public Method getSetter() {
                return setter;
            }

        };
    }

    /**
     * @param accessorName
     * @param propertyPath
     * @return
     */
    private static String getAccessorKey(String accessorName, String propertyPath) {
        return accessorName + ":" + propertyPath;
    }

    /**
     * @param obj
     * @param propertyPath
     * @return
     */
    public static Accessor getAccessorObj(Object obj, String propertyPath) {
        return getAccessor(obj, null, propertyPath.split("\\."), 0);
    }

    /**
     * @param obj
     * @param propertyPath
     * @param accessorName
     * @return
     */
    public static Accessor getAccessorObj(Object obj, String propertyPath, String accessorName) {
        return getAccessorObj(obj, propertyPath, accessorName, true);
    }

    /**
     * @param obj
     * @param propertyPath
     * @param accessorName
     * @param cacheable
     * @return
     */
    public static Accessor getAccessorObj(Object obj, String propertyPath, String accessorName, boolean cacheable) {
        if (accessorName == null || obj == null || !cacheable) {
            return getAccessorObj(obj, propertyPath);

        } else {
            accessorName = getAccessorKey(accessorName, propertyPath);
            Accessor accessor = Accessor_Name_Map_Accessor.get(accessorName);
            if (accessor == null) {
                synchronized (obj.getClass()) {
                    accessor = Accessor_Name_Map_Accessor.get(accessorName);
                    if (accessor == null) {
                        accessor = getAccessorObj(obj, propertyPath);
                        Accessor_Name_Map_Accessor.put(accessorName, accessor);
                    }
                }
            }

            return accessor;
        }
    }

    /**
     * @param propertyPath
     * @param accessorName
     */
    public static void clearAccessor(String propertyPath, String accessorName) {
        Accessor_Name_Map_Accessor.remove(getAccessorKey(accessorName, propertyPath));
    }

    /**
     *
     */
    public static void clearAll() {
        Accessor_Name_Map_Accessor.clear();
    }

    /**
     * @param obj
     * @param accessorWrapper
     * @param properties
     * @return
     */
    private static Accessor getAccessor(Object obj, AccessorWrapper accessorWrapper, final String[] properties, int i) {
        for (; i < properties.length; i++) {
            if (obj == null) {
                final int index = i;
                return new AccessorWrapper(accessorWrapper) {

                    /**
                     * evalAccessor
                     */
                    private Accessor evalAccessor;

                    /**
                     * @param obj
                     * @return
                     */
                    private Accessor getEvalAccessor(Object obj) {
                        if (evalAccessor == null) {
                            evalAccessor = getAccessor(obj, null, properties, index);
                        }

                        return evalAccessor;
                    }

                    @Override
                    public Object evalGet(Object obj) {
                        return getEvalAccessor(obj).get(obj);
                    }

                    @Override
                    public boolean evalSet(Object obj, Object value) {
                        return getEvalAccessor(obj).set(obj, value);
                    }

                    @Override
                    public Field getField() {
                        return evalAccessor == null ? null : evalAccessor.getField();
                    }

                    @Override
                    public Method getGetter() {
                        return evalAccessor == null ? null : evalAccessor.getGetter();
                    }

                    @Override
                    public Method getSetter() {
                        return evalAccessor == null ? null : evalAccessor.getSetter();
                    }
                };

            } else {
                final String property = properties[i];
                if (obj instanceof Map) {
                    accessorWrapper = new AccessorWrapper(accessorWrapper) {

                        @Override
                        public Object evalGet(Object obj) {
                            return ((Map) obj).get(property);
                        }

                        @Override
                        public boolean evalSet(Object obj, Object value) {
                            ((Map) obj).put(property, value);
                            return true;
                        }

                        @Override
                        public Field getField() {
                            return null;
                        }

                        @Override
                        public Method getGetter() {
                            return null;
                        }

                        @Override
                        public Method getSetter() {
                            return null;
                        }

                    };

                } else {
                    Field field = KernelReflect.declaredField(obj.getClass(), property);
                    final Accessor evalAccessor = getAccessor(obj.getClass(), property, field);
                    accessorWrapper = new AccessorWrapper(accessorWrapper) {

                        @Override
                        public Object evalGet(Object obj) {
                            return evalAccessor.get(obj);
                        }

                        @Override
                        public boolean evalSet(Object obj, Object value) {
                            return evalAccessor.set(obj, value);
                        }

                        @Override
                        public Field getField() {
                            return evalAccessor.getField();
                        }

                        @Override
                        public Method getGetter() {
                            return evalAccessor.getGetter();
                        }

                        @Override
                        public Method getSetter() {
                            return evalAccessor.getSetter();
                        }
                    };
                }

                obj = accessorWrapper.evalGet(obj);
            }
        }

        return accessorWrapper;
    }

    /**
     * @author absir
     */
    public static abstract class Accessor {

        /**
         * @param obj
         * @return
         */
        public abstract Object get(Object obj);

        /**
         * @param obj
         * @param value
         * @return
         */
        public abstract boolean set(Object obj, Object value);

        /**
         * @return
         */
        public abstract Field getField();

        /**
         * @return
         */
        public abstract Method getGetter();

        /**
         * @return
         */
        public abstract Method getSetter();

        /**
         * @return
         */
        public Class<?> getDeclaringClass() {
            return getField() == null ? getGetter() == null ? getSetter().getDeclaringClass() : getGetter().getDeclaringClass() : getField().getDeclaringClass();
        }

        /**
         * @param annotationClass
         * @param getter
         * @return
         */
        public <T extends Annotation> T getAnnotation(Class<T> annotationClass, boolean getter) {
            Method method = getter ? getGetter() : getSetter();
            T annotation = method == null ? null : method.getAnnotation(annotationClass);
            if (annotation == null) {
                Field field = getField();
                annotation = field == null ? null : field.getAnnotation(annotationClass);
                if (annotation == null) {
                    method = getter ? getSetter() : getGetter();
                    annotation = method == null ? null : method.getAnnotation(annotationClass);
                    if (annotation == null && !getter) {
                        method = getSetter();
                        annotation = method == null ? null : method.getAnnotation(annotationClass);
                    }
                }
            }

            return annotation;
        }
    }

    /**
     * @author absir
     */
    public static abstract class AccessorWrapper extends Accessor {

        /**
         * accessor
         */
        private Accessor accessor;

        /**
         * @param accessor
         */
        public AccessorWrapper(Accessor accessor) {
            this.accessor = accessor;
        }

        /**
         * @param obj
         * @return
         */
        private Object eval(Object obj) {
            return accessor == null ? obj : accessor.get(obj);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.absir.core.util.UtilAccessor.Accessor#get(java.lang.Object)
         */
        @Override
        public Object get(Object obj) {
            obj = eval(obj);
            if (obj == null) {
                return null;
            }

            return evalGet(obj);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.absir.core.util.UtilAccessor.Accessor#set(java.lang.Object,
         * java.lang.Object)
         */
        @Override
        public boolean set(Object obj, Object value) {
            obj = eval(obj);
            if (obj == null) {
                return false;
            }

            return evalSet(obj, value);
        }

        /**
         * @param obj
         * @return
         */
        public abstract Object evalGet(Object obj);

        /**
         * @param obj
         * @param value
         * @return
         */
        public abstract boolean evalSet(Object obj, Object value);
    }
}
