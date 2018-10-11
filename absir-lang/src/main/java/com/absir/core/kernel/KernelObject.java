/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-5 下午2:31:23
 */
package com.absir.core.kernel;

import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class KernelObject {

    public static <T> T getValue(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static boolean set(Object obj, String name, Object value) {
        return declaredSet(obj, name, 0, false, value);
    }

    public static boolean declaredSet(Object obj, String name, Object value) {
        return declaredSet(obj, name, 0, true, value);
    }

    public static boolean declaredSet(Object obj, String name, int ancest, boolean declared, Object value) {
        Field field;
        if (obj instanceof Class) {
            field = KernelReflect.declaredField((Class) obj, name, ancest, declared);
            field = KernelReflect.memberStatic(field);

        } else {
            field = KernelReflect.declaredField(obj.getClass(), name, ancest, declared);
        }

        return KernelReflect.set(obj, field, value);
    }

    public static Object get(Object obj, String name) {
        return declaredGet(obj, name, 0, false);
    }

    public static Object declaredGet(Object obj, String name) {
        return declaredGet(obj, name, 0, true);
    }

    public static Object declaredGet(Object obj, String name, int ancest, boolean declared) {
        Field field;
        if (obj instanceof Class) {
            field = KernelReflect.declaredField((Class) obj, name, ancest, declared);
            field = KernelReflect.memberStatic(field);

        } else {
            field = KernelReflect.declaredField(obj.getClass(), name, ancest, declared);
        }

        return KernelReflect.get(obj, field);
    }

    public static Object send(Object obj, String name, Object... args) {
        return send(obj, name, 0, true, true, KernelClass.parameterTypes(args), args);
    }

    public static Object send(Object obj, String name, Class[] parameterTypes, Object... args) {
        return send(obj, name, 0, false, false, parameterTypes, args);
    }

    public static Object send(Object obj, String name, int ancest, boolean assignable, boolean cacheable, Class[] parameterTypes,
                              Object... args) {
        return declaredSend(obj, name, ancest, false, assignable, cacheable, parameterTypes, args);
    }

    public static Object declaredSend(Object obj, String name, Object... args) {
        return declaredSendArray(obj, name, args);
    }

    public static Object declaredSendArray(Object obj, String name, Object[] args) {
        return declaredSend(obj, name, 0, true, true, true, KernelClass.parameterTypes(args), args);
    }

    public static Object declaredSend(Object obj, String name, Class[] parameterTypes, Object... args) {
        return declaredSend(obj, name, 0, true, false, false, parameterTypes, args);
    }

    public static Object declaredSend(Object obj, String name, int ancest, boolean declared, boolean assignable, boolean cacheable,
                                      Class[] parameterTypes, Object... args) {
        Method method;
        if (obj instanceof Class) {
            method = KernelReflect.assignableMethod((Class) obj, name, ancest, declared, assignable, cacheable, parameterTypes);
            method = KernelReflect.memberStatic(method);

        } else {
            method = KernelReflect.assignableMethod(obj.getClass(), name, ancest, declared, assignable, cacheable, parameterTypes);
        }

        return KernelReflect.invoke(obj, method, args);
    }

    public static boolean setter(Object obj, Field field, Object value) {
        return setter(obj, field.getName(), field.getType(), value);
    }

    public static boolean setter(Object obj, String field, Object value) {
        return setter(obj, field, value.getClass(), value);
    }

    public static boolean setter(Object obj, String field, Class fieldType, Object value) {
        Method method = KernelClass.setter(obj.getClass(), field, fieldType);
        if (method != null) {
            if (KernelReflect.invoke(obj, false, method, value) == null) {
                return true;
            }
        }

        return false;
    }

    public static boolean publicSetter(Object obj, Field field, Object value) {
        if (setter(obj, field, value)) {
            return true;
        }

        if (Modifier.isPublic(field.getModifiers())) {
            if (KernelReflect.set(obj, field, value)) {
                return true;
            }
        }

        return false;
    }

    public static boolean publicSetter(Object obj, String field, Object value) {
        Method method = KernelClass.setter(obj.getClass(), field, value.getClass());
        if (method == null) {
            return set(obj, field, value);

        } else {
            return KernelReflect.run(obj, method, value);
        }
    }

    public static boolean declaredSetter(Object obj, Field field, Object value) {
        Method method = KernelClass.declaredSetter(obj.getClass(), field);
        if (method == null) {
            if (KernelReflect.set(obj, field, value)) {
                return true;
            }

        } else {
            return KernelReflect.run(obj, method, value);
        }

        return false;
    }

    public static Object getter(Object obj, Field field) {
        return getter(obj, field.getName(), field.getType());
    }

    public static Object getter(Object obj, String field) {
        return getter(obj, field, Object.class);
    }

    public static Object getter(Object obj, String field, Class fieldType) {
        Method method = KernelClass.getter(obj.getClass(), field, fieldType);
        if (method != null) {
            return KernelReflect.invoke(obj, method);
        }

        return null;
    }

    public static Object publicGetter(Object obj, Field field) {
        Method method = KernelClass.getter(obj.getClass(), field);
        if (method == null) {
            if (Modifier.isPublic(field.getModifiers())) {
                return KernelReflect.get(obj, field);
            }

        } else {
            return KernelReflect.invoke(obj, method);
        }

        return null;
    }

    public static Object publicGetter(Object obj, String field) {
        Method method = KernelClass.getter(obj.getClass(), field);
        if (method == null) {
            return get(obj, field);

        } else {
            return KernelReflect.invoke(obj, method);
        }
    }

    public static Object declaredGetter(Object obj, Field field) {
        Method method = KernelClass.getter(obj.getClass(), field);
        if (method == null) {
            return KernelReflect.get(obj, field);

        } else {
            return KernelReflect.invoke(obj, method);
        }
    }

    public static Object declaredGetter(Object obj, String field) {
        Method method = KernelClass.getter(obj.getClass(), field);
        if (method == null) {
            return declaredGet(obj, field);

        } else {
            return KernelReflect.invoke(obj, method);
        }
    }

    public static Object expressGetter(Object obj, String target) {
        if (KernelString.isEmpty(target)) {
            return obj;
        }

        String[] fields = target.split("\\.");
        for (String field : fields) {
            if (obj == null) {
                return null;
            }

            if (field.startsWith(":")) {
                Method method = KernelReflect.method(obj.getClass(), field.substring(1));
                if (method != null) {
                    obj = KernelReflect.invoke(obj, method);
                }

            } else {
                obj = declaredGetter(obj, field);
            }
        }

        return obj;
    }

    public static <T> T cast(Object obj, Class<T> toClass) {
        if (obj != null && toClass.isAssignableFrom(obj.getClass())) {
            return (T) obj;
        }

        return null;
    }

    public static <T> T clone(T from) {
        if (from == null) {
            return null;
        }

        try {
            if (from.getClass().isArray()) {
                return KernelArray.clone(from);

            } else {
                T clone = (T) from.getClass().newInstance();
                clone(from, clone);
                return clone;
            }

        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }

        return null;
    }

    public static <T> void clone(final T from, final T to) {
        if (from.getClass().isArray()) {
            KernelArray.copy(from, to);

        } else if (from instanceof Collection) {
            KernelCollection.copy((Collection) from, (Collection) to);

        } else if (from instanceof Map) {
            KernelMap.copy((Map<Object, Object>) from, (Map<Object, Object>) to);

        } else {
            KernelReflect.doWithDeclaredFields(from.getClass(), new CallbackBreak<Field>() {

                @Override
                public void doWith(Field template) throws BreakException {
                    template.setAccessible(true);
                    try {
                        template.set(to, template.get(from));

                    } catch (IllegalArgumentException e) {
                    } catch (IllegalAccessException e) {
                    }
                }
            });
        }
    }

    public static void copy(final Object from, final Object to) {
        final Class cls = to.getClass();
        if (from.getClass().isArray()) {
            KernelArray.copy(from, to);

        } else if (from instanceof Collection) {
            if (to instanceof Collection) {
                KernelCollection.copy((Collection) from, (Collection) to);
            }

        } else if (from instanceof Map) {
            if (to instanceof Map) {
                KernelMap.copy((Map<Object, Object>) from, (Map<Object, Object>) to);
            }

        } else {
            KernelReflect.doWithDeclaredFields(from.getClass(), new CallbackBreak<Field>() {

                @Override
                public void doWith(Field template) throws BreakException {
                    Field field = KernelReflect.declaredField(cls, template.getName());
                    if (field != null && field.getType().isAssignableFrom(template.getType())) {
                        template.setAccessible(true);
                        try {
                            field.set(to, template.get(from));

                        } catch (IllegalArgumentException e) {
                        } catch (IllegalAccessException e) {
                        }
                    }
                }
            });
        }
    }

    public static byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
            objectOut.writeObject(obj);
            return byteOut.toByteArray();

        } catch (IOException e) {
        }

        return null;
    }

    public static Object unserialize(byte[] buf) {
        try {
            ByteArrayInputStream byteInput = new ByteArrayInputStream(buf);
            ObjectInputStream objectInput = new ObjectInputStream(byteInput);
            Object obj = objectInput.readObject();
            return obj;

        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        }

        return null;
    }

    public static <T> T serializeClone(T obj) {
        byte[] buf = serialize(obj);
        if (buf == null) {
            return null;
        }

        return (T) unserialize(buf);
    }

    public static Map<String, Object> getMap(final Object obj) {
        final Map<String, Object> map = new HashMap<String, Object>();
        KernelReflect.doWithDeclaredFields(obj.getClass(), new CallbackBreak<Field>() {

            @Override
            public void doWith(Field template) throws BreakException {
                Object value = publicGetter(obj, template);
                if (value != null) {
                    map.put(template.getName(), value);
                }
            }
        });

        return map;
    }

    public static void setMap(Object obj, Map<String, Object> map) {
        for (Entry<String, Object> entry : map.entrySet()) {
            publicSetter(obj, entry.getKey(), entry.getValue());
        }
    }

    public static int hashCode(Object obj) {
        return obj == null ? 1 : obj.hashCode();
    }

    public static boolean equals(Object obj, Object equal) {
        return obj == equal || (obj != null && obj.equals(equal));
    }

    public static int compare(Object obj, Object compare) {
        if (obj == null) {
            return compare == null ? 0 : -1;
        }

        if (compare == null) {
            return 1;
        }

        return 0;
    }

}
