/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-30 下午5:23:31
 */
package com.absir.aserv.developer.editor;

import com.absir.aserv.system.bean.value.JaEmbedd;
import com.absir.bean.inject.value.Bean;
import com.absir.core.dyna.DynaBinder;
import com.absir.property.PropertyResolverAbstract;

@Bean
public class EditorEmbedd extends PropertyResolverAbstract<EditorObject, JaEmbedd> {

    @Override
    public EditorObject getPropertyObjectAnnotation(EditorObject propertyObject, JaEmbedd annotation) {
        if (propertyObject == null) {
            propertyObject = new EditorObject();
        }

        propertyObject.setEmbedd(true);
        return propertyObject;
    }

    @Override
    public EditorObject getPropertyObjectAnnotationValue(EditorObject propertyObject, String annotationValue) {
        if (propertyObject == null) {
            propertyObject = new EditorObject();
        }

        propertyObject.setEmbedd(DynaBinder.to(annotationValue, boolean.class));
        return propertyObject;
    }

}
