/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-16 上午10:25:39
 */
package com.absir.bean.core;

import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;
import com.absir.core.kernel.KernelPattern;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilPackage;

import java.util.*;
import java.util.regex.Pattern;

public class BeanScanner {

    private static String ABSIR_BEAN_PACKAGE = "com.absir";

    public final void scanBeanTypes(Collection<Class<?>> beanTypes, Set<String> includePackages, Set<String> excludePackages, Set<String> filtPatterns) {
        List<String> unPackageNames = getunPackageNames(excludePackages);
        List<String> packageNames = new ArrayList<String>();
        if (ABSIR_BEAN_PACKAGE != null) {
            includePackages.add(ABSIR_BEAN_PACKAGE);
            ABSIR_BEAN_PACKAGE = null;
        }

        for (String includePackage : includePackages) {
            addPackageNames(packageNames, includePackage, unPackageNames);
        }

        List<Pattern> unPatterns = new ArrayList<Pattern>();
        for (String filtPattern : filtPatterns) {
            unPatterns.add(Pattern.compile(filtPattern));
        }

        scanBeanTypes(beanTypes, KernelCollection.toArray(unPackageNames, String.class), KernelCollection.toArray(packageNames, String.class), KernelCollection.toArray(unPatterns, Pattern.class));
    }

    private List<String> getunPackageNames(Set<String> excludePackages) {
        List<String> unPackageNames = new ArrayList<String>();
        for (String excludePackage : excludePackages) {
            int size = unPackageNames.size();
            for (int i = 0; i < size; i++) {
                String unPackageName = unPackageNames.get(i);
                if (excludePackage.startsWith(unPackageName)) {
                    continue;

                } else if (unPackageName.startsWith(excludePackage)) {
                    unPackageNames.set(i, excludePackage);
                    continue;
                }
            }
        }

        return unPackageNames;
    }

    private void addPackageNames(List<String> packageNames, String includePackage, List<String> unPackageNames) {
        int size = packageNames.size();
        for (int i = 0; i < size; i++) {
            String packageName = packageNames.get(i);
            if (includePackage.startsWith(packageName)) {
                return;

            } else if (packageName.startsWith(includePackage)) {
                packageNames.set(i, includePackage);
                return;
            }
        }

        for (String unPackageName : unPackageNames) {
            if (includePackage.startsWith(unPackageName)) {
                return;
            }
        }

        packageNames.add(includePackage);
    }

    protected void scanBeanTypes(Collection<Class<?>> beanTypes, final String[] unPackageNames, final String[] packageNames, final Pattern[] unPatterns) {
        final Set<String> classnames = new HashSet<String>();
        scanBeanTypes(packageNames, new CallbackBreak<String>() {

            @Override
            public void doWith(String template) throws BreakException {
                if (!(KernelString.startStrings(template, unPackageNames) || KernelPattern.matchPatterns(template, unPatterns))) {
                    classnames.add(template);
                }
            }
        });

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (String classname : classnames) {
            try {
                beanTypes.add(classLoader.loadClass(classname));

            } catch (ClassNotFoundException e) {
                if (BeanFactoryUtils.getEnvironment().compareTo(Environment.DEBUG) <= 0) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void scanBeanTypes(String[] packageNames, CallbackBreak<String> callbackBreak) {
        for (String packageName : packageNames) {
            UtilPackage.findClasses(packageName, true, callbackBreak);
        }
    }
}
