/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-4-1 下午1:14:02
 */
package com.absir.core.util;

import com.absir.core.kernel.*;
import com.absir.core.kernel.KernelArray.ArrayAccessor;
import com.absir.core.kernel.KernelLang.CallbackBreak;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author absir
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class UtilDump {

    /**
     * NULL_STRING
     */
    public static final String NULL_STRING = "NULL";
    /**
     * DUMP_MAX_LEVEL
     */
    private static final int DUMP_MAX_LEVEL = 4;

    /**
     * @param object
     * @return
     */
    public static boolean dumpNull(Object object) {
        if (object == null) {
            System.out.println(NULL_STRING);
            return true;
        }

        return false;
    }

    /**
     * @param array
     */
    public static void dumpArray(Object array) {
        if (dumpNull(array)) {
            return;
        }

        ArrayAccessor accessor = KernelArray.forClass(array.getClass());
        if (accessor == null) {
            return;
        }

        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            System.out.println(accessor.get(array, i));
        }
    }

    /**
     * @param array
     */
    public static void dumpPrint(Object[] array) {
        if (dumpNull(array)) {
            return;
        }

        for (Object obj : array) {
            System.out.println(obj);
        }
    }

    /**
     * @param collection
     */
    public static void dumpCollection(Collection<?> collection) {
        if (dumpNull(collection)) {
            return;
        }

        for (Object obj : collection) {
            System.out.println(obj);
        }
    }

    /**
     * @param map
     */
    public static void dumpMap(Map<?, ?> map) {
        if (dumpNull(map)) {
            return;
        }

        for (Entry<?, ?> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "=>" + entry.getValue());
        }
    }

    /**
     * @param enumeration
     */
    public static void dumpEnumeration(Enumeration enumeration) {
        if (enumeration == null) {
            System.out.println(NULL_STRING);
            return;
        }

        while (enumeration.hasMoreElements()) {
            System.out.println(enumeration.nextElement());
        }
    }

    /**
     * @param str
     * @param level
     */
    private static void dumpPrint(String str, int level) {
        dumpPrint(str, level, false);
    }

    /**
     * @param str
     * @param level
     * @param node
     */
    private static void dumpPrint(String str, int level, boolean node) {
        dumpPrint(str, level, node, null);
    }

    /**
     * @param str
     * @param level
     * @param node
     */
    private static void dumpPrint(String str, int level, boolean node, Object[] params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append(' ');
        }

        if (node) {
            sb.append('.');
        }

        sb.append(str);
        if (params != null) {
            sb.append(':');
            for (Object param : params) {
                sb.append(param);
            }
        }

        System.out.println(sb.toString());
    }

    /**
     * @param obj
     * @param filters
     */
    public static void dumpObject(Object obj, int filters) {
        dumpObject(obj, filters, filters);
    }

    /**
     * @param obj
     * @param fields
     * @param methods
     */
    public static void dumpObject(Object obj, int fields, int methods) {
        dumpObject(obj, fields, methods, 0);
    }

    /**
     * @param obj
     * @param fields
     * @param methods
     * @param ancest
     */
    public static void dumpObject(Object obj, int fields, int methods, int ancest) {
        dumpObject(obj, fields, methods, ancest, 0);
    }

    /**
     * @param obj
     * @param fields
     * @param methods
     * @param ancest
     * @param level
     */
    public static void dumpObject(Object obj, int fields, int methods, int ancest, int level) {
        if (dumpNull(obj)) {
            return;
        }

        dumpObject(obj, fields, methods, ancest, ancest == 0 ? null : new HashSet<Object>(), level, DUMP_MAX_LEVEL);
    }

    /**
     * @param obj
     * @param fields
     * @param methods
     * @param ancest
     * @param ancests
     * @param level
     * @param maxlevel
     */
    private static void dumpObject(Object obj, int fields, int methods, int ancest, final Set<Object> ancests, int level,
                                   int maxlevel) {
        if (maxlevel-- < 0) {
            return;
        }

        dumpPrint("CLASS:" + (obj instanceof Class ? obj : obj.getClass() + ":" + obj) + obj + "==>dump", level++);
        if (fields != -1) {
            dumpFields(obj, fields, methods, ancest, ancests, level, maxlevel);
        }

        if (methods != -1) {
            dumpMethods(obj, methods, ancest, level);
        }
    }

    /**
     * @param obj
     * @param fields
     * @param methods
     * @param ancest
     * @param ancests
     * @param level
     * @param maxlevel
     */
    private static void dumpFields(final Object obj, final int fields, final int methods, final int ancest,
                                   final Set<Object> ancests, final int level, final int maxlevel) {
        final Class cls = obj instanceof Class ? (Class) obj : obj.getClass();
        dumpPrint("FIELDS:" + cls.getName() + ":" + fields, level);
        KernelReflect.doWithDeclaredFields(cls, new CallbackBreak<Field>() {
            @Override
            public void doWith(Field template) throws KernelLang.BreakException {
                if (fields == 0 || (fields & (template.getModifiers() | Modifier.PROTECTED)) == 0) {
                    template.setAccessible(true);
                    Object value = KernelReflect.get(obj, template);
                    dumpPrint(template.getName() + ":" + value, level, true);
                    if (value != null && !KernelClass.isBasicClass(value.getClass()) && cls != obj && ancest != 0
                            && !ancests.contains(value)) {
                        dumpObject(value, fields, methods, ancest > 0 ? ancest - 1 : ancest, ancests, level, maxlevel);
                    }
                }
            }
        });
    }

    /**
     * @param obj
     * @param filters
     * @param level
     */
    private static void dumpMethods(final Object obj, final int filters, final int ancest, final int level) {
        final Class cls = obj instanceof Class ? (Class) obj : obj.getClass();
        dumpPrint("METHODS:" + cls.getName() + ":" + filters, level);
        KernelReflect.doWithDeclaredMethods(cls, new CallbackBreak<Method>() {
            @Override
            public void doWith(Method template) throws KernelLang.BreakException {
                if (filters == 0 || (filters & (template.getModifiers() | Modifier.PROTECTED)) == 0) {
                    dumpPrint(template.getName() + "(" + template.getParameterTypes().length + ")", level, true,
                            template.getParameterTypes());
                }
            }
        });
    }

    /**
     * @param thread
     */
    public static void dumpThreadError(Thread thread) {
        System.out.print(thread + "\r\n");
        StackTraceElement[] elements = thread.getStackTrace();
        for (StackTraceElement element : elements) {
            System.err.print("\tat " + element + "\r\n");
        }
    }

    /**
     * @param threadGroup
     */
    public static void dumpThreadGroupError(ThreadGroup threadGroup) {
        Thread[] threads = new Thread[threadGroup.activeCount() * 2];
        int actualSize = threadGroup.enumerate(threads);
        for (int i = 0; i < actualSize; i++) {
            dumpThreadError(threads[i]);
        }
    }

    /**
     * @param threadPoolExecutor
     * @throws Exception
     */
    public static void dumpThreadPoolExecutorError(ThreadPoolExecutor threadPoolExecutor) throws Exception {
        Collection<Object> workers = (Collection<Object>) KernelObject.declaredGet(threadPoolExecutor, "workers");
        for (Object worker : workers.toArray()) {
            System.out.print(threadPoolExecutor + " => " + worker + "\r\n");
            Object thread = KernelObject.declaredGet(worker, "thread");
            if (thread != null && thread instanceof Thread) {
                UtilDump.dumpThreadError((Thread) thread);
            }
        }
    }
}
