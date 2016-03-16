/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-22 下午1:49:32
 */
package com.absir.orm.hibernate.boost;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

import java.lang.reflect.Field;
import java.util.Map;

public interface IEventService {

    public void setEventListenerRegistry(EventListenerRegistry eventListenerRegistry);

    public void boost(Map<String, PersistentClass> classes, PersistentClass persistentClass, Property property, Field field, String referencedEntityName);
}
