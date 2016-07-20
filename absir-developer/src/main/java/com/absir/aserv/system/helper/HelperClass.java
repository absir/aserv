/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-28 下午4:49:18
 */
package com.absir.aserv.system.helper;

import com.absir.client.helper.HelperEncrypt;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.kernel.KernelString;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("rawtypes")
public class HelperClass {

    public static final String CLASS_FILE_EXTENSION = ".class";
    private static Map<Class, String> clsMapIdentity = new HashMap<Class, String>();

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

    public static String getClassIdentity(Class cls, boolean cache) {
        String identity = clsMapIdentity.get(cls);
        if (identity != null) {
            return identity;
        }

        final Set<String> fields = new TreeSet<String>();
        KernelReflect.doWithDeclaredFields(cls, new KernelLang.CallbackBreak<Field>() {
            @Override
            public void doWith(Field template) throws KernelLang.BreakException {
                fields.add(template.getName());
            }
        });

        identity = KernelString.implode(fields, ',');
        identity = cls.getSimpleName() + ":" + HelperEncrypt.encryptionMD5(identity);
        if (cache) {
            clsMapIdentity.put(cls, identity);
        }

        return identity;
    }
}
