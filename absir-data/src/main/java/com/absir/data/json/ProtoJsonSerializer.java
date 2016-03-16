/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月6日 下午4:10:58
 */
package com.absir.data.json;

import com.absir.data.value.IProto;
import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ProtoJsonSerializer extends JsonSerializer<IProto> {

    @Override
    public void serialize(IProto value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        Codec codec = ProtobufProxy.create(value.getClass());
        jgen.writeObject(codec.encode(value));
    }

}
