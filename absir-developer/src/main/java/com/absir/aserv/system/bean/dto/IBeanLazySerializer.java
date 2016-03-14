/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-29 下午12:29:09
 */
package com.absir.aserv.system.bean.dto;

import com.absir.aserv.system.helper.HelperBase;
import com.absir.core.base.IBase;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author absir
 *
 */
@SuppressWarnings("rawtypes")
public class IBeanLazySerializer extends JsonSerializer<IBase> {

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.jackson.map.JsonSerializer#serialize(java.lang.Object,
     * org.codehaus.jackson.JsonGenerator,
     * org.codehaus.jackson.map.SerializerProvider)
     */
    @Override
    public void serialize(IBase value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeObject(HelperBase.getLazyId(value));
    }
}
