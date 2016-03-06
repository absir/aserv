/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月4日 上午11:24:09
 */
package com.absir.client.callback;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import com.absir.data.helper.HelperDatabind;

/**
 * @author absir
 *
 */
public abstract class CallbackJson<T> extends CallbackMsg<T> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.client.callback.CallbackJson#read(byte[], int, int,
	 * java.lang.reflect.Type)
	 */
	@Override
	protected Object read(byte[] bytes, int off, int len, Type toType) throws IOException {
		return HelperDatabind.OBJECT_MAPPER.readValue(bytes, off, len,
				HelperDatabind.OBJECT_MAPPER.constructType(toType));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.client.callback.CallbackJson#read(java.io.InputStream,
	 * java.lang.reflect.Type)
	 */
	@Override
	protected Object read(InputStream inputStream, Type toType) throws IOException {
		return HelperDatabind.OBJECT_MAPPER.readValue(inputStream, HelperDatabind.OBJECT_MAPPER.constructType(toType));
	}

}
