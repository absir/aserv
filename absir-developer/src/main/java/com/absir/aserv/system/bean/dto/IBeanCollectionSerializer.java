/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-29 下午12:41:38
 */
package com.absir.aserv.system.bean.dto;

import java.io.IOException;
import java.util.Collection;

import com.absir.core.base.IBase;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public class IBeanCollectionSerializer extends JsonSerializer<Collection<? extends IBase>> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.codehaus.jackson.map.JsonSerializer#serialize(java.lang.Object,
	 * org.codehaus.jackson.JsonGenerator,
	 * org.codehaus.jackson.map.SerializerProvider)
	 */
	@Override
	public void serialize(Collection<? extends IBase> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeStartArray();
		for (IBase base : value) {
			jgen.writeObject(base.getId());
		}

		jgen.writeEndArray();
	}

}
