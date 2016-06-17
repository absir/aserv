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
import com.absir.bean.core.BeanConfigImpl;
import com.absir.context.lang.LangBundle;
import com.absir.core.kernel.KernelMap;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.kernel.KernelString;
import com.absir.property.PropertyUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings("rawtypes")
public class HelperLang {

    public static String getLangName(JaLang lang, String name) {
        return getLangName(lang == null ? null : lang.value(), lang == null ? null : lang.tag(), name);
    }

    public static String[] getLangNameLang(JaLang lang, String name) {
        return getLangNameLang(lang == null ? null : lang.value(), lang == null ? null : lang.tag(), name);
    }

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

        return LangBundle.isStrictTag() || KernelString.isEmpty(lang) ? name : lang;
    }

    public static String[] getLangNameLang(String lang, String tag, String name) {
        name = getLangName(lang, tag, name);
        return new String[]{name, lang == null ? name : lang};
    }

    public static String getTypeCaption(Class<?> type) {
        return getTypeCaption(type, null);
    }

    public static String getTypeCaption(Class<?> type, String typeName) {
        return getLangName(BeanConfigImpl.getTypeAnnotation(type, JaLang.class), typeName == null ? type.getSimpleName() : typeName);
    }

    public static String getFieldCaption(Field field) {
        return getFieldCaption(field, field.getDeclaringClass());
    }

    public static String getFieldCaption(Field field, Class<?> cls) {
        String name = cls + "." + field;
        String[] valueTag = LangBundleImpl.ME == null ? null : LangBundleImpl.ME.getLangValueTag(name);
        if (valueTag == null || valueTag.length == 0) {
            return getLangName(PropertyUtils.getFieldAnnotation(cls, field, JaLang.class), KernelString.capitalize(field.getName()));
        }

        return getFieldCaption(valueTag[0], valueTag.length > 1 ? valueTag[1] : name, field.getName(), cls);
    }

    public static String getFieldCaption(String lang, String tag, String field, Class<?> cls) {
        return getLangName(lang, tag, KernelString.capitalize(field));
    }

    public static String getMethodCaption(Method method) {
        return getMethodCaption(method, method.getDeclaringClass());
    }

    public static String getMethodCaption(Method method, Class<?> cls) {
        return getLangName(BeanConfigImpl.getMethodAnnotation(method, JaLang.class), KernelString.capitalize(method.getName()));
    }

    public static String getEnumNameCaption(Enum enumerate) {
        return getLangName(PropertyUtils.getFieldAnnotation(enumerate.getClass(), KernelReflect.declaredField(enumerate.getClass(), enumerate.name()), JaLang.class),
                enumerate.name());
    }

    public static String[] getEnumNameCaptions(Enum enumerate) {
        return getLangNameLang(PropertyUtils.getFieldAnnotation(enumerate.getClass(), KernelReflect.declaredField(enumerate.getClass(), enumerate.name()), JaLang.class),
                enumerate.name());
    }

    public static String getCaptionLang(String lang) {
        String name = KernelMap.getKey(LangBundle.ME.getResourceBundle(), lang);
        if (name == null) {
            name = lang;
        }

        return name;
    }
}
