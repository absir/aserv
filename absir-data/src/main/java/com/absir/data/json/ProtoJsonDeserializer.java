/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月6日 下午4:11:30
 */
package com.absir.data.json;

import java.io.IOException;

import com.absir.data.value.IProto;
import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * @author absir
 *
 */
public class ProtoJsonDeserializer<T extends IProto> extends JsonDeserializer<T> {

	/** sClass */
	private Class<T> sClass;

	/**
	 * @param sClass
	 */
	public ProtoJsonDeserializer(Class<T> sClass) {
		this.sClass = sClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml
	 * .jackson.core.JsonParser,
	 * com.fasterxml.jackson.databind.DeserializationContext)
	 */
	@Override
	public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		Object ob = jp.getEmbeddedObject();
		if (ob == null)
			return null;
		Codec<T> codec = ProtobufProxy.create(sClass);
		return codec.decode((byte[]) ob);
	}

}
