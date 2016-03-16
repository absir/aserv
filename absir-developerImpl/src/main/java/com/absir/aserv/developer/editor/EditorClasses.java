/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-30 下午5:23:31
 */
package com.absir.aserv.developer.editor;

import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelClass;
import com.absir.orm.value.JaClasses;
import com.absir.property.PropertyResolverAbstract;

@Bean
public class EditorClasses extends PropertyResolverAbstract<EditorObject, JaClasses> {

    @Override
    public EditorObject getPropertyObjectAnnotation(EditorObject propertyObject, JaClasses annotation) {
        if (propertyObject == null) {
            propertyObject = new EditorObject();
        }

        propertyObject.setKeyClass(annotation.key());
        propertyObject.setValueClass(annotation.value());
        return propertyObject;
    }

    @Override
    public EditorObject getPropertyObjectAnnotationValue(EditorObject propertyObject, String annotationValue) {
        if (propertyObject == null) {
            propertyObject = new EditorObject();
        }

        String[] names = annotationValue.split(",");
        if (names.length == 1) {
            propertyObject.setValueClass(KernelClass.forName(names[0]));

        } else if (names.length == 2) {
            propertyObject.setKeyClass(KernelClass.forName(names[0]));
            propertyObject.setValueClass(KernelClass.forName(names[1]));
        }

        return propertyObject;
    }

}
