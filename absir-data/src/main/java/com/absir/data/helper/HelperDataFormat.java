/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月9日 上午10:33:24
 */
package com.absir.data.helper;

import com.absir.core.base.Environment;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelLang;
import com.absir.data.format.DataFormat;
import com.absir.data.json.*;
import com.absir.data.json.DataDeserializationContext.JsonDeserializerResolver;
import com.absir.data.value.IProto;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.thrift.TBase;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Collection;

@SuppressWarnings("unchecked")
public class HelperDataFormat {

    public static final JsonFactory JSON_FACTORY = new JsonFactory();

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public static final JsonFormat JSON = new JsonFormat(JSON_MAPPER, JSON_FACTORY);

    public static final MessagePackFactory PACK_FACTORY = new MessagePackFactory();
    public static final DataDeserializationContext PACK_DESERIALIZATION_CONTEXT = new PackDeserializationContext(
            BeanDeserializerFactory.instance);
    public static final ObjectMapper PACK_MAPPER = new ObjectMapper(PACK_FACTORY, null, PACK_DESERIALIZATION_CONTEXT);
    public static final JsonFormat PACK = new JsonFormat(PACK_MAPPER, PACK_FACTORY);
    private static boolean tProto = true;
    private static boolean tBase = true;
    public static final JsonDeserializerResolver PACK_DESERIALIZER_RESOLVER = new JsonDeserializerResolver() {

        @Override
        public int getOrder() {
            return 0;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public JsonDeserializer<Object> forJavaType(Class<?> type) {
            if (tProto && IProto.class.isAssignableFrom(type)) {
                return new ProtoJsonDeserializer(type);
            }

            if (tBase && TBase.class.isAssignableFrom(type)) {
                return new ThriftBaseDeserializer(type);
            }

            return null;
        }
    };

    static {
        // JSON_MAPPER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        JSON_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // PACK_MAPPER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        PACK_MAPPER.setSerializationInclusion(Include.NON_DEFAULT);
        PACK_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        PACK_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        try {
            module.addSerializer(IProto.class, new ProtoJsonSerializer());

        } catch (Throwable e) {
            tProto = false;
            if (!(e instanceof NoClassDefFoundError)) {
                Environment.throwable(e);
            }
        }
        try {
            module.addSerializer(TBase.class, new ThriftBaseSerializer());

        } catch (Throwable e) {
            tBase = false;
            if (!(e instanceof NoClassDefFoundError)) {
                Environment.throwable(e);
            }
        }

        PACK_MAPPER.registerModule(module);
        PACK_DESERIALIZATION_CONTEXT.addJsonDeserializerResolver(PACK_DESERIALIZER_RESOLVER);
    }

    protected static class PackDeserializationContext extends DataDeserializationContext {

        public PackDeserializationContext(DeserializerFactory df) {
            super(df);
        }

        protected PackDeserializationContext(DataDeserializationContext src, DeserializationConfig config, JsonParser jp, InjectableValues values) {
            super(src, config, jp, values);
        }

        @Override
        public DefaultDeserializationContext createInstance(DeserializationConfig config, JsonParser jp, InjectableValues values) {
            return new PackDeserializationContext(this, config, jp, values);
        }

        @Override
        public JsonDeserializer<?> handleSecondaryContextualization(JsonDeserializer<?> deser, BeanProperty prop, JavaType type) throws JsonMappingException {
            JsonDeserializer<?> deserializer = PACK_DESERIALIZER_RESOLVER.forJavaType(type.getRawClass());
            return deserializer == null ? super.handleSecondaryContextualization(deser, prop, type) : deserializer;
        }
    }

    public static class JsonFormat extends DataFormat {

        private ObjectMapper mapper;

        private JsonFactory factory;

        public JsonFormat(ObjectMapper mapper, JsonFactory factory) {
            this.mapper = mapper;
            this.factory = factory;
        }

        public ObjectMapper getMapper() {
            return mapper;
        }

        public JsonFactory getFactory() {
            return factory;
        }

        public Object read(JsonParser parser, Type toType) throws IOException {
            parser.nextToken();
            parser.nextToken();
            return mapper.readValue(parser, mapper.constructType(toType));
        }

        public Object[] readArray(JsonParser parser, InputStream inputStream, Type... toTypes) throws IOException {
            parser.nextToken();
            parser.nextToken();
            int length = toTypes.length;
            Object[] objects = new Object[length];
            Type type;
            for (int i = 0; i < length; i++) {
                type = toTypes[i];
                if (type == InputStream.class) {
                    objects[i] = inputStream;

                } else {
                    objects[i] = type == null ? mapper.reader().readValue(parser) : mapper.readValue(parser, mapper.constructType(type));
                }
            }

            return objects;
        }

        @Override
        protected void formatWrite(OutputStream outputStream, Object object) throws IOException {
            JsonGenerator generator = factory.createGenerator(outputStream);
            generator.writeStartArray();
            mapper.writeValue(generator, object);
            generator.writeEndArray();
            generator.flush();
        }

        @Override
        protected void formatWriteArray(OutputStream outputStream, Class<?>[] types, Object... objects) throws IOException {
            JsonGenerator generator = factory.createGenerator(outputStream);
            generator.writeStartArray();
            int i = 0;
            InputStream inputStream = null;
            for (Object object : objects) {
                if (types == null || types[i++] != InputStream.class) {
                    mapper.writeValue(generator, object);

                } else {
                    inputStream = (InputStream) object;
                }
            }

            generator.writeEndArray();
            generator.flush();

            if (inputStream != null) {
                HelperIO.copy(inputStream, outputStream);
            }
        }

        @Override
        protected Object formatRead(InputStream inputStream, Type toType) throws IOException {
            return read(factory.createParser(inputStream), toType);
        }

        @Override
        protected Object[] formatReadArray(InputStream inputStream, Type... toTypes) throws IOException {
            return readArray(factory.createParser(inputStream), inputStream, toTypes);
        }

        @Override
        protected Object formatRead(byte[] bytes, int off, int len, Type toType) throws IOException {
            return read(factory.createParser(bytes, off, len), toType);
        }

        @Override
        protected Object[] formatReadArray(byte[] bytes, int off, int len, Type... toTypes) throws IOException {
            return readArray(factory.createParser(bytes, off, len), null, toTypes);
        }

        public <T> void writeGetTemplates(OutputStream outputStream, Collection<T> templates, KernelLang.GetTemplate<T, Object> getTemplate) throws IOException {
            JsonGenerator generator = factory.createGenerator(outputStream);
            generator.writeStartArray();
            for (T template : templates) {
                Object o = getTemplate.getWith(template);
                if (o != null) {
                    mapper.writeValue(generator, o);
                }
            }

            generator.writeEndArray();
        }

    }

}
