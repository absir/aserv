/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-4-30 下午5:23:31
 */
package com.absir.aserv.developer.editor;

import javax.persistence.GeneratedValue;

import com.absir.bean.inject.value.Bean;
import com.absir.core.dyna.DynaBinder;
import com.absir.property.PropertyResolverAbstract;

/**
 * @author absir
 * 
 */
@Bean
public class EditorGenerated extends PropertyResolverAbstract<EditorObject, GeneratedValue> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.property.PropertyResolverAbstract#getPropertyObjectAnnotation
	 * (com.absir.property.PropertyObject, java.lang.annotation.Annotation)
	 */
	@Override
	public EditorObject getPropertyObjectAnnotation(EditorObject propertyObject, GeneratedValue annotation) {
		if (propertyObject == null) {
			propertyObject = new EditorObject();
		}

		propertyObject.setGenerated(true);
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

		propertyObject.setGenerated(DynaBinder.to(annotationValue, boolean.class));
		return propertyObject;
	}

}
