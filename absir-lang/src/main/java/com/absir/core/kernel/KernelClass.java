/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-15 下午5:41:36
 */
package com.absir.core.kernel;

import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class KernelClass {

    public static final Class<?>[] NUMBER_CLASSES = new Class<?>[]{int.class, long.class, float.class, double.class};

    public static final TypeVariable CollectionTypeVariable = Collection.class.getTypeParameters()[0];

    public static final TypeVariable[] MapTypeVariable = Map.class.getTypeParameters();

    protected static final Map<Class, String> classMapSimpleName = new HashMap<Class, String>();

    static final Map<Class, Object> Class_Map_Instance = new HashMap<Class, Object>();

    public static boolean isBasicClass(Class cls) {
        return cls.getPackage() == Character.class.getPackage() || Date.class.isAssignableFrom(cls)
                || Enum.class.isAssignableFrom(cls) || !Object.class.isAssignableFrom(cls);
    }

    public static boolean isCustomClass(Class cls) {
        return cls.getPackage() != Map.class.getPackage() && !isBasicClass(cls);
    }

    public static String parentName(Class cls) {
        return KernelString.rightSubString(cls.getName(), cls.getSimpleName().length() + 1);
    }

    public static boolean isAssignableFrom(Class cls, Class[] types) {
        for (Class type : types) {
            if (cls.isAssignableFrom(type)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAssignableFrom(Class[] classes, Class type) {
        for (Class cls : classes) {
            if (cls.isAssignableFrom(type)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAssignableFrom(Class[] classes, Class[] types) {
        int length = classes.length;
        if (length == types.length) {
            for (int i = 0; i < length; i++) {
                if (!classes[i].isAssignableFrom(types[i])) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public static <T> Class<? extends T> classAssignable(Class cls, Class T) {
        return classAssignable(cls, null, T);
    }

    public static <T> Class<? extends T> classAssignable(Class cls, Class<? extends T> defaultValue, Class T) {
        if (cls == null || !T.isAssignableFrom(cls)) {
            return defaultValue;
        }

        return cls;
    }

    public static boolean isMatchableFrom(Class cls, Class type) {
        if (type == null || type == Object.class || cls.isAssignableFrom(type)) {
            return true;
        }

        if (cls == byte.class) {
            return type == Byte.class;

        } else if (cls == Byte.class) {
            return type == byte.class;

        } else if (cls == short.class) {
            return type == Short.class;

        } else if (cls == Short.class) {
            return type == short.class;

        } else if (cls == int.class) {
            return type == Integer.class;

        } else if (cls == Integer.class) {
            return type == int.class;

        } else if (cls == float.class) {
            return type == Float.class;

        } else if (cls == Float.class) {
            return type == float.class;

        } else if (cls == double.class) {
            return type == Double.class;

        } else if (cls == Double.class) {
            return type == double.class;

        } else if (cls == boolean.class) {
            return type == Boolean.class;

        } else if (cls == Boolean.class) {
            return type == boolean.class;

        } else if (cls == char.class) {
            return type == Character.class;

        } else if (cls == Character.class) {
            return type == char.class;

        } else if (cls == long.class) {
            return type == Long.class;

        } else if (cls == Long.class) {
            return type == long.class;

        } else if (cls.isArray() && type.isArray()) {
            cls.getComponentType().isAssignableFrom(type.getComponentType());
        }

        return false;
    }

    public static Class getMatchableFrom(Class cls, Class[] types) {
        for (Class type : types) {
            if (isMatchableFrom(cls, type)) {
                return type;
            }
        }

        return null;
    }

    public static boolean isMatchableFrom(Class[] classes, Class[] types) {
        int length = classes.length;
        if (length == types.length) {
            for (int i = 0; i < length; i++) {
                if (!isMatchableFrom(classes[i], types[i])) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public static Class<?> getMatchNumberClass(Class cls) {
        return getMatchableFrom(cls, NUMBER_CLASSES);
    }

    public static Class[] parameterTypes(Object... args) {
        int length = args.length;
        if (length == 0) {
            return KernelLang.NULL_CLASSES;
        }

        Class[] parameterTypes = new Class[length];
        Object arg;
        for (int i = 0; i < length; i++) {
            arg = args[i];
            parameterTypes[i] = arg == null ? null : args[i].getClass();
        }

        return parameterTypes;
    }

    public static Class rawClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;

        } else if (type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) type).getRawType();

        } else if (type instanceof GenericArrayType) {
            try {
                return Array.newInstance(rawClass(((GenericArrayType) type).getGenericComponentType()), 0).getClass();

            } catch (Exception e) {
            }
        }

        return Object.class;
    }

    public static Type[] typeArguments(Type type) {
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments();
        }

        return null;
    }

    public static Class[] rawClasses(Type[] types) {
        int length = types.length;
        Class[] classes = new Class[length];
        for (int i = 0; i < length; i++) {
            classes[i] = rawClass(types[i]);
        }

        return classes;
    }

    public static Class componentClass(Class<?> cls) {
        if (cls.isArray()) {
            return cls.getComponentType();
        }

        Class superClass = componentClass(cls.getGenericSuperclass());
        return superClass == cls.getSuperclass() ? cls : superClass;
    }

    public static Class[] componentClasses(Class<?> cls) {
        if (cls.isArray()) {
            return new Class[]{cls.getComponentType()};
        }

        Class[] superClasses = componentClasses(cls.getGenericSuperclass());
        if (superClasses.length == 1 && superClasses[0] == cls.getSuperclass()) {
            superClasses[0] = cls;
        }

        return superClasses;
    }

    public static Class componentClass(Type type) {
        Class<?> cls = rawClass(type);
        if (cls.isArray()) {
            return cls.getComponentType();
        }

        if (Collection.class.isAssignableFrom(cls)) {
            return rawClass(type(type, CollectionTypeVariable));

        } else if (Map.class.isAssignableFrom(cls)) {
            return rawClass(type(type, MapTypeVariable[0]));
        }

        return cls;
    }

    public static Class[] componentClasses(Type type) {
        Class<?> cls = rawClass(type);
        if (cls.isArray()) {
            return new Class[]{cls.getComponentType()};
        }

        if (Collection.class.isAssignableFrom(cls)) {
            return new Class[]{rawClass(type(type, CollectionTypeVariable))};

        } else if (Map.class.isAssignableFrom(cls)) {
            return new Class[]{rawClass(type(type, MapTypeVariable[0])), rawClass(type(type, MapTypeVariable[1]))};
        }

        return new Class[]{cls};
    }

    public static Class argumentClass(Class<?> cls) {
        if (cls.isArray()) {
            return cls.getComponentType();
        }

        Class superClass = argumentClass(cls, true);
        return superClass == cls.getSuperclass() ? cls : superClass;
    }

    public static Class[] argumentClasses(Class<?> cls) {
        if (cls.isArray()) {
            return new Class[]{cls.getComponentType()};
        }

        Class[] superClasses = argumentClasses(cls, true);
        if (superClasses.length == 1 && superClasses[0] == cls.getSuperclass()) {
            superClasses[0] = cls;
        }

        return superClasses;
    }

    public static boolean isArgumentClass(Class cls) {
        return cls != null
                && (cls.isArray() || Collection.class.isAssignableFrom(cls) || Map.class.isAssignableFrom(cls));
    }

    public static Class argumentClass(Type type, boolean force) {
        Type componentType = type;
        while (type != null) {
            Type[] types = typeArguments(type);
            if (types == null || types.length <= 0) {
                Class cls = rawClass(type);
                for (Type interfaceType : cls.getGenericInterfaces()) {
                    types = typeArguments(interfaceType);
                    if (types != null && types.length > 0) {
                        break;
                    }
                }

                if (types == null || types.length <= 0) {
                    if (!(force || isArgumentClass(cls))) {
                        break;
                    }

                    if (cls.isArray()) {
                        return cls.getComponentType();

                    } else {
                        type = cls.getGenericSuperclass();
                        continue;
                    }
                }
            }

            return rawClass(types[0]);
        }

        return rawClass(componentType);
    }

    public static Class[] argumentClasses(Type type, boolean force) {
        Type componentType = type;
        while (type != null) {
            Type[] types = typeArguments(type);
            if (types == null || types.length <= 0) {
                Class cls = rawClass(type);
                for (Type interfaceType : cls.getGenericInterfaces()) {
                    types = typeArguments(interfaceType);
                    if (types != null && types.length > 0) {
                        break;
                    }
                }

                if (types == null || types.length <= 0) {
                    if (!(force || isArgumentClass(cls))) {
                        break;
                    }

                    if (cls.isArray()) {
                        return new Class[]{cls.getComponentType()};

                    } else {
                        type = cls.getGenericSuperclass();
                        continue;
                    }
                }
            }

            return rawClasses(types);
        }

        return new Class[]{rawClass(componentType)};
    }

    public static Type[] argumentTypes(Type type, boolean force) {
        while (type != null) {
            Type[] types = typeArguments(type);
            if (types == null || types.length <= 0) {
                Class cls = rawClass(type);
                for (Type interfaceType : cls.getGenericInterfaces()) {
                    types = typeArguments(interfaceType);
                    if (types != null && types.length > 0) {
                        break;
                    }
                }

                if (types == null || types.length <= 0) {
                    if (!(force || isArgumentClass(cls))) {
                        break;
                    }

                    if (cls.isArray()) {
                        return new Class[]{cls.getComponentType()};

                    } else {
                        type = cls.getGenericSuperclass();
                        continue;
                    }
                }
            }

            return types;
        }

        return null;
    }

    public static Type type(Type type, TypeVariable typeVariable) {
        Type root = type;
        GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
        boolean impl = genericDeclaration instanceof Class && !((Class) genericDeclaration).isInterface();
        Class cls = rawClass(type);
        Class superCls;
        while (true) {
            if (type != null) {
                if (type instanceof ParameterizedType) {
                    Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
                    int i = 0;
                    int len = typeArguments.length;
                    String name = typeVariable.getName();
                    type = null;
                    for (TypeVariable var : genericDeclaration.getTypeParameters()) {
                        if (name.equals(var.getName())) {
                            type = typeArguments[i];
                            if (type instanceof TypeVariable) {
                                if (type == typeVariable) {
                                    return root;
                                }

                                return type(root, (TypeVariable) type);
                            }
                        }

                        if (i++ >= len) {
                            break;
                        }
                    }
                }
            }

            if (cls == null) {
                break;
            }

            superCls = cls.getSuperclass();
            if (impl) {
                if (superCls == genericDeclaration) {
                    type = cls.getGenericSuperclass();
                }

            } else {
                int i = 0;
                for (Class iCls : cls.getInterfaces()) {
                    if (iCls == genericDeclaration) {
                        type = cls.getGenericInterfaces()[i];
                    }

                    i++;
                }
            }

            cls = superCls;
        }

        return type;
    }

    public static Type typeType(Class cls, TypeVariable typeVariable) {
        Type type;
        while (true) {
            type = type(cls, typeVariable);
            if (type == null || !(type instanceof TypeVariable)) {
                break;

            } else {
                typeVariable = (TypeVariable) type;
            }
        }

        return type;
    }

    public static Class typeClass(Class cls, TypeVariable typeVariable) {
        Type type = typeType(cls, typeVariable);
        return type == null ? null : KernelClass.rawClass(type);
    }

    public static int similar(Class cls, Class type) {
        if (cls == null) {
            return 0;
        }

        if (cls == type) {
            return 1;
        }

        int similar = -1;
        while (cls != null && cls != Object.class) {
            if (cls == type) {
                return similar;
            }

            for (Class iCls : cls.getInterfaces()) {
                if (iCls == type) {
                    return similar;
                }
            }

            similar--;
            cls = cls.getSuperclass();
        }

        return type == null ? -similar : similar;
    }

    public static int similar(Class[] classes, Class[] types) {
        int similar = -1;
        int length = classes.length;
        if (length == types.length) {
            for (int i = 0; i < length; i++) {
                similar += similar(classes[i], types[i]);
            }
        }

        return similar;
    }

    public static Class forName(String className) {
        return forName(className, null);
    }

    public static Class forName(String className, Class defaultValue) {
        try {
            return Class.forName(className);

        } catch (ClassNotFoundException e) {
        }

        return defaultValue;
    }

    public static <T extends Annotation> T fetchAnnotation(Class<?> cls, Class<T> annotationType) {
        while (cls != null) {
            T annotation = cls.getAnnotation(annotationType);
            if (annotation != null) {
                return annotation;
            }

            for (Class c : cls.getInterfaces()) {
                annotation = (T) fetchAnnotation(c, annotationType);
                if (annotation != null) {
                    return annotation;
                }
            }

            cls = cls.getSuperclass();
        }

        return null;
    }

    public static Annotation fetchAnnotations(Class<?> cls, Class<? extends Annotation>... annotationTypes) {
        while (cls != null) {
            for (Class<? extends Annotation> annotationType : annotationTypes) {
                Annotation annotation = cls.getAnnotation(annotationType);
                if (annotation != null) {
                    return annotation;
                }
            }

            for (Class c : cls.getInterfaces()) {
                for (Class<? extends Annotation> annotationType : annotationTypes) {
                    Annotation annotation = fetchAnnotation(c, annotationType);
                    if (annotation != null) {
                        return annotation;
                    }
                }
            }

            cls = cls.getSuperclass();
        }

        return null;
    }

    public static <T extends Annotation> T getAnnotation(AnnotatedElement cls, Class<T> annotationType) {
        T annotation = cls.getAnnotation(annotationType);
        if (annotation == null && (!(cls instanceof Class) || !Annotation.class.isAssignableFrom((Class) cls))) {
            Annotation[] annotations = cls.getAnnotations();
            if (annotations != null) {
                for (Annotation obj : annotations) {
                    annotation = getAnnotation(obj.annotationType(), annotationType);
                    if (annotation != null) {
                        break;
                    }
                }
            }
        }

        return annotation;
    }

    public static <T> T newInstance(Class<T> cls) {
        return newInstance(null, cls);
    }

    public static <T> T newInstance(T defaultValue, Class<T> cls) {
        try {
            return cls.newInstance();

        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }

        return defaultValue;
    }

    public static <T> T newInstance(Class<T> cls, Object... initargs) {
        return newInstance(cls, null, initargs);
    }

    public static <T> T newInstance(Class<T> cls, T defaultValue, Object... initargs) {
        if (initargs.length == 0) {
            return newInstance(defaultValue, cls);
        }

        return newInstance(cls, defaultValue, KernelClass.parameterTypes(initargs), true, initargs);
    }

    public static <T> T newInstance(Class<T> cls, T defaultValue, Class[] parameterTypes, Object... initargs) {
        return newInstance(cls, defaultValue, parameterTypes, false, initargs);
    }

    public static <T> T newInstance(Class<T> cls, T defaultValue, Class[] parameterTypes, boolean assignable,
                                    Object... initargs) {
        return declaredNew(cls, defaultValue, false, assignable, parameterTypes, initargs);
    }

    public static <T> T declaredNew(Class<T> cls, Object... initargs) {
        return declaredNew(cls, null, KernelClass.parameterTypes(initargs), true, initargs);
    }

    public static <T> T declaredNew(Class<T> cls, T defaultValue, Object... initargs) {
        return declaredNew(cls, defaultValue, KernelClass.parameterTypes(initargs), true, initargs);
    }

    public static <T> T declaredNew(Class<T> cls, Class[] parameterTypes, Object... initargs) {
        return declaredNew(cls, null, parameterTypes, initargs);
    }

    public static <T> T declaredNew(Class<T> cls, T defaultValue, Class[] parameterTypes, Object... initargs) {
        return declaredNew(cls, defaultValue, parameterTypes, false, initargs);
    }

    public static <T> T declaredNew(Class<T> cls, T defaultValue, Class[] parameterTypes, boolean assignable,
                                    Object... initargs) {
        return declaredNew(cls, defaultValue, true, assignable, parameterTypes, initargs);
    }

    public static <T> T declaredNew(Class<T> cls, T defaultValue, boolean declared, boolean assignable,
                                    Class[] parameterTypes, Object... initargs) {
        Constructor<T> constructor = KernelReflect.assignableConstructor(cls, declared, assignable, parameterTypes);
        return KernelReflect.newInstance(constructor, initargs);
    }

    public static <T> T getInstance(Class<T> cls) {
        T instance = (T) Class_Map_Instance.get(cls);
        if (instance == null) {
            synchronized (cls) {
                instance = (T) Class_Map_Instance.get(cls);
                if (instance == null) {
                    instance = newInstance(cls);
                    Class_Map_Instance.put(cls, instance);
                }
            }
        }

        return instance;
    }

    public static <T> T getInstance(Class<T> cls, Object... initargs) {
        T instance = (T) Class_Map_Instance.get(cls);
        if (instance == null) {
            synchronized (cls) {
                instance = (T) Class_Map_Instance.get(cls);
                if (instance == null) {
                    instance = newInstance(cls, initargs);
                    Class_Map_Instance.put(cls, instance);
                }
            }
        }

        return instance;
    }

    public static Object declaredGet(Class cls, String name) {
        Field field = KernelReflect.declaredField(cls.getClass(), name);
        return KernelReflect.get(cls, field);
    }

    public static boolean declaredSet(Class cls, String name, Object value) {
        Field field = KernelReflect.declaredField(cls.getClass(), name);
        return KernelReflect.set(cls, field, value);
    }

    public static Object declaredSend(Class cls, String name, Object... args) {
        return declaredSendArray(cls, name, args);
    }

    public static Object declaredSendArray(Class cls, String name, Object[] args) {
        Method method = KernelReflect.assignableMethod(cls, name, 0, true, parameterTypes(args));
        if (method != null) {
            return KernelReflect.invoke(cls, method, args);
        }

        return null;
    }

    public static Method setter(Field field) {
        return setter(field.getDeclaringClass(), field);
    }

    public static Method setter(Class cls, Field field) {
        return setter(cls, field.getName(), field.getType());
    }

    public static Method setter(Class cls, String field, Class fieldType) {
        return declaredSetter(cls, field, fieldType, false);
    }

    public static Method declaredSetter(Class cls, Field field) {
        return declaredSetter(cls, field.getName(), field.getType(), true);
    }

    public static Method declaredSetter(Class cls, String field, Class fieldType, boolean declared) {
        String name = "set" + KernelString.capitalize(field);
        return KernelReflect.assignableMethod(cls, name, 0, declared, fieldType == null ? true : false, true,
                fieldType);
    }

    public static Method getter(Field field) {
        return getter(field.getDeclaringClass(), field);
    }

    public static Method getter(Class cls, Field field) {
        return getter(cls, field.getName(), field.getType());
    }

    public static Method getter(Class cls, String field, Class fieldType) {
        return declaredGetter(cls, field, fieldType, false);
    }

    public static Method getter(Class cls, String field) {
        return declaredGetter(cls, field, null, false);
    }

    public static Method declaredGetter(Class cls, Field field) {
        return declaredGetter(cls, field.getName(), field.getType(), true);
    }

    public static Method declaredGetter(Class cls, String field, Class fieldType, boolean declared) {
        field = KernelString.capitalize(field);
        boolean is = fieldType != null && (fieldType == boolean.class || fieldType == Boolean.class);
        Method method = KernelReflect.assignableMethod(cls, fieldType, (is ? "is" : "get") + field, 0, declared, true,
                true);
        if (method == null || (fieldType != null && !fieldType.isAssignableFrom(method.getReturnType()))) {
            if (fieldType == null) {
                method = KernelReflect.assignableMethod(cls, fieldType, "is" + field, 0, declared, true, true);

            } else {
                if (is) {
                    method = KernelReflect.assignableMethod(cls, fieldType, "get" + field, 0, declared, true, true);
                }
            }

            if (method != null && fieldType != null && !fieldType.isAssignableFrom(method.getReturnType())) {
                method = null;
            }
        }

        return method;
    }

    public static <T> T instanceOf(Class<T> cls, Object obj) {
        return instanceOf(cls, null, obj);
    }

    public static <T> T instanceOf(Class<T> cls, T defaultValue, Object obj) {
        T value = newInstance(cls, obj);
        if (value == null) {
            value = valueOf(cls, obj);
        }

        if (value == null) {
            return defaultValue;

        } else {
            return value;
        }
    }

    public static <T> T valueOf(Class<T> cls, Object obj) {
        return valueOf(cls, null, obj);
    }

    public static <T> T valueOf(Class<T> cls, T defaultValue, Object obj) {
        Method method = KernelReflect.assignableMethod(cls, "valueOf", obj.getClass());
        if (method != null && cls.isAssignableFrom(method.getReturnType())) {
            return (T) KernelReflect.invoke(cls, method, obj);
        }

        return defaultValue;
    }

    public static <T> Class<? extends T> cast(Class cls, Class<T> toClass) {
        return cast(cls, null, toClass);
    }

    public static <T> Class<? extends T> cast(Class cls, Class<? extends T> defaultValue, Class<T> toClass) {
        if (cls != null && toClass.isAssignableFrom(cls)) {
            return cls;
        }

        return defaultValue;
    }

    public static void doWithSuperClass(Class cls, CallbackBreak<Class<?>> callback) {
        try {
            while (!(cls == null || cls == Object.class)) {
                callback.doWith(cls);
                cls = cls.getSuperclass();
            }

        } catch (BreakException e) {
        }
    }

    public static void doWithAncestClass(Class cls, CallbackBreak<Class<?>> callback) {
        try {
            while (!(cls == null || cls == Object.class)) {
                callback.doWith(cls);
                for (Class c : cls.getInterfaces()) {
                    callback.doWith(c);
                }

                cls = cls.getSuperclass();
            }

        } catch (BreakException e) {
        }
    }

    public static boolean doWithAncestRevertClass(Class cls, CallbackBreak<Class<?>> callback, boolean withInterface) {
        if (cls != null && cls != Object.class) {
            if (doWithAncestRevertClass(cls.getSuperclass(), callback, withInterface)) {
                return true;
            }

            try {
                if (withInterface) {
                    for (Class<?> iCls : cls.getInterfaces()) {
                        callback.doWith(iCls);
                    }
                }

                callback.doWith(cls);

            } catch (BreakException e) {
                return true;
            }
        }

        return false;
    }

    public static Object invokeCommandString(String commandString) {
        if (!KernelString.isEmpty(commandString)) {
            String[] commands = commandString.split("\\:", 3);
            if (commands.length >= 2) {
                Class<?> targetClass = forName(commands[0]);
                if (targetClass != null) {
                    if (commands.length == 2) {
                        return declaredSend(targetClass, commands[1]);

                    } else {
                        return declaredSendArray(targetClass, commands[1], commands[2].split(","));
                    }
                }
            }
        }

        return null;
    }

    public static String getClassSharedSimpleName(Class<?> cls) {
        String simpleName = classMapSimpleName.get(cls);
        if (simpleName == null) {
            simpleName = cls.getSimpleName();
            classMapSimpleName.put(cls, simpleName);
        }

        return simpleName;
    }

    public static URL getCodeSource(Class cls, String path) throws MalformedURLException {
        URL url = cls.getProtectionDomain().getCodeSource().getLocation();
        String spec = url.toString();
        if (spec.endsWith(".jar")) {
            return new URL("jar:" + spec + '!' + path);

        } else {
            return cls.getResource(path);
        }
    }
}
