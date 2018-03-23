/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-5 下午2:31:23
 */
package com.absir.core.kernel;

import com.absir.core.dyna.DynaBinder;

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

        } else if (componentType == String.class) {
            return EnumArrayAccessor.StringAcc;
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

            @Override
            public void join(final StringBuilder stringBuilder, Object array, char separator) {
                byte[] oArray = (byte[]) array;
                int length = oArray.length;
                for (int i = 0; i < length; i++) {
                    if (i > 0) {
                        stringBuilder.append(separator);
                    }

                    stringBuilder.append(oArray[i]);
                }
            }

            @Override
            public Object split(String string, char separator, int start) {
                int size = KernelString.countChar(string, separator, start) + 1;
                byte[] array = new byte[size];
                int nPos;
                size = 0;
                while (true) {
                    nPos = string.indexOf(',', start);
                    if (nPos < start) {
                        try {
                            array[size] = java.lang.Byte.parseByte(string.substring(start));

                        } catch (Exception e) {
                        }

                        break;
                    }

                    try {
                        array[size] = java.lang.Byte.parseByte(string.substring(start, nPos));

                    } catch (Exception e) {
                    }

                    size++;
                    start = nPos + 1;
                }

                return array;
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

            @Override
            public void join(final StringBuilder stringBuilder, Object array, char separator) {
                short[] oArray = (short[]) array;
                int length = oArray.length;
                for (int i = 0; i < length; i++) {
                    if (i > 0) {
                        stringBuilder.append(separator);
                    }

                    stringBuilder.append(oArray[i]);
                }
            }

            @Override
            public Object split(String string, char separator, int start) {
                int size = KernelString.countChar(string, separator, start) + 1;
                short[] array = new short[size];
                int nPos;
                size = 0;
                while (true) {
                    nPos = string.indexOf(',', start);
                    if (nPos < start) {
                        try {
                            array[size] = java.lang.Short.parseShort(string.substring(start));

                        } catch (Exception e) {
                        }

                        break;
                    }

                    try {
                        array[size] = java.lang.Short.parseShort(string.substring(start, nPos));

                    } catch (Exception e) {
                    }

                    size++;
                    start = nPos + 1;
                }

                return array;
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

            @Override
            public void join(final StringBuilder stringBuilder, Object array, char separator) {
                int[] oArray = (int[]) array;
                int length = oArray.length;
                for (int i = 0; i < length; i++) {
                    if (i > 0) {
                        stringBuilder.append(separator);
                    }

                    stringBuilder.append(oArray[i]);
                }
            }

            @Override
            public Object split(String string, char separator, int start) {
                int size = KernelString.countChar(string, separator, start) + 1;
                int[] array = new int[size];
                int nPos;
                size = 0;
                while (true) {
                    nPos = string.indexOf(',', start);
                    if (nPos < start) {
                        try {
                            array[size] = java.lang.Integer.parseInt(string.substring(start));

                        } catch (Exception e) {
                        }

                        break;
                    }

                    try {
                        array[size] = java.lang.Integer.parseInt(string.substring(start, nPos));

                    } catch (Exception e) {
                    }

                    size++;
                    start = nPos + 1;
                }

                return array;
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

            @Override
            public void join(final StringBuilder stringBuilder, Object array, char separator) {
                long[] oArray = (long[]) array;
                int length = oArray.length;
                for (int i = 0; i < length; i++) {
                    if (i > 0) {
                        stringBuilder.append(separator);
                    }

                    stringBuilder.append(oArray[i]);
                }
            }

            @Override
            public Object split(String string, char separator, int start) {
                int size = KernelString.countChar(string, separator, start) + 1;
                long[] array = new long[size];
                int nPos;
                size = 0;
                while (true) {
                    nPos = string.indexOf(',', start);
                    if (nPos < start) {
                        try {
                            array[size] = java.lang.Long.parseLong(string.substring(start));

                        } catch (Exception e) {
                        }

                        break;
                    }

                    try {
                        array[size] = java.lang.Long.parseLong(string.substring(start, nPos));

                    } catch (Exception e) {
                    }

                    size++;
                    start = nPos + 1;
                }

                return array;
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

            @Override
            public void join(final StringBuilder stringBuilder, Object array, char separator) {
                float[] oArray = (float[]) array;
                int length = oArray.length;
                for (int i = 0; i < length; i++) {
                    if (i > 0) {
                        stringBuilder.append(separator);
                    }

                    stringBuilder.append(oArray[i]);
                }
            }

            @Override
            public Object split(String string, char separator, int start) {
                int size = KernelString.countChar(string, separator, start) + 1;
                float[] array = new float[size];
                int nPos;
                size = 0;
                while (true) {
                    nPos = string.indexOf(',', start);
                    if (nPos < start) {
                        try {
                            array[size] = java.lang.Float.parseFloat(string.substring(start));

                        } catch (Exception e) {
                        }

                        break;
                    }

                    try {
                        array[size] = java.lang.Float.parseFloat(string.substring(start, nPos));

                    } catch (Exception e) {
                    }

                    size++;
                    start = nPos + 1;
                }

                return array;
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

            @Override
            public void join(final StringBuilder stringBuilder, Object array, char separator) {
                double[] oArray = (double[]) array;
                int length = oArray.length;
                for (int i = 0; i < length; i++) {
                    if (i > 0) {
                        stringBuilder.append(separator);
                    }

                    stringBuilder.append(oArray[i]);
                }
            }

            @Override
            public Object split(String string, char separator, int start) {
                int size = KernelString.countChar(string, separator, start) + 1;
                double[] array = new double[size];
                int nPos;
                size = 0;
                while (true) {
                    nPos = string.indexOf(',', start);
                    if (nPos < start) {
                        try {
                            array[size] = java.lang.Double.parseDouble(string.substring(start));

                        } catch (Exception e) {
                        }

                        break;
                    }

                    try {
                        array[size] = java.lang.Double.parseDouble(string.substring(start, nPos));

                    } catch (Exception e) {
                    }

                    size++;
                    start = nPos + 1;
                }

                return array;
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

            @Override
            public void join(final StringBuilder stringBuilder, Object array, char separator) {
                boolean[] oArray = (boolean[]) array;
                int length = oArray.length;
                for (int i = 0; i < length; i++) {
                    if (i > 0) {
                        stringBuilder.append(separator);
                    }

                    stringBuilder.append(oArray[i] ? '1' : '0');
                }
            }

            @Override
            public Object split(String string, char separator, int start) {
                int size = KernelString.countChar(string, separator, start) + 1;
                boolean[] array = new boolean[size];
                int nPos;
                size = 0;
                while (true) {
                    nPos = string.indexOf(',', start);
                    if (nPos < start) {
                        String str = string.substring(start);
                        try {
                            array[size] = java.lang.Integer.parseInt(str) != 0;

                        } catch (Exception e) {
                            try {
                                array[size] = java.lang.Boolean.parseBoolean(str);

                            } catch (Exception ex) {

                            }
                        }

                        break;
                    }

                    String str = string.substring(start, nPos);
                    try {
                        array[size] = java.lang.Integer.parseInt(str) != 0;

                    } catch (Exception e) {
                        try {
                            array[size] = java.lang.Boolean.parseBoolean(str);

                        } catch (Exception ex) {

                        }
                    }

                    size++;
                    start = nPos + 1;
                }

                return array;
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

            @Override
            public void join(final StringBuilder stringBuilder, Object array, char separator) {
                char[] oArray = (char[]) array;
                int length = oArray.length;
                for (int i = 0; i < length; i++) {
                    if (i > 0) {
                        stringBuilder.append(separator);
                    }

                    stringBuilder.append(oArray[i]);
                }
            }

            @Override
            public Object split(String string, char separator, int start) {
                int size = KernelString.countChar(string, separator, start) + 1;
                char[] array = new char[size];
                int nPos;
                size = 0;
                while (true) {
                    nPos = string.indexOf(',', start);
                    if (nPos < start) {
                        array[size] = start < string.length() ? string.charAt(start) : (char) 0;
                        break;
                    }

                    array[size] = start < nPos ? string.charAt(start) : (char) 0;
                    size++;
                    start = nPos + 1;
                }

                return array;
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

            public void join(final StringBuilder stringBuilder, Object array, char separator) {
                Object[] oArray = (Object[]) array;
                int length = oArray.length;
                Object o;
                for (int i = 0; i < length; i++) {
                    if (i > 0) {
                        stringBuilder.append(separator);
                    }

                    o = oArray[i];
                    if (o != null) {
                        stringBuilder.append(o);
                    }
                }
            }

            @Override
            public Object split(String string, char separator, int start) {
                return StringAcc.split(string, separator, start);
            }
        },

        StringAcc {
            @Override
            public Object newInstance(int length) {
                return new String[length];
            }

            @Override
            public Object get(Object array, int index) {
                return Array.get(array, index);
            }

            @Override
            public void set(Object array, int index, Object value) {
                Array.set(array, index, value);
            }

            public void join(final StringBuilder stringBuilder, Object array, char separator) {
                String[] sArray = (String[]) array;
                int length = sArray.length;
                String s;
                for (int i = 0; i < length; i++) {
                    if (i > 0) {
                        stringBuilder.append(separator);
                    }

                    s = sArray[i];
                    if (s != null) {
                        stringBuilder.append(s);
                    }
                }
            }

            @Override
            public Object split(String string, char separator, int start) {
                int size = KernelString.countChar(string, separator, start) + 1;
                String[] array = new String[size];
                int nPos;
                size = 0;
                while (true) {
                    nPos = string.indexOf(',', start);
                    if (nPos < start) {
                        array[size] = string.substring(start);
                        break;
                    }

                    array[size] = string.substring(start, nPos);
                    size++;
                    start = nPos + 1;
                }

                return array;
            }
        }
    }

    public static interface ArrayAccessor {

        public Object newInstance(int length);

        public Object get(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

        public void set(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

        public void join(final StringBuilder stringBuilder, Object array, char separator);

        public Object split(String string, char separator, int start);
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

        public void join(final StringBuilder stringBuilder, Object array, char separator) {
            EnumArrayAccessor.Object.join(stringBuilder, array, separator);
        }

        @Override
        public Object split(String string, char separator, int start) {
            int size = KernelString.countChar(string, separator, start) + 1;
            Object[] array = (Object[]) newInstance(size);
            int nPos;
            size = 0;
            while (true) {
                nPos = string.indexOf(',', start);
                if (nPos < start) {
                    array[size] = DynaBinder.to(string.substring(start), componentType);
                    break;
                }

                array[size] = DynaBinder.to(string.substring(start, nPos), componentType);
                size++;
                start = nPos + 1;
            }

            return array;
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
        if (array == null || !array.getClass().isArray()) {
            return null;
        }

        int length = Array.getLength(array);
        if (length == 0) {
            return "";
        }

        final StringBuilder stringBuilder = new StringBuilder(length * 16);
        if (start != null) {
            stringBuilder.append(start);
        }

        ArrayAccessor accessor = forComponentType(array.getClass().getComponentType());
        accessor.join(stringBuilder, array, ',');
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

        ArrayAccessor accessor = forComponentType(componentClass);
        return accessor.split(params, ',', pos);
    }
}
