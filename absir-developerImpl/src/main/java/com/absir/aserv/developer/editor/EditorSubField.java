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
import com.absir.bean.lang.LangCodeUtils;
import com.absir.core.kernel.KernelString;
import com.absir.property.PropertyResolverAbstract;

@Bean
public class EditorSubField extends PropertyResolverAbstract<EditorObject, JaSubField> {

    @Override
    public EditorObject getPropertyObjectAnnotation(EditorObject propertyObject, JaSubField annotation) {
        if (propertyObject == null) {
            propertyObject = new EditorObject();
        }

        String lang = annotation.value();
        lang = LangCodeUtils.get(lang, EditorSubField.class);
        propertyObject.setMeta("subField", lang);
        String caption = annotation.caption();
        propertyObject.setMeta("subFieldLang", KernelString.isEmpty(caption) ? lang : caption);
        return propertyObject;
    }

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
