/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-30 下午5:09:42
 */
package com.absir.aserv.developer.editor;

import com.absir.aserv.system.bean.value.JaIgnore;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.property.PropertySupply;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@Bean
public class EditorSupply extends PropertySupply<EditorObject, EditorObject> {

    public static final EditorSupply ME = BeanFactoryUtils.get(EditorSupply.class);

    @Override
    public Class<? extends Annotation> getIgnoreAnnotationClass() {
        return JaIgnore.class;
    }

    @Override
    public EditorObject getPropertyObject(EditorObject propertyObject, Field field) {
        if (propertyObject == null) {
            propertyObject = new EditorObject();
        }

        return super.getPropertyObject(propertyObject, field);
    }
}
