/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月13日 上午10:29:46
 */
package com.absir.server.route.body;

import com.absir.core.kernel.KernelList.Orderable;
import com.absir.server.on.OnPut;

import java.io.InputStream;
import java.io.OutputStream;

public interface IBodyConverter extends Orderable {

    public String[] getContentTypes();

    public Object readBodyParameterValue(OnPut onPut, int group, String input, Class<?> parameterType) throws Exception;

    public Object readBodyParameterValue(OnPut onPut, int group, InputStream inputStream, Class<?> parameterType) throws Exception;

    public byte[] writeAsBytes(OnPut onPut, Object returnValue) throws Exception;

    public void writeValue(OnPut onPut, Object returnValue, OutputStream outputStream) throws Exception;

}
