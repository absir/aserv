/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-22 下午1:49:32
 */
package com.absir.orm.hibernate.boost;

import java.lang.reflect.Field;
import java.util.Map;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

/**
 * @author absir
 * 
 */
public interface IEventService {

	/**
	 * @param eventListenerRegistry
	 */
	public void setEventListenerRegistry(EventListenerRegistry eventListenerRegistry);

	/**
	 * @param classes
	 * @param persistentClass
	 * @param property
	 * @param field
	 * @param referencedEntityName
	 */
	public void boost(Map<String, PersistentClass> classes, PersistentClass persistentClass, Property property, Field field, String referencedEntityName);
}
