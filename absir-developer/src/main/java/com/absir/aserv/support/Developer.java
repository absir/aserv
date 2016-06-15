/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-8 下午1:09:42
 */
package com.absir.aserv.support;

import com.absir.aserv.support.developer.IDeveloper;
import com.absir.aserv.support.developer.IRender;
import com.absir.aserv.system.helper.HelperString;
import com.absir.bean.basis.Configure;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.base.Environment;
import com.absir.core.helper.HelperFile;
import com.absir.core.kernel.KernelLang.CallbackTemplate;
import com.absir.core.kernel.KernelLang.ObjectEntry;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilFile;
import com.absir.orm.value.JoEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
@Configure
public abstract class Developer {

    public static final String CLASS_FILE_EXTENSION = ".class";

    public static final String RUNTIME_PATH = "META-RUNTIME/";

    private static final List<CallbackTemplate<Entry<String, File>>> RUMTIME_LISTENERS = new ArrayList<CallbackTemplate<Entry<String, File>>>();

    public static boolean isDeveloper() {
        return IDeveloper.ME != null;
    }

    public static File getClassFile(Class cls) {
        File file = new File(cls.getResource(cls.getSimpleName().concat(CLASS_FILE_EXTENSION)).getFile());
        if (!file.exists()) {
            file = new File(cls.getProtectionDomain().getCodeSource().getLocation().getFile());
        }

        return file;
    }

    public static Long lastModified(JoEntity joEntity) {
        return getClassFile(joEntity.getClass()).lastModified();
    }

    public synchronized static void addListener(CallbackTemplate<Entry<String, File>> listener) {
        RUMTIME_LISTENERS.add(listener);
    }

    public synchronized static void removeListener(CallbackTemplate<Entry<String, File>> listener) {
        RUMTIME_LISTENERS.remove(listener);
    }

    public static void doEntry(Entry<String, File> entry) {
        for (CallbackTemplate<Entry<String, File>> listener : RUMTIME_LISTENERS) {
            listener.doWith(entry);
        }
    }

    public static void doEntry(File file) {
        if (!RUMTIME_LISTENERS.isEmpty()) {
            String path = file.getPath();
            path = HelperString.substringAfter(path, BeanFactoryUtils.getBeanConfig().getClassPath());
            if (!KernelString.isEmpty(path)) {
                doEntry(new ObjectEntry<String, File>(path, file));
            }
        }
    }

    public static void writeEntry(File file, CharSequence data) throws IOException {
        HelperFile.write(file, data);
        doEntry(file);
    }

    public static void writeGenerate(String filePath, CharSequence data) throws IOException {
        File file = new File(IRender.ME.getRealPath(filePath));
        HelperFile.write(file, data);
        if (IDeveloper.ME != null) {
            IDeveloper.ME.copyDeveloper(file, filePath);
        }
    }

    private static File getRuntimeFile(String runtimeName) {
        return new File(BeanFactoryUtils.getBeanConfig().getClassPath() + RUNTIME_PATH + runtimeName);
    }

    public static Object getRuntime(String runtimeName) {
        try {
            return KernelObject.unserialize(UtilFile.read(getRuntimeFile(runtimeName)));

        } catch (IOException e) {
            Environment.throwable(e);
            return null;
        }
    }

    public synchronized static void setRuntime(String runtimeName, Object value) {
        if (value == null) {
            return;
        }

        try {
            File runtimeFile = getRuntimeFile(runtimeName);
            UtilFile.write(runtimeFile, KernelObject.serialize(value));
            if (RUMTIME_LISTENERS.size() > 0) {
                ObjectEntry<String, File> entry = new ObjectEntry<String, File>();
                entry.setKey(RUNTIME_PATH + runtimeName);
                entry.setValue(runtimeFile);
                doEntry(entry);
            }

        } catch (IOException e) {
            Environment.throwable(e);
        }
    }
}
