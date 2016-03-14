/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-3-11 上午10:37:28
 */
package com.absir.core.util;

import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;
import com.absir.core.kernel.KernelLang.ObjectTemplate;
import com.absir.core.kernel.KernelReflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author absir
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class UtilLoader {

    /**
     * CLASS_LOADER
     */
    public final static ClassLoader CLASS_LOADER = UtilLoader.class.getClassLoader();
    /**
     * Define_Class_Method
     */
    static Method Define_Class_Method = KernelReflect.declaredMethod(ClassLoader.class, "defineClass", new Class[]{String.class, byte[].class, int.class, int.class});

    /**
     * @param cls
     * @param simpleName
     * @param superClass
     * @return
     */
    public static <T> Class<? extends T> iterateClass(Class<?> cls, final String simpleName, final Class<T> superClass) {
        final ObjectTemplate<Class<?>> classTemplate = new ObjectTemplate<Class<?>>();
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        UtilIterator.iterateName(cls.getName(), '.', new CallbackBreak<String>() {

            @Override
            public void doWith(String template) throws BreakException {
                try {
                    Class<?> cls = classLoader.loadClass(template + '.' + simpleName);
                    if (superClass.isAssignableFrom(cls)) {
                        classTemplate.object = cls;
                        throw new BreakException();
                    }

                } catch (ClassNotFoundException e) {
                    // e.printStackTrace();
                }
            }

        });

        return (Class<? extends T>) classTemplate.object;
    }

    /**
     * @param cls
     * @param simpleName
     * @param superClass
     * @return
     */
    public static <T> Class<? extends T> reverseClass(Class<?> cls, final String simpleName, final Class<T> superClass) {
        final ObjectTemplate<Class<?>> classTemplate = new ObjectTemplate<Class<?>>();
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        UtilIterator.reverseName(cls.getName(), '.', new CallbackBreak<String>() {

            @Override
            public void doWith(String template) throws BreakException {
                try {
                    Class<?> cls = classLoader.loadClass(template + '.' + simpleName);
                    if (superClass.isAssignableFrom(cls)) {
                        classTemplate.object = cls;
                        throw new BreakException();
                    }

                } catch (ClassNotFoundException e) {
                    // e.printStackTrace();
                }
            }

        });

        return (Class<? extends T>) classTemplate.object;
    }

    /**
     * @param loader
     * @param name
     * @param b
     * @param off
     * @param len
     * @return
     */
    public static Class defineClass(ClassLoader loader, String name, byte[] b, int off, int len) {
        try {
            return (Class) Define_Class_Method.invoke(loader, name, b, off, len);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();

        } catch (IllegalAccessException e) {
            e.printStackTrace();

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
