/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月13日 上午10:18:05
 */
package com.absir.server.socket.resolver;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.data.helper.HelperDatabind;
import com.absir.server.on.OnPut;
import com.absir.server.route.body.IBodyConverter;

import java.io.InputStream;
import java.io.OutputStream;

@Base
@Bean
public class BodyMsgResolver implements IBodyConverter {

    public static final BodyMsgResolver ME = BeanFactoryUtils.get(BodyMsgResolver.class);

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String[] getContentTypes() {
        return new String[]{"messagePack"};
    }

    @Override
    public Object readBodyParameterValue(OnPut onPut, int group, String input, Class<?> parameterType)
            throws Exception {
        byte[] buffer = input.getBytes();
        return HelperDatabind.read(buffer, 0, buffer.length, parameterType);
    }

    @Override
    public Object readBodyParameterValue(OnPut onPut, int group, InputStream inputStream, Class<?> parameterType)
            throws Exception {
        return HelperDatabind.read(inputStream, parameterType);
    }

    @Override
    public byte[] writeAsBytes(OnPut onPut, Object returnValue) throws Exception {
        return HelperDatabind.writeAsBytes(returnValue);
    }

    @Override
    public void writeValue(OnPut onPut, Object returnValue, OutputStream outputStream) throws Exception {
        HelperDatabind.write(outputStream, returnValue);
    }
}
