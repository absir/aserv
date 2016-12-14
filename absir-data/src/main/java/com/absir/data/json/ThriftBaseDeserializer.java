package com.absir.data.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.io.IOExceptionWithCause;
import org.apache.thrift.TBase;

import java.io.IOException;

/**
 * Created by absir on 2016/12/13.
 */
public class ThriftBaseDeserializer<T extends TBase> extends JsonDeserializer<T> {

    private Class<T> sClass;

    public ThriftBaseDeserializer(Class<T> sClass) {
        this.sClass = sClass;
    }

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Object ob = jsonParser.getEmbeddedObject();
        if (ob == null)
            return null;

        try {
            return ThriftBaseSerializer.deserializeBytes((byte[]) ob, sClass);

        } catch (Exception e) {
            throw new IOExceptionWithCause(e);
        }
    }

}
