/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-4-30 下午5:23:31
 */
package com.absir.aserv.developer.editor;

import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.bean.inject.value.Bean;
import com.absir.property.PropertyResolverAbstract;

/**
 * @author absir
 * 
 */
@Bean
public class EditorCrud extends PropertyResolverAbstract<EditorObject, JaCrud> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.property.PropertyResolverAbstract#getPropertyObjectAnnotation
	 * (com.absir.property.PropertyObject, java.lang.annotation.Annotation)
	 */
	@Override
	public EditorObject getPropertyObjectAnnotation(EditorObject propertyObject, JaCrud annotation) {
		if (propertyObject == null) {
			propertyObject = new EditorObject();
		}

		propertyObject.setCrud(annotation);
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

		propertyObject.setCrudValue(annotationValue);
		return propertyObject;
	}

}
