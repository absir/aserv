/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-4-30 下午5:23:31
 */
package com.absir.aserv.developer.editor;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.bean.inject.value.Bean;
import com.absir.property.PropertyResolverAbstract;

/**
 * @author absir
 * 
 */
@Bean
public class EditorLang extends PropertyResolverAbstract<EditorObject, JaLang> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.property.PropertyResolverAbstract#getPropertyObjectAnnotation
	 * (com.absir.property.PropertyObject, java.lang.annotation.Annotation)
	 */
	@Override
	public EditorObject getPropertyObjectAnnotation(EditorObject propertyObject, JaLang annotation) {
		if (propertyObject == null) {
			propertyObject = new EditorObject();
		}

		propertyObject.setLang(annotation.value());
		propertyObject.setTag(annotation.tag());
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
			propertyObject.setLang(anntations[0]);
			propertyObject.setTag(anntations[1]);

		} else {
			propertyObject.setLang(annotationValue);
		}

		return propertyObject;
	}

}
