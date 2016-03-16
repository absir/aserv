/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-24 下午2:23:23
 */
package com.absir.aserv.system.bean.dto;

import com.absir.core.base.IBase;
import com.absir.core.dyna.DynaBinder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

@SuppressWarnings("rawtypes")
public class IBeanKeySerializer extends JsonSerializer<IBase> {

    @Override
    public void serialize(IBase value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeFieldName(DynaBinder.to(value.getId(), String.class));
    }
}
