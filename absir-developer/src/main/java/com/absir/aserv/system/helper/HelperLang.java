/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-12 下午4:40:28
 */
package com.absir.aserv.system.helper;

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

    public static String getLangCaption(String lang, String tag, String name, String cls) {
        return getLangCaption(lang, tag, cls, name, false);
    }

    public static String getLangCaption(String lang, String tag, String name, String cls, boolean simple) {
        if (KernelString.isEmpty(lang)) {
            lang = name;
        }

        if (LangBundle.isI18n() && LangBundle.ME != null) {
            if (KernelString.isEmpty(tag)) {
                lang = getLangCode(lang);

            } else {
                if (cls != null && cls.length() > 0) {
                    int length = tag.length();
                    if (length > 1) {
                        if (tag.charAt(0) == '.') {
                            tag = cls + tag.substring(1);

                        } else if (tag.charAt(length - 1) == '.') {
                            tag = tag.substring(0, length - 1) + cls;
                        }
                    }

                    if (simple || LangBundle.ME.getResourceBundle().containsKey(tag)) {
                        lang = tag;
                    }

                } else {
                    cls = cls + '.' + tag;
                    if (LangBundle.ME.getResourceBundle().containsKey(cls)) {
                        lang = cls;

                    } else {
                        lang = tag;
                    }
                }
            }
        }

        return lang;
    }

    public static String getLangCaption(String lang, String tag, String name, Class<?> cls) {
        if (LangBundle.isI18n()) {
            lang = getLangCaption(lang, tag, name, cls.getName());
            if (lang == tag) {
                lang = getLangCaption(lang, tag, name, cls.getSimpleName(), true);
            }
        }

        return KernelString.isEmpty(lang) ? name : lang;
    }

    public static String getLangCaption(JaLang lang, String name, Class<?> cls) {
        return getLangCaption(lang == null ? null : lang.value(), lang == null ? null : lang.tag(), name, cls);
    }

    public static String getTypeCaption(Class<?> type) {
        return getLangCaption(BeanConfigImpl.getTypeAnnotation(type, JaLang.class), type.getSimpleName(), type);
    }

    public static String getFieldCaption(Field field) {
        return getFieldCaption(field, field.getDeclaringClass());
    }

    public static String getFieldCaption(Field field, Class<?> cls) {
        return getLangCaption(PropertyUtils.getFieldAnnotation(cls, field, JaLang.class), KernelString.capitalize(field.getName()), cls);
    }

    public static String getMethodCaption(Method method) {
        return getMethodCaption(method, method.getDeclaringClass());
    }

    public static String getMethodCaption(Method method, Class<?> cls) {
        return getLangCaption(BeanConfigImpl.getMethodAnnotation(method, JaLang.class), method.getName(), cls);
    }

    public static String getEnumCaption(Enum enumerate) {
        return getLangCaption(PropertyUtils.getFieldAnnotation(enumerate.getClass(), KernelReflect.declaredField(enumerate.getClass(), enumerate.name()), JaLang.class),
                enumerate.name(), enumerate.getClass());
    }

    public static String getLangCode(String lang) {
        if (LangBundle.isI18n() && !LangBundle.ME.getResourceBundle().containsKey(lang)) {
            String name = KernelMap.getKey(LangBundle.ME.getResourceBundle(), lang);
            if (name == null) {
                name = lang;
            }

            return name;
        }

        return lang;
    }

    public static String getLangMessage(String lang) {
        String message = LangBundle.ME.getResourceBundle().get(lang);
        if (message == null) {
            message = lang;
        }

        return message;
    }
}
