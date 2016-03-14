/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-21 下午2:10:45
 */
package com.absir.aserv.system.helper;

import com.absir.core.util.UtilAccessor;
import com.absir.core.util.UtilAccessor.Accessor;
import com.absir.property.PropertyUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author absir
 *
 */
public class HelperAccessor {

    /**
     * @param field
     * @return
     */
    public static boolean isAccessor(Field field) {
        return (field.getModifiers() & PropertyUtils.TRANSIENT_MODIFIER) == 0;
    }

    /**
     * @param cls
     * @param filterAnnotations
     * @return
     */
    public static List<Field> getFields(Class<?> cls, Class<? extends Annotation>... filterAnnotations) {
        List<Field> fields = new ArrayList<Field>();
        List<Field> fieldSopes = new ArrayList<Field>();
        while (cls != null) {
            for (Field field : cls.getDeclaredFields()) {
                if (isAccessor(field)) {
                    for (Class<? extends Annotation> filterAnnotation : filterAnnotations) {
                        if (field.getAnnotation(filterAnnotation) != null) {
                            continue;
                        }
                    }

                    field.setAccessible(true);
                    fieldSopes.add(field);
                }
            }

            fields.addAll(0, fieldSopes);
            fieldSopes.clear();
            cls = cls.getSuperclass();
        }

        return fields;
    }

    /**
     * @param cls
     * @param filterAnnotations
     * @return
     */
    public static List<Accessor> getXlsAccessors(Class<?> cls, Class<? extends Annotation>... filterAnnotations) {
        List<Accessor> accessors = new ArrayList<Accessor>();
        for (Field field : getFields(cls, filterAnnotations)) {
            accessors.add(UtilAccessor.getAccessor(cls, field));
        }

        return accessors;
    }
}
