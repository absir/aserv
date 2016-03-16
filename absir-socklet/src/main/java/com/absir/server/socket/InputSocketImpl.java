/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月4日 下午4:57:36
 */
package com.absir.server.socket;

import com.absir.client.SocketAdapter;
import com.absir.server.in.InModel;
import com.absir.server.socket.resolver.InputBufferResolver;
import com.absir.server.socket.resolver.SocketBufferResolver;

import java.nio.channels.SocketChannel;

public class InputSocketImpl extends InputSocket {

    public InputSocketImpl(InModel model, InputSocketAtt inputSocketAtt, SocketChannel socketChannel) {
        super(model, inputSocketAtt, socketChannel);
    }

    public static boolean writeByteBufferSuccess(SelSession selSession, SocketChannel socketChannel, boolean success,
                                                 int callbackIndex, byte[] bytes) {
        return writeByteBuffer(InputBufferResolver.ME, selSession, socketChannel,
                success == true ? 0 : SocketAdapter.ERROR_FLAG, callbackIndex, bytes);
    }

    @Override
    public SocketBufferResolver getSocketBufferResolver() {
        return InputBufferResolver.ME;
    }
}
