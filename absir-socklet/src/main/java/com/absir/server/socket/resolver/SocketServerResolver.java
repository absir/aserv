/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月4日 下午4:21:11
 */
package com.absir.server.socket.resolver;

import com.absir.bean.inject.value.Value;
import com.absir.client.SocketAdapter;
import com.absir.core.base.Environment;
import com.absir.core.util.UtilPipedStream;
import com.absir.server.in.InDispatcher;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.returned.ReturnedResolver;
import com.absir.server.route.returned.ReturnedResolverBody;
import com.absir.server.socket.*;
import com.absir.server.socket.InputSocket.InputSocketAtt;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.channels.SocketChannel;

public class SocketServerResolver extends InDispatcher<InputSocketAtt, SocketChannel> implements IServerResolver, IBufferResolver.IServerDispatch {

    @Value("server.socket.stream.max")
    private static int streamMax = Integer.MAX_VALUE;

    public int getStreamMax() {
        return streamMax;
    }

    @Override
    public long acceptTimeoutNIO(SocketChannel socketChannel) throws Throwable {
        return InputSocketContext.getAcceptTimeout();
    }

    @Override
    public void doBeat(SocketChannel socketChannel, SelSession selSession, long currentTime) {
    }

    @Override
    public void register(SocketChannel socketChannel, SelSession selSession, byte[] buffer, long currentTime) throws Throwable {
        selSession.getSocketBuffer().setId(new String(buffer));
    }

    @Override
    public void receiveBeatNIO(SocketChannel socketChannel, SelSession selSession, long currentTime) {
    }

    @Override
    public boolean receiveBufferNIO(final SocketChannel socketChannel, final SelSession selSession,
                                    final SocketBuffer socketBuffer, final byte[] buffer, final long currentTime) {
        final byte flag = buffer[0];
        if (InputSocketContext.ME.getBufferResolver().receiveStreamNIO(socketChannel, selSession, socketBuffer, flag, buffer, currentTime, getStreamMax(), this)) {
            return true;
        }

        return false;
    }

    @Override
    public void receiveByteBuffer(SocketChannel socketChannel, SelSession selSession, SocketBuffer socketBuffer,
                                  byte[] buffer, long currentTime) {
        if (buffer.length > 0) {
            byte flag = buffer[0];
            doDispatch(selSession, socketChannel, socketBuffer.getId(), buffer, flag, 1, socketBuffer, null, currentTime);
        }
    }

    @Override
    public void unRegister(Serializable id, SocketChannel socketChannel, SelSession selSession, long currentTime) {
    }

    protected InputSocketAtt createSocketAtt(SelSession selSession, Serializable id, byte[] buffer, byte flag, int off,
                                             SocketBuffer socketBuffer, InputStream inputStream) {
        return new InputSocketAtt(id, buffer, flag, off, selSession, inputStream);
    }

    @Override
    public void doDispatch(SelSession selSession, SocketChannel socketChannel, Serializable id, byte[] buffer, byte flag, int off,
                           SocketBuffer socketBuffer, InputStream inputStream, long currentTime) {
        if ((flag & SocketAdapter.RESPONSE_FLAG) == 0) {
            InputSocketAtt socketAtt = createSocketAtt(selSession, id, buffer, flag, off, socketBuffer, inputStream);
            try {
                if (socketAtt != null && on(socketAtt.getUrl(), socketAtt, socketChannel)) {
                    return;
                }

            } catch (Throwable e) {
                Environment.throwable(e);
            }

            UtilPipedStream.closeCloseable(inputStream);
            int callbackIndex = socketAtt.getCallbackIndex();
            if (callbackIndex != 0) {
                InputSocket.writeByteBufferSuccess(selSession, socketChannel, false, callbackIndex, InputSocket.NONE_RESPONSE_BYTES);
            }

        } else {
            doResponse(socketChannel, id, flag, off, buffer, inputStream);
        }
    }

    protected void doResponse(SocketChannel socketChannel, Serializable id, byte flag, int off, byte[] buffer, InputStream inputStream) {
    }

    @Override
    public InMethod getInMethod(InputSocketAtt req) {
        return req.getMethod();
    }

    @Override
    public String decodeUri(String uri, InputSocketAtt req) {
        return uri;
    }

    @Override
    protected Input input(String uri, InMethod inMethod, InModel model, InputSocketAtt req, SocketChannel res) {
        InputSocketImpl socketInput = new InputSocketImpl(model, req, res);
        return socketInput;
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
