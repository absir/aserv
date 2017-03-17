/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-3 下午1:35:42
 */
package com.absir.core.util;

import com.absir.core.kernel.KernelCharset;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@SuppressWarnings("rawtypes")
public class UtilPackage {

    public static String CLASS_SUFFIX_NAME = ".class";

    public static final FileFilter CLASS_FILE_FILTER = new FileFilter() {

        @Override
        public boolean accept(File file) {
            return file.getName().endsWith(CLASS_SUFFIX_NAME);
        }
    };

    public static final FileFilter CLASS_DIR_FILE_FILTER = new FileFilter() {

        @Override
        public boolean accept(File file) {
            return file.isDirectory() || file.getName().endsWith(CLASS_SUFFIX_NAME);
        }
    };

    public static int CLASS_SUFFIX_LENGTH = CLASS_SUFFIX_NAME.length();

    public static List<Class> findClasses(String packageName, boolean iterator) {
        final List<Class> classes = new ArrayList<Class>();
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        findClasses(packageName, iterator, new CallbackBreak<String>() {

            @Override
            public void doWith(String template) throws BreakException {
                try {
                    classes.add(classLoader.loadClass(template));

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        return classes;
    }

    public static void findClasses(String packageName, boolean iterator, final CallbackBreak<String> callback) {
        String packageDir = packageName.replace('.', '/');
        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageDir);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if ("file".equals(url.getProtocol())) {
                    findClasses(packageName, URLDecoder.decode(url.getFile(), KernelCharset.getDefault().name()),
                            iterator, callback);

                } else {
                    findClasses(packageDir, url, iterator, callback);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

        } catch (BreakException e) {
        }
    }

    public static void findClasses(String packageDir, URL jarUrl, boolean iterator,
                                   final CallbackBreak<String> callback) throws IOException, BreakException {
        if ("jar".equals(jarUrl.getProtocol())) {
            JarFile jarFile = ((JarURLConnection) jarUrl.openConnection()).getJarFile();
            findClasses(packageDir, jarFile, iterator, callback);
        }
    }

    public static void findClasses(String packageDir, JarFile jarFile, boolean iterator,
                                   final CallbackBreak<String> callback) throws IOException, BreakException {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntity = entries.nextElement();
            if (!jarEntity.isDirectory()) {
                String jarName = jarEntity.getName();
                if (jarName.endsWith(CLASS_SUFFIX_NAME)) {
                    if (jarName.charAt(0) == '/') {
                        jarName = jarName.substring(1);
                    }

                    if (packageDir == null || jarName.startsWith(packageDir)) {
                        String classname = jarName.substring(0, jarName.length() - CLASS_SUFFIX_LENGTH);
                        callback.doWith(classname.replace('/', '.'));
                    }
                }
            }
        }
    }

    public static void findClasses(String packageName, String packagePath, boolean iterator,
                                   final CallbackBreak<String> callback) throws BreakException {
        File packageDir = new File(packagePath);
        if (!packageDir.exists() || !packageDir.isDirectory()) {
            return;
        }

        File[] packageFiles = packageDir.listFiles(iterator ? CLASS_DIR_FILE_FILTER : CLASS_FILE_FILTER);
        try {
            for (File packageFile : packageFiles) {
                if (packageFile.isDirectory()) {
                    if (iterator) {
                        findClasses(packageName + "." + packageFile.getName(), packageFile.getAbsolutePath(), iterator,
                                callback);
                    }

                } else {
                    callback.doWith(packageName + "."
                            + packageFile.getName().substring(0, packageFile.getName().length() - CLASS_SUFFIX_LENGTH));
                }
            }

        } catch (BreakException e) {
            throw e;
        }
    }
}
