/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月6日 下午4:11:30
 */
package com.absir.data.json;

import com.absir.data._abp_jprotobuf.ProtobufProxyBasic;
import com.absir.data.value.IProto;
import com.baidu.bjf.remoting.protobuf.Codec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class ProtoJsonDeserializer<T extends IProto> extends JsonDeserializer<T> {

    private Class<T> sClass;

    public ProtoJsonDeserializer(Class<T> sClass) {
        this.sClass = sClass;
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Object ob = jp.getEmbeddedObject();
        if (ob == null)
            return null;
        Codec<T> codec = ProtobufProxyBasic.create(sClass);
        return codec.decode((byte[]) ob);
    }

}
