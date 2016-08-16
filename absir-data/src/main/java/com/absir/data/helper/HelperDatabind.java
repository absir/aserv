/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月9日 上午10:33:24
 */
package com.absir.data.helper;

import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelCharset;
import com.absir.core.kernel.KernelLang;
import com.absir.data.json.DataDeserializationContext;
import com.absir.data.json.DataDeserializationContext.JsonDeserializerResolver;
import com.absir.data.json.ProtoJsonDeserializer;
import com.absir.data.json.ProtoJsonSerializer;
import com.absir.data.value.IProto;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

@SuppressWarnings("unchecked")
public class HelperDatabind {

    public static final DataDeserializationContext DESERIALIZATION_CONTEXT = new DataDeserializationContext(
            BeanDeserializerFactory.instance);

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public static final JsonFactory JSON_FACTORY = new JsonFactory();

    public static final DataBind JSON = new DataBind(JSON_MAPPER, JSON_FACTORY);

    public static final ObjectMapper PACK_MAPPER = new ObjectMapper(null, null, DESERIALIZATION_CONTEXT);

    public static final MessagePackFactory PACK_FACTORY = new MessagePackFactory();

    public static final DataBind PACK = new DataBind(PACK_MAPPER, PACK_FACTORY);

    static {
        // JSON_MAPPER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        JSON_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // PACK_MAPPER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        PACK_MAPPER.setSerializationInclusion(Include.NON_DEFAULT);
        PACK_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        PACK_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addSerializer(IProto.class, new ProtoJsonSerializer());
        PACK_MAPPER.registerModule(module);
        DESERIALIZATION_CONTEXT.addJsonDeserializerResolver(new JsonDeserializerResolver() {

            @Override
            public int getOrder() {
                return 0;
            }

            @SuppressWarnings("rawtypes")
            @Override
            public JsonDeserializer<Object> forJavaType(Class<?> type) {
                if (IProto.class.isAssignableFrom(type)) {
                    return new ProtoJsonDeserializer(type);
                }

                return null;
            }
        });
    }

    public static class DataBind {

        private ObjectMapper objectMapper;

        private JsonFactory jsonFactory;

        public DataBind(ObjectMapper mapper, JsonFactory factory) {
            objectMapper = mapper;
            jsonFactory = factory;
        }

        public ObjectMapper getObjectMapper() {
            return objectMapper;
        }

        public JsonFactory getJsonFactory() {
            return jsonFactory;
        }

        public void write(OutputStream outputStream, Object object) throws IOException {
            if (object == null) {
                return;
            }

            if (object.getClass() == byte[].class) {
                outputStream.write((byte[]) object);

            } else {
                JsonGenerator generator = jsonFactory.createGenerator(outputStream);
                generator.writeStartArray();
                objectMapper.writeValue(generator, object);
                generator.writeEndArray();
                generator.flush();
            }
        }

        public void writeArray(OutputStream outputStream, Object... objects) throws IOException {
            JsonGenerator generator = jsonFactory.createGenerator(outputStream);
            generator.writeStartArray();
            for (Object object : objects) {
                objectMapper.writeValue(generator, object);
            }

            generator.writeEndArray();
            generator.flush();
        }

        public byte[] writeAsBytes(Object object) throws IOException {
            if (object == null) {
                return null;
            }

            if (object.getClass() == byte[].class) {
                return (byte[]) object;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            write(outputStream, object);
            return outputStream.toByteArray();
        }

        public byte[] writeAsBytesArray(Object... objects) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            writeArray(outputStream, objects);
            return outputStream.toByteArray();
        }

        public String writeAsStringArray(Object... objects) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            writeArray(outputStream, objects);
            return outputStream.toString(KernelCharset.UTF8.name());
        }

        public Object read(JsonParser parser, Type toType) throws IOException {
            parser.nextToken();
            parser.nextToken();
            return objectMapper.readValue(parser, objectMapper.constructType(toType));
        }

        public Object[] readArray(JsonParser parser, Type... toTypes) throws IOException {
            parser.nextToken();
            parser.nextToken();
            int length = toTypes.length;
            Object[] objects = new Object[length];
            Type type;
            for (int i = 0; i < length; i++) {
                type = toTypes[i];
                objects[i] = type == null ? objectMapper.reader().readValue(parser) : objectMapper.readValue(parser, objectMapper.constructType(type));
            }

            return objects;
        }

        public <T> T read(InputStream inputStream, Class<T> toClass) throws IOException {
            if (toClass == byte[].class) {
                return (T) HelperIO.toByteArray(inputStream);
            }

            JsonParser parser = jsonFactory.createParser(inputStream);
            return (T) read(parser, toClass);
        }

        public Object read(InputStream inputStream, Type toType) throws IOException {
            if (toType == byte[].class) {
                return HelperIO.toByteArray(inputStream);
            }

            return read(jsonFactory.createParser(inputStream), toType);
        }

        public Object[] readArray(InputStream inputStream, Type... toTypes) throws IOException {
            if (toTypes.length == 0) {
                return KernelLang.NULL_OBJECTS;
            }

            return readArray(jsonFactory.createParser(inputStream), toTypes);
        }

        public Object read(byte[] bytes, Type toType) throws IOException {
            if (bytes == null) {
                return null;
            }

            return read(bytes, 0, bytes.length, toType);
        }

        public Object read(byte[] bytes, int off, int len, Type toType) throws IOException {
            if (toType == byte[].class) {
                return bytes;
            }

            return read(jsonFactory.createParser(bytes, off, len), toType);
        }

        public Object[] readArray(byte[] bytes, Type... toTypes) throws IOException {
            return readArray(bytes, 0, bytes.length, toTypes);
        }

        public Object[] readArray(byte[] bytes, int off, int len, Type... toTypes) throws IOException {
            if (toTypes.length == 0) {
                return KernelLang.NULL_OBJECTS;
            }

            if (len <= 0) {
                len = bytes.length;
            }

            return readArray(jsonFactory.createParser(bytes, off, len), toTypes);
        }

        public Object[] readArray(String string, Type... toTypes) throws IOException {
            if (toTypes.length == 0) {
                return KernelLang.NULL_OBJECTS;
            }

            return readArray(string.getBytes(KernelCharset.UTF8), toTypes);
        }
    }

}
