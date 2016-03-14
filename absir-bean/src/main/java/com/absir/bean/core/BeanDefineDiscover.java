/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-12-23 下午4:31:39
 */
package com.absir.bean.core;

import com.absir.bean.basis.ParamName;
import com.absir.core.kernel.KernelString;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author absir
 */
public class BeanDefineDiscover {

    /**
     * Instance
     */
    public static BeanDefineDiscover Instance;

    /**
     * MethodOrCtorMapParameterNames
     */
    private static Map<AccessibleObject, String[]> MethodOrCtorMapParameterNames = new HashMap<AccessibleObject, String[]>();

    /**
     * adaptiveParanamer
     */
    protected Paranamer adaptiveParanamer = new AdaptiveParanamer();

    /**
     *
     */
    private BeanDefineDiscover() {
    }

    /**
     *
     */
    public static void open() {
        if (Instance == null) {
            Instance = new BeanDefineDiscover();
        }
    }

    /**
     * @param methodOrCtor
     * @return
     */
    public static String[] paramterNames(AccessibleObject methodOrCtor) {
        return paramterNames(methodOrCtor, methodOrCtor instanceof Method ? ((Method) methodOrCtor).getParameterAnnotations() : ((Constructor<?>) methodOrCtor).getParameterAnnotations());
    }

    /**
     * @param methodOrCtor
     * @param parameterAnnotations
     * @return
     */
    public static String[] paramterNames(AccessibleObject methodOrCtor, Annotation[][] parameterAnnotations) {
        if (parameterAnnotations == null || parameterAnnotations.length == 0) {
            return null;
        }

        String[] parameterNames = MethodOrCtorMapParameterNames.get(methodOrCtor);
        if (parameterNames == null) {
            if (Instance != null) {
                parameterNames = Instance.getParamterNames(methodOrCtor);
            }

            int length = parameterAnnotations.length;
            if (parameterNames == null) {
                parameterNames = new String[length];

            } else if (parameterNames.length != length) {
                String[] names = parameterNames;
                parameterNames = new String[length];
                int nameLength = length;
                if (nameLength > names.length) {
                    nameLength = names.length;
                }

                for (int i = 0; i < nameLength; i++) {
                    parameterNames[i] = names[i];
                }
            }

            for (int i = 0; i < length; i++) {
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (annotation instanceof ParamName) {
                        String name = ((ParamName) annotation).value();
                        if (!KernelString.isEmpty(name)) {
                            parameterNames[i] = name;
                        }

                        break;
                    }
                }
            }

            MethodOrCtorMapParameterNames.put(methodOrCtor, parameterNames);
        }

        return parameterNames;
    }

    /**
     *
     */
    public static void clear() {
        if (Instance != null) {
            Instance.adaptiveParanamer = null;
        }

        MethodOrCtorMapParameterNames.clear();
    }

    /**
     * @param methodOrCtor
     * @param annotations
     * @return
     */
    public String[] getParamterNames(AccessibleObject methodOrCtor) {
        if (adaptiveParanamer == null) {
            adaptiveParanamer = new AdaptiveParanamer();
        }

        return adaptiveParanamer.lookupParameterNames(methodOrCtor, false);
    }
}
