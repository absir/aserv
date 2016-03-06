/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年4月9日 上午10:33:24
 */
package com.absir.data.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import org.msgpack.jackson.dataformat.MessagePackFactory;

import com.absir.core.helper.HelperIO;
import com.absir.data.json.DataDeserializationContext;
import com.absir.data.json.DataDeserializationContext.JsonDeserializerResolver;
import com.absir.data.json.ProtoJsonDeserializer;
import com.absir.data.json.ProtoJsonSerializer;
import com.absir.data.value.IProto;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author absir
 *
 */
@SuppressWarnings("unchecked")
public class HelperDatabind {

	/** DESERIALIZATION_CONTEXT */
	public static final DataDeserializationContext DESERIALIZATION_CONTEXT = new DataDeserializationContext(
			BeanDeserializerFactory.instance);

	/** OBJECT_MAPPER */
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(null, null, DESERIALIZATION_CONTEXT);

	/** MESSAGE_PACK_FACTORY */
	public static final MessagePackFactory MESSAGE_PACK_FACTORY = new MessagePackFactory();

	static {
		// OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		OBJECT_MAPPER.setSerializationInclusion(Include.NON_DEFAULT);
		OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SimpleModule module = new SimpleModule();
		module.addSerializer(IProto.class, new ProtoJsonSerializer());
		OBJECT_MAPPER.registerModule(module);
		DESERIALIZATION_CONTEXT.addJsonDeserializerResolver(new JsonDeserializerResolver() {

			@Override
			public int getOrder() {
				// TODO Auto-generated method stub
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

	/**
	 * @param outputStream
	 * @param object
	 * @throws IOException
	 */
	public static void write(OutputStream outputStream, Object object) throws IOException {
		if (object == null) {
			return;
		}

		if (object.getClass() == byte[].class) {
			outputStream.write((byte[]) object);

		} else {
			JsonGenerator generator = MESSAGE_PACK_FACTORY.createGenerator(outputStream);
			generator.writeStartArray();
			OBJECT_MAPPER.writeValue(generator, object);
			generator.writeEndArray();
			generator.flush();
		}
	}

	/**
	 * @param outputStream
	 * @param objects
	 * @throws IOException
	 */
	public static void writeArray(OutputStream outputStream, Object... objects) throws IOException {
		JsonGenerator generator = MESSAGE_PACK_FACTORY.createGenerator(outputStream);
		generator.writeStartArray();
		for (Object object : objects) {
			OBJECT_MAPPER.writeValue(generator, object);
		}

		generator.writeEndArray();
		generator.flush();
	}

	/**
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public static byte[] writeAsBytes(Object object) throws IOException {
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

	/**
	 * @param objects
	 * @return
	 * @throws IOException
	 */
	public static byte[] writeAsBytesArray(Object... objects) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		writeArray(outputStream, objects);
		return outputStream.toByteArray();
	}

	/**
	 * @param parser
	 * @param toType
	 * @return
	 * @throws IOException
	 */
	public static Object read(JsonParser parser, Type toType) throws IOException {
		parser.nextToken();
		parser.nextToken();
		return OBJECT_MAPPER.readValue(parser, OBJECT_MAPPER.constructType(toType));
	}

	/**
	 * @param parser
	 * @param toTypes
	 * @return
	 * @throws IOException
	 */
	public static Object[] readArray(JsonParser parser, Type... toTypes) throws IOException {
		parser.nextToken();
		parser.nextToken();
		int length = toTypes.length;
		Object[] objects = new Object[length];
		for (int i = 0; i < length; i++) {
			objects[i] = OBJECT_MAPPER.readValue(parser, OBJECT_MAPPER.constructType(toTypes[i]));
		}

		return objects;
	}

	/**
	 * @param inputStream
	 * @param toClass
	 * @return
	 * @throws IOException
	 */
	public static <T> T read(InputStream inputStream, Class<T> toClass) throws IOException {
		if (toClass == byte[].class) {
			return (T) HelperIO.toByteArray(inputStream);
		}

		JsonParser parser = MESSAGE_PACK_FACTORY.createParser(inputStream);
		return (T) read(parser, toClass);
	}

	/**
	 * @param inputStream
	 * @param toType
	 * @return
	 * @throws IOException
	 */
	public static Object read(InputStream inputStream, Type toType) throws IOException {
		if (toType == byte[].class) {
			return HelperIO.toByteArray(inputStream);
		}

		return read(MESSAGE_PACK_FACTORY.createParser(inputStream), toType);
	}

	/**
	 * @param inputStream
	 * @param toTypes
	 * @return
	 * @throws IOException
	 */
	public static Object[] readArray(InputStream inputStream, Type... toTypes) throws IOException {
		return readArray(MESSAGE_PACK_FACTORY.createParser(inputStream), toTypes);
	}

	/**
	 * @param bytes
	 * @param toType
	 * @return
	 * @throws IOException
	 */
	public static Object read(byte[] bytes, Type toType) throws IOException {
		if (bytes == null) {
			return null;
		}

		return read(bytes, 0, bytes.length, toType);
	}

	/**
	 * @param bytes
	 * @param off
	 * @param len
	 * @param toType
	 * @return
	 * @throws IOException
	 */
	public static Object read(byte[] bytes, int off, int len, Type toType) throws IOException {
		if (toType == byte[].class) {
			return bytes;
		}

		return read(MESSAGE_PACK_FACTORY.createParser(bytes, off, len), toType);
	}

	/**
	 * @param bytes
	 * @param toTypes
	 * @return
	 * @throws IOException
	 */
	public static Object[] readArray(byte[] bytes, Type... toTypes) throws IOException {
		return readArray(bytes, 0, bytes.length, toTypes);
	}

	/**
	 * @param bytes
	 * @param off
	 * @param len
	 * @param toTypes
	 * @return
	 * @throws IOException
	 */
	public static Object[] readArray(byte[] bytes, int off, int len, Type... toTypes) throws IOException {
		if (len <= 0) {
			len = bytes.length;
		}

		return readArray(MESSAGE_PACK_FACTORY.createParser(bytes, off, len), toTypes);
	}
}
