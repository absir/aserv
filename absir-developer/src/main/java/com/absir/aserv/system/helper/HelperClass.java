/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-28 下午4:49:18
 */
package com.absir.aserv.system.helper;

import java.io.File;

@SuppressWarnings("rawtypes")
public class HelperClass {

    public static final String CLASS_FILE_EXTENSION = ".class";

    public static File getClassFile(Class cls) {
        File file = new File(cls.getResource(cls.getSimpleName().concat(CLASS_FILE_EXTENSION)).getFile());
        if (!file.exists()) {
            file = new File(cls.getProtectionDomain().getCodeSource().getLocation().getFile());
        }

        return file;
    }

    public static Long lastModified(Class cls) {
        return getClassFile(cls).lastModified();
    }
}
