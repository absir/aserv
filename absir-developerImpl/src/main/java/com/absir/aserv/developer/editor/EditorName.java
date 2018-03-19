/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-30 下午5:23:31
 */
package com.absir.aserv.developer.editor;

import com.absir.aserv.system.bean.value.JaName;
import com.absir.bean.inject.value.Bean;
import com.absir.property.PropertyResolverAbstract;

@Bean
public class EditorName extends PropertyResolverAbstract<EditorObject, JaName> {

    @Override
    public EditorObject getPropertyObjectAnnotation(EditorObject propertyObject, JaName annotation) {
        if (propertyObject == null) {
            propertyObject = new EditorObject();
        }

        propertyObject.setValueName(annotation.value());
        return propertyObject;
    }

    @Override
    public EditorObject getPropertyObjectAnnotationValue(EditorObject propertyObject, String annotationValue) {
        if (propertyObject == null) {
            propertyObject = new EditorObject();
        }

        propertyObject.setValueName(annotationValue);
        return propertyObject;
    }

}
