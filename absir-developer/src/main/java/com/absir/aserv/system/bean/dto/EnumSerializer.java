/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-24 下午4:50:50
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
public class EnumSerializer extends JsonSerializer<Enum<?>> {

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.jackson.map.JsonSerializer#serialize(java.lang.Object,
     * org.codehaus.jackson.JsonGenerator,
     * org.codehaus.jackson.map.SerializerProvider)
     */
    @Override
    public void serialize(Enum<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeObject(value.ordinal());
    }
}
