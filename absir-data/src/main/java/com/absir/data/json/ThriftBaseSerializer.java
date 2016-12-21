package com.absir.data.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TMemoryInputTransport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by absir on 2016/12/13.
 */
public class ThriftBaseSerializer extends JsonSerializer<TBase> {

    public static byte[] serializerBytes(TBase tBase) throws TException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tBase.write(new TCompactProtocol(new TIOStreamTransport(outputStream)));
        return outputStream.toByteArray();
    }

    public static <T extends TBase> T deserializeBytes(byte[] bytes, Class<T> sClass) throws IllegalAccessException, InstantiationException, TException {
        T bean = sClass.newInstance();
        bean.read(new TCompactProtocol(new TMemoryInputTransport(bytes)));
        return bean;
    }

    @Override
    public void serialize(TBase tBase, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        try {
            byte[] bytes = serializerBytes(tBase);
            jsonGenerator.writeObject(bytes);

        } catch (TException e) {
            throw new IOException(e);
        }
    }
}

