/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-30 下午5:23:31
 */
package com.absir.aserv.developer.editor;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.bean.inject.value.Bean;
import com.absir.client.helper.HelperJson;
import com.absir.property.PropertyResolverAbstract;

import java.util.Map;

/**
 * @author absir
 *
 */
@Bean
public class EditorEdit extends PropertyResolverAbstract<EditorObject, JaEdit> {

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.property.PropertyResolverAbstract#getPropertyObjectAnnotation
     * (com.absir.property.PropertyObject, java.lang.annotation.Annotation)
     */
    @Override
    public EditorObject getPropertyObjectAnnotation(EditorObject propertyObject, JaEdit annotation) {
        if (propertyObject == null) {
            propertyObject = new EditorObject();
        }

        propertyObject.setEdit(annotation);
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

        Map<?, ?> map = HelperJson.decodeMap(annotationValue);
        propertyObject.setMetas(map);
        return propertyObject;
    }
}
