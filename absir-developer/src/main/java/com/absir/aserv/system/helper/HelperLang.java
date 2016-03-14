/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-12 下午4:40:28
 */
package com.absir.aserv.system.helper;

import com.absir.aserv.lang.LangBundleImpl;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.context.lang.LangBundle;
import com.absir.core.kernel.KernelMap;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.kernel.KernelString;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author absir
 */
@SuppressWarnings("rawtypes")
public class HelperLang {

    /**
     * @param lang
     * @param name
     * @return
     */
    public static String getLangName(JaLang lang, String name) {
        return getLangName(lang == null ? null : lang.value(), lang == null ? null : lang.tag(), name);
    }

    /**
     * @param lang
     * @param name
     * @return [lang, caption]
     */
    public static String[] getLangNameLang(JaLang lang, String name) {
        return getLangNameLang(lang == null ? null : lang.value(), lang == null ? null : lang.tag(), name);
    }

    /**
     * @param lang
     * @param tag
     * @param name
     * @return
     */
    public static String getLangName(String lang, String tag, String name) {
        if (!KernelString.isEmpty(tag)) {
            if (name != null) {
                int length = tag.length();
                if (length > 1) {
                    if (tag.charAt(0) == '.') {
                        tag = name + tag.substring(1);

                    } else if (tag.charAt(length - 1) == '.') {
                        tag = tag.substring(0, length - 1) + name;
                    }
                }
            }

            name = tag;
        }

        if (LangBundle.ME == null) {
            return lang == null ? name : lang;
        }

        if (lang != null) {
            LangBundle.ME.setResourceLang(name, lang);
        }

        return name;
    }

    /**
     * @param lang
     * @param tag
     * @param name
     * @return
     */
    public static String[] getLangNameLang(String lang, String tag, String name) {
        name = getLangName(lang, tag, name);
        return new String[]{name, lang == null ? name : lang};
    }

    /**
     * @param type
     * @return
     */
    public static String getTypeCaption(Class<?> type) {
        return getTypeCaption(type, null);
    }

    /**
     * @param type
     * @param typeName
     * @return
     */
    public static String getTypeCaption(Class<?> type, String typeName) {
        return getLangName(type.getAnnotation(JaLang.class), typeName == null ? type.getSimpleName() : typeName);
    }

    /**
     * @param field
     * @return
     */
    public static String getFieldCaption(Field field) {
        return getFieldCaption(field, field.getDeclaringClass());
    }

    /**
     * @param field
     * @param cls
     * @return
     */
    public static String getFieldCaption(Field field, Class<?> cls) {
        String name = cls + "." + field;
        String[] valueTag = LangBundleImpl.ME == null ? null : LangBundleImpl.ME.getLangValueTag(name);
        if (valueTag == null || valueTag.length == 0) {
            return getLangName(field.getAnnotation(JaLang.class), KernelString.capitalize(field.getName()));
        }

        return getFieldCaption(valueTag[0], valueTag.length > 1 ? valueTag[1] : name, field.getName(), cls);
    }

    /**
     * @param lang
     * @param cls
     * @return
     */
    public static String getFieldCaption(String lang, String tag, String field, Class<?> cls) {
        return getLangName(lang, tag, KernelString.capitalize(field));
    }

    /**
     * @param method
     * @return
     */
    public static String getMethodCaption(Method method) {
        return getMethodCaption(method, method.getDeclaringClass());
    }

    /**
     * @param method
     * @param cls
     * @return
     */
    public static String getMethodCaption(Method method, Class<?> cls) {
        return getLangName(method.getAnnotation(JaLang.class), KernelString.capitalize(method.getName()));
    }

    /**
     * @param enumerate
     * @return
     */
    public static String getEnumNameCaption(Enum enumerate) {
        return getLangName(KernelReflect.declaredField(enumerate.getClass(), enumerate.name()).getAnnotation(JaLang.class),
                enumerate.name());
    }

    /**
     * @param enumerate
     * @return
     */
    public static String[] getEnumNameCaptions(Enum enumerate) {
        return getLangNameLang(KernelReflect.declaredField(enumerate.getClass(), enumerate.name()).getAnnotation(JaLang.class),
                enumerate.name());
    }

    /**
     * @param lang
     * @return
     */
    public static String getCaptionLang(String lang) {
        String name = KernelMap.getKey(LangBundle.ME.getResourceBundle(), lang);
        if (name == null) {
            name = lang;
        }

        return name;
    }
}
