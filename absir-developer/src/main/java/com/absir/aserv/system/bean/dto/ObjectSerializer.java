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

public class ObjectSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (value instanceof Number) {
            jgen.writeString(value.toString());

        } else {
            jgen.writeObject(value);
        }
    }
}
