/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-1 下午3:48:25
 */
package com.absir.aserv.system.bean.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author absir
 *
 */
public class ObjectSerializer extends JsonSerializer<Object> {

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.jackson.map.JsonSerializer#serialize(java.lang.Object,
     * org.codehaus.jackson.JsonGenerator,
     * org.codehaus.jackson.map.SerializerProvider)
     */
    @Override
    public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (value instanceof Number) {
            jgen.writeString(value.toString());

        } else {
            jgen.writeObject(value);
        }
    }
}
