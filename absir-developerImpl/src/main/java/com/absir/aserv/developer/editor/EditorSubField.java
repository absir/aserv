/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-30 下午5:23:31
 */
package com.absir.aserv.developer.editor;

import com.absir.aserv.system.bean.value.JaSubField;
import com.absir.bean.inject.value.Bean;
import com.absir.property.PropertyResolverAbstract;

/**
 * @author absir
 */
@Bean
public class EditorSubField extends PropertyResolverAbstract<EditorObject, JaSubField> {

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.property.PropertyResolverAbstract#getPropertyObjectAnnotation
     * (com.absir.property.PropertyObject, java.lang.annotation.Annotation)
     */
    @Override
    public EditorObject getPropertyObjectAnnotation(EditorObject propertyObject, JaSubField annotation) {
        if (propertyObject == null) {
            propertyObject = new EditorObject();
        }

        propertyObject.setMeta("subField", annotation.value());
        propertyObject.setMeta("subFieldLang", annotation.capition());
        return propertyObject;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.property.PropertyResolverAbstract#getPropertyObjectAnnotationValue
     * (com.absir.property.PropertyObject, java.lang.String)
     */
    @Override
    public EditorObject getPropertyObjectAnnotationValue(EditorObject propertyObject, String annotationValue) {
        if (propertyObject == null) {
            propertyObject = new EditorObject();
        }

        String[] anntations = annotationValue.split(",");
        if (anntations.length == 2) {
            propertyObject.setMeta("subField", anntations[0]);
            propertyObject.setMeta("subFieldLang", anntations[1]);

        } else {
            propertyObject.setMeta("subField", annotationValue);
        }

        return propertyObject;
    }
}
