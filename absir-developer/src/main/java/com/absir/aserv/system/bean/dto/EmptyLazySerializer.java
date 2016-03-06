/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-29 下午12:29:09
 */
package com.absir.aserv.system.bean.dto;

import java.io.IOException;

import org.hibernate.proxy.HibernateProxy;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author absir
 * 
 */
public class EmptyLazySerializer extends JsonSerializer<Object> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.codehaus.jackson.map.JsonSerializer#serialize(java.lang.Object,
	 * org.codehaus.jackson.JsonGenerator,
	 * org.codehaus.jackson.map.SerializerProvider)
	 */
	@Override
	public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		if (value == null || value instanceof HibernateProxy) {
			jgen.writeObject(null);

		} else {
			jgen.writeObject(value);
		}
	}
}
