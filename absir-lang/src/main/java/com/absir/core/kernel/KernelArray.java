/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-5 下午2:31:23
 */
package com.absir.core.kernel;

import java.lang.reflect.Array;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class KernelArray {

    public static <T> T get(T[] array, int index) {
        if (array != null && index >= 0 && index < array.length) {
            return array[index];
        }

        return null;
    }

    public static <T> void set(T[] array, T element) {
        set(array, element, array.length);
    }

    public static <T> void set(T[] array, T element, int length) {
        set(array, element, 0, length);
    }

    public static <T> void set(T[] array, T element, int beginIndex, int endIndex) {
        for (int i = beginIndex; i < endIndex; i++) {
            array[i] = element;
        }
    }

    public static <T> T[] repeat(T element, int length) {
        T[] array = (T[]) forComponentType(element.getClass()).newInstance(length);
        set(array, element, length);
        return array;
    }

    public static <T> T[] repeat(T element, int length, Class<T> componentType) {
        T[] array = (T[]) Array.newInstance(componentType, length);
        set(array, element, length);
        return array;
    }

    public static <T> int index(T[] array, T element) {
        int length = array.length;
        for (int i = 0; i < length; i++) {
            if (KernelObject.equals(array[i], element)) {
                return i;
            }
        }

        return -1;
    }

    public static <T> boolean contain(T[] array, T element) {
        return index(array, element) != -1;
    }

    public static <T> boolean contains(T[] array, T... elements) {
        for (T element : elements) {
            if (!contain(array, element)) {
                return false;
            }
        }

        return true;
    }

    public static boolean equal(Object[] array, Object[] other) {
        int length = array.length;
        if (length != other.length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (array[i] != other[i]) {
                return false;
            }
        }

        return true;
    }

    public static boolean equals(Object[] array, Object[] other) {
        int length = array.length;
        if (length != other.length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (!KernelObject.equals(array[i], other[i])) {
                return false;
            }
        }

        return true;
    }

    public static <T> T[] concat(T[] array, T[] other) {
        T[] concatArray = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length + other.length);
        System.arraycopy(array, 0, concatArray, 0, array.length);
        try {
            System.arraycopy(other, 0, concatArray, array.length, other.length);

        } catch (ArrayStoreException ase) {
            return array;
        }

        return concatArray;
    }

    public static <T> void copy(T[] array, T[] to) {
        int length = array.length;
        for (int i = 0; i < length; i++) {
            to[i] = array[i];
        }
    }

    public static <T> void copy(T[] array, Collection<T> collection) {
        for (T value : array) {
            collection.add(value);
        }
    }

    public static Object[] toArray(Object array) {
        int length = Array.getLength(array);
        Object[] objects = new Object[length];
        if (length > 0) {
            ArrayAccessor accessor = forClass(array.getClass());
            for (int i = 0; i < length; i++) {
                objects[i] = accessor.get(array, i);
            }
        }

        return objects;
    }

    public static <T> List<T> toList(T[] array) {
        List<T> list = new ArrayList<T>(array.length);
        copy(array, list);
        return list;
    }

    public static <T> Set<T> toSet(T[] array) {
        Set<T> set = new HashSet<T>(array.length);
        copy(array, set);
        return set;
    }

    public static <T> T getAssignable(Object[] array, Class<T> cls) {
        for (Object value : array) {
            if (value != null && cls.isAssignableFrom(value.getClass())) {
                return (T) value;
            }
        }

        return null;
    }

    public static ArrayAccessor forClass(Class cls) {
        if (cls.isArray()) {
            return forComponentType(cls.getComponentType());
        }

        return null;
    }

    public static ArrayAccessor forComponentType(Class componentType) {
        if (componentType == byte.class) {
            return EnumArrayAccessor.Byte;

        } else if (componentType == short.class) {
            return EnumArrayAccessor.Short;

        } else if (componentType == int.class) {
            return EnumArrayAccessor.Integer;

        } else if (componentType == long.class) {
            return EnumArrayAccessor.Long;

        } else if (componentType == float.class) {
            return EnumArrayAccessor.Float;

        } else if (componentType == double.class) {
            return EnumArrayAccessor.Double;

        } else if (componentType == boolean.class) {
            return EnumArrayAccessor.Boolean;

        } else if (componentType == char.class) {
            return EnumArrayAccessor.Character;

        } else if (componentType == Object.class) {
            return EnumArrayAccessor.Object;
        }

        return new ComponentArrayAsscessor(componentType);
    }

    public static <T> void copy(Object array, Object to) {
        if (array.getClass().isArray() && to.getClass().isArray() && array.getClass().getComponentType().isAssignableFrom(to.getClass().getComponentType())) {
            ArrayAccessor interfaceArray = forClass(array.getClass());
            int length = Array.getLength(array);
            for (int i = 0; i < length; i++) {
                interfaceArray.set(to, i, interfaceArray.get(array, i));
            }
        }
    }

    public static <T> T clone(T array) {
        ArrayAccessor interfaceArray = forClass(array.getClass());
        if (interfaceArray == null) {
            return null;
        }

        int length = Array.getLength(array);
        T clone = (T) interfaceArray.newInstance(length);
        for (int i = 0; i < length; i++) {
            interfaceArray.set(clone, i, interfaceArray.get(array, i));
        }

        return clone;
    }

    public static enum EnumArrayAccessor implements ArrayAccessor {

        Byte {
            @Override
            public Object newInstance(int length) {
                return new byte[length];
            }

            @Override
            public Object get(Object array, int index) {
                return Array.getByte(array, index);
            }

            @Override
            public void set(Object array, int index, Object value) {
                Array.setByte(array, index, (Byte) value);
            }
        },

        Short {
            @Override
            public Object newInstance(int length) {
                return new short[length];
            }

            @Override
            public Object get(Object array, int index) {
                return Array.getShort(array, index);
            }

            @Override
            public void set(Object array, int index, Object value) {
                Array.setShort(array, index, (Short) value);
            }
        },

        Integer {
            @Override
            public Object newInstance(int length) {
                return new int[length];
            }

            @Override
            public Object get(Object array, int index) {
                return Array.getInt(array, index);
            }

            @Override
            public void set(Object array, int index, Object value) {
                Array.setInt(array, index, (Integer) value);
            }
        },

        Long {
            @Override
            public Object newInstance(int length) {
                return new long[length];
            }

            @Override
            public Object get(Object array, int index) {
                return Array.getLong(array, index);
            }

            @Override
            public void set(Object array, int index, Object value) {
                Array.setLong(array, index, (Long) value);
            }
        },

        Float {
            @Override
            public Object newInstance(int length) {
                return new float[length];
            }

            @Override
            public Object get(Object array, int index) {
                return Array.getFloat(array, index);
            }

            @Override
            public void set(Object array, int index, Object object) {
                Array.setFloat(array, index, (Float) object);
            }
        },

        Double {
            @Override
            public Object newInstance(int length) {
                return new double[length];
            }

            @Override
            public Object get(Object array, int index) {
                return Array.getDouble(array, index);
            }

            @Override
            public void set(Object array, int index, Object value) {
                Array.setDouble(array, index, (Double) value);
            }
        },

        Boolean {
            @Override
            public Object newInstance(int length) {
                return new boolean[length];
            }

            @Override
            public Object get(Object array, int index) {
                return Array.getBoolean(array, index);
            }

            @Override
            public void set(Object array, int index, Object value) {
                Array.setBoolean(array, index, (Boolean) value);
            }
        },

        Character {
            @Override
            public Object newInstance(int length) {
                return new char[length];
            }

            @Override
            public Object get(Object array, int index) {
                return Array.getChar(array, index);
            }

            @Override
            public void set(Object array, int index, Object value) {
                Array.setChar(array, index, (Character) value);
            }
        },

        Object {
            @Override
            public Object newInstance(int length) {
                return new Object[length];
            }

            @Override
            public Object get(Object array, int index) {
                return Array.get(array, index);
            }

            @Override
            public void set(Object array, int index, Object value) {
                Array.set(array, index, value);
            }
        };
    }

    public static interface ArrayAccessor {

        public Object newInstance(int length);

        public Object get(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

        public void set(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
    }

    public static class ComponentArrayAsscessor implements ArrayAccessor {

        private Class componentType;

        public ComponentArrayAsscessor(Class componentType) {
            this.componentType = componentType;
        }

        @Override
        public Object newInstance(int length) {
            return Array.newInstance(componentType, length);
        }

        @Override
        public Object get(Object array, int index) {
            return Array.get(array, index);
        }

        @Override
        public void set(Object array, int index, Object value) {
            Array.set(array, index, value);
        }
    }

    private static Map<Class<?>, Object> clsMapNullArray;

    public static Object nullArray(Class<?> componentClass) {
        if (clsMapNullArray == null) {
            synchronized (KernelArray.class) {
                if (clsMapNullArray == null) {
                    clsMapNullArray.put(byte.class, KernelLang.NULL_BYTES);
                    clsMapNullArray.put(int.class, KernelLang.NULL_INTS);
                    clsMapNullArray.put(long.class, KernelLang.NULL_LONGS);
                    clsMapNullArray.put(String.class, KernelLang.NULL_STRINGS);
                    clsMapNullArray.put(Class.class, KernelLang.NULL_CLASSES);
                }
            }
        }

        Object array = clsMapNullArray.get(componentClass);
        if (array == null) {
            ArrayAccessor accessor = forComponentType(componentClass);
            array = accessor.newInstance(0);
            clsMapNullArray.put(componentClass, array);
        }

        return array;
    }

    public static String serializer(String start, Object array) {
        ArrayAccessor accessor = array == null ? null : forClass(array.getClass());
        if (accessor == null) {
            return null;
        }

        int length = Array.getLength(array);
        if (length == 0) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (start != null) {
            stringBuilder.append(start);
        }

        for (int i = 0; i < length; i++) {
            if (i > 0) {
                stringBuilder.append(',');
            }

            stringBuilder.append(accessor.get(array, i));
        }

        return stringBuilder.toString();
    }

    public static Object deserialize(String start, String params, Class<?> componentClass) {
        if (params == null) {
            return null;
        }

        int length = params.length();
        if (length == 0) {
            if (componentClass == int.class) {
                return KernelLang.NULL_INTS;

            } else if (componentClass == long.class) {
                return KernelLang.NULL_LONGS;

            } else if (componentClass == String.class) {
                return KernelLang.NULL_STRINGS;
            }

            return nullArray(componentClass);
        }

        int pos = 0;
        if (start != null && start.length() > 0) {
            if (params.startsWith(start)) {
                pos = start.length();
            }
        }

        List<String> list = new ArrayList<String>();
        int nPos;
        while (true) {
            nPos = params.indexOf(',', pos);
            if (nPos < pos) {
                list.add(params.substring(pos));
                break;
            }

            list.add(params.substring(pos, nPos));
            pos = nPos + 1;
        }

        ArrayAccessor accessor = forComponentType(componentClass);
        int size = list.size();
        Object array = accessor.newInstance(size);
        for (int i = 0; i < size; i++) {
            accessor.set(array, i, KernelDyna.to(list.get(i), componentClass));
        }

        return array;
    }
}
