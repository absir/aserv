/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-29 下午12:41:38
 */
package com.absir.aserv.system.bean.dto;

import com.absir.core.base.IBase;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Collection;

@SuppressWarnings("rawtypes")
public class IBeanCollectionSerializer extends JsonSerializer<Collection<? extends IBase>> {

    @Override
    public void serialize(Collection<? extends IBase> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartArray();
        for (IBase base : value) {
            jgen.writeObject(base.getId());
        }

        jgen.writeEndArray();
    }

}
