/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-6-8 上午9:44:13
 */
package com.absir.core.util;

import com.absir.core.kernel.KernelObject;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author absir
 */
public class UtilRuntime {

    /**
     * RUNTIME_PATH
     */
    public static final String RUNTIME_PATH = UtilLoader.CLASS_LOADER.getResource("").getPath() + ".runtime/";

    /**
     * RUNTIME_PATTERN
     */
    public static final Pattern RUNTIME_PATTERN = Pattern.compile("[^\\w|.|/|-]", Pattern.MULTILINE);

    /**
     * @param cls
     * @param propertyName
     * @return
     */
    public static String getRuntimeName(Class<?> cls, String propertyName) {
        return cls.getName().replace('.', '/').replace('$', '/') + "/" + RUNTIME_PATTERN.matcher(propertyName).replaceAll("_");
    }

    /**
     * @param runtimeName
     * @return
     */
    public static Object getRuntime(String runtimeName) {
        try {
            return KernelObject.unserialize(UtilFile.read(RUNTIME_PATH + runtimeName));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param cls
     * @param propertyName
     * @return
     */
    public static Object getRuntime(Class<?> cls, String propertyName) {
        return getRuntime(getRuntimeName(cls, propertyName));
    }

    /**
     * synchronized
     *
     * @param runtimeName
     * @param obj
     */
    public static void setRuntime(String runtimeName, Object obj) {
        try {
            UtilFile.write(RUNTIME_PATH + runtimeName, KernelObject.serialize(obj));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param cls
     * @param propertyName
     * @param obj
     */
    public static void setRuntime(Class<?> cls, String propertyName, Object obj) {
        setRuntime(getRuntimeName(cls, propertyName), obj);
    }
}
