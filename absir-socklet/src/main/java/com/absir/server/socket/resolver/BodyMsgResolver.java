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

/**
 * @author absir
 *
 */
@Base
@Bean
public class BodyMsgResolver implements IBodyConverter {

    /**
     * ME
     */
    public static final BodyMsgResolver ME = BeanFactoryUtils.get(BodyMsgResolver.class);

    /*
     * (non-Javadoc)
     *
     * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.server.route.body.IBodyConverter#getContentTypes()
     */
    @Override
    public String[] getContentTypes() {
        return new String[]{"messagePack"};
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.system.server.IServerBodyConverter#
     * readBodyParameterValue (com.absir.server.on.OnPut, int, java.lang.String,
     * java.lang.Class)
     */
    @Override
    public Object readBodyParameterValue(OnPut onPut, int group, String input, Class<?> parameterType)
            throws Exception {
        byte[] buffer = input.getBytes();
        return HelperDatabind.read(buffer, 0, buffer.length, parameterType);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.system.server.IServerBodyConverter#
     * readBodyParameterValue (com.absir.server.on.OnPut, int,
     * java.io.InputStream, java.lang.Class)
     */
    @Override
    public Object readBodyParameterValue(OnPut onPut, int group, InputStream inputStream, Class<?> parameterType)
            throws Exception {
        return HelperDatabind.read(inputStream, parameterType);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.server.IServerBodyConverter#writeAsBytes(com
     * .absir.server.on.OnPut, java.lang.Object)
     */
    @Override
    public byte[] writeAsBytes(OnPut onPut, Object returnValue) throws Exception {
        return HelperDatabind.writeAsBytes(returnValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.server.IServerBodyConverter#writeValue(com.absir
     * .server.on.OnPut, java.lang.Object, java.io.OutputStream)
     */
    @Override
    public void writeValue(OnPut onPut, Object returnValue, OutputStream outputStream) throws Exception {
        HelperDatabind.write(outputStream, returnValue);
    }
}
