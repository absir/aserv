/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-30 下午5:23:31
 */
package com.absir.aserv.developer.editor;

import com.absir.bean.inject.value.Bean;
import com.absir.core.dyna.DynaBinder;
import com.absir.property.PropertyResolverAbstract;

import javax.persistence.GeneratedValue;

@Bean
public class EditorGenerated extends PropertyResolverAbstract<EditorObject, GeneratedValue> {

    @Override
    public EditorObject getPropertyObjectAnnotation(EditorObject propertyObject, GeneratedValue annotation) {
        if (propertyObject == null) {
            propertyObject = new EditorObject();
        }

        propertyObject.setGenerated(true);
        return propertyObject;
    }

    @Override
    public EditorObject getPropertyObjectAnnotationValue(EditorObject propertyObject, String annotationValue) {
        if (propertyObject == null) {
            propertyObject = new EditorObject();
        }

        propertyObject.setGenerated(DynaBinder.to(annotationValue, boolean.class));
        return propertyObject;
    }

}
