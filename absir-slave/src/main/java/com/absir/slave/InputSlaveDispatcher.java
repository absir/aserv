/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月9日 下午7:48:10
 */
package com.absir.slave;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.client.SocketAdapter;
import com.absir.core.base.Environment;
import com.absir.server.in.InDispatcher;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.returned.ReturnedResolver;
import com.absir.server.route.returned.ReturnedResolverBody;
import com.absir.server.socket.InputSocket;
import com.absir.server.socket.resolver.BodyMsgResolver;
import com.absir.slave.InputSlave.InputSlaveAtt;
import com.absir.slave.resolver.ISlaveCallback;
import com.absir.slave.resolver.SlaveBufferResolver;

import java.nio.channels.SocketChannel;

@Base
@Bean
public class InputSlaveDispatcher extends InDispatcher<InputSlaveAtt, SocketChannel> implements ISlaveCallback {

    @Override
    public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
        if (buffer.length > 1) {
            InputSlaveAtt inputSocketAtt = new InputSlaveAtt(null, buffer, null, adapter);
            try {
                if (on(inputSocketAtt.getUrl(), inputSocketAtt, adapter.getSocket().getChannel())) {
                    return;
                }

            } catch (Throwable e) {
                Environment.throwable(e);
            }

            InputSocket.writeByteBuffer(SlaveBufferResolver.ME, null, adapter.getSocket().getChannel(),
                    (byte) (SocketAdapter.ERROR_FLAG | SocketAdapter.RESPONSE_FLAG), inputSocketAtt.getCallbackIndex(),
                    InputSocket.NONE_RESPONSE_BYTES);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public int getCallbackIndex() {
        return 1;
    }

    @Override
    public InMethod getInMethod(InputSlaveAtt req) {
        return req.getMethod();
    }

    @Override
    public String decodeUri(String uri, InputSlaveAtt req) {
        return uri;
    }

    @Override
    protected Input input(String uri, InMethod inMethod, InModel model, InputSlaveAtt req, SocketChannel res) {
        InputSlave input = new InputSlave(model, req, res);
        ReturnedResolverBody.ME.setBodyConverter(input, BodyMsgResolver.ME);
        return input;
    }

    @Override
    public void resolveReturnedValue(Object routeBean, OnPut onPut) throws Throwable {
        if (onPut.getReturnValue() == null) {
            ReturnedResolver<?> returnedResolver = onPut.getReturnedResolver();
            if (returnedResolver != null && returnedResolver instanceof ReturnedResolverBody) {
                onPut.setReturnValue(InputSocket.NONE_RESPONSE);
            }
        }

        super.resolveReturnedValue(routeBean, onPut);
    }
}
