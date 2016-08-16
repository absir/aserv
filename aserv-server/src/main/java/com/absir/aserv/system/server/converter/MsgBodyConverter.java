package com.absir.aserv.system.server.converter;

import com.absir.context.core.ContextUtils;
import com.absir.data.helper.HelperDatabind;
import com.absir.server.on.OnPut;
import com.absir.server.route.body.IBodyConverter;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by absir on 16/3/22.
 */
public class MsgBodyConverter implements IBodyConverter {

    @Override
    public String[] getContentTypes() {
        return new String[]{"application/msg"};
    }

    @Override
    public Object readBodyParameterValue(OnPut onPut, int group, String input, Class<?> parameterType) throws Exception {
        return HelperDatabind.PACK.read(input.getBytes(ContextUtils.getCharset()), parameterType);
    }

    @Override
    public Object readBodyParameterValue(OnPut onPut, int group, InputStream inputStream, Class<?> parameterType) throws Exception {
        return HelperDatabind.PACK.read(inputStream, parameterType);
    }

    @Override
    public byte[] writeAsBytes(OnPut onPut, Object returnValue) throws Exception {
        return HelperDatabind.PACK.writeAsBytes(returnValue);
    }

    @Override
    public void writeValue(OnPut onPut, Object returnValue, OutputStream outputStream) throws Exception {
        HelperDatabind.PACK.write(outputStream, returnValue);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
