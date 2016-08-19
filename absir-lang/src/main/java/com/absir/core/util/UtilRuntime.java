/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-8 上午9:44:13
 */
package com.absir.core.util;

import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelObject;

import java.io.IOException;
import java.util.regex.Pattern;

public class UtilRuntime {

    public static final String RUNTIME_PATH = UtilLoader.CLASS_LOADER.getResource("").getPath() + ".runtime/";

    public static final Pattern RUNTIME_PATTERN = Pattern.compile("[^\\w|.|/|-]", Pattern.MULTILINE);

    public static String getRuntimeName(Class<?> cls, String propertyName) {
        return cls.getName().replace('.', '/').replace('$', '/') + "/" + RUNTIME_PATTERN.matcher(propertyName).replaceAll("_");
    }

    public static Object getRuntime(String runtimeName) {
        try {
            return KernelObject.unserialize(UtilFile.read(RUNTIME_PATH + runtimeName));

        } catch (IOException e) {
            Environment.throwable(e);
        }

        return null;
    }

    public static Object getRuntime(Class<?> cls, String propertyName) {
        return getRuntime(getRuntimeName(cls, propertyName));
    }

    public static void setRuntime(String runtimeName, Object obj) {
        try {
            UtilFile.write(RUNTIME_PATH + runtimeName, KernelObject.serialize(obj));

        } catch (IOException e) {
            Environment.throwable(e);
        }
    }

    public static void setRuntime(Class<?> cls, String propertyName, Object obj) {
        setRuntime(getRuntimeName(cls, propertyName), obj);
    }
}
