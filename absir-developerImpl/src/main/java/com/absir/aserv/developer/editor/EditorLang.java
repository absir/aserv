/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-30 下午5:23:31
 */
package com.absir.aserv.developer.editor;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.bean.inject.value.Bean;
import com.absir.property.PropertyResolverAbstract;

@Bean
public class EditorLang extends PropertyResolverAbstract<EditorObject, JaLang> {

    @Override
    public EditorObject getPropertyObjectAnnotation(EditorObject propertyObject, JaLang annotation) {
        if (propertyObject == null) {
            propertyObject = new EditorObject();
        }

        propertyObject.setLang(annotation.value());
        propertyObject.setTag(annotation.tag());
        return propertyObject;
    }

    @Override
    public EditorObject getPropertyObjectAnnotationValue(EditorObject propertyObject, String annotationValue) {
        if (propertyObject == null) {
            propertyObject = new EditorObject();
        }

        String[] anntations = annotationValue.split(",");
        if (anntations.length == 2) {
            propertyObject.setLang(anntations[0]);
            propertyObject.setTag(anntations[1]);

        } else {
            propertyObject.setLang(annotationValue);
        }

        return propertyObject;
    }

}
