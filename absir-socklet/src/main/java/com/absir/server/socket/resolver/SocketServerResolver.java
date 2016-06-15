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
import com.absir.core.kernel.KernelByte;
import com.absir.core.util.UtilContext;
import com.absir.core.util.UtilPipedStream;
import com.absir.core.util.UtilPipedStream.NextOutputStream;
import com.absir.server.in.InDispatcher;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.returned.ReturnedResolver;
import com.absir.server.route.returned.ReturnedResolverBody;
import com.absir.server.socket.*;
import com.absir.server.socket.InputSocket.InputSocketAtt;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.Serializable;
import java.nio.channels.SocketChannel;

public class SocketServerResolver extends InDispatcher<InputSocketAtt, SocketChannel> implements IServerResolver {

    @Value("server.socket.stream.max")
    private static int streamMax = 4;

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
        byte flag = buffer[0];
        if ((flag & SocketAdapter.STREAM_FLAG) != 0 && (flag & SocketAdapter.POST_FLAG) == 0 && buffer.length > 5) {
            UtilPipedStream pipedStream = socketBuffer.getPipedStream();
            if (pipedStream.getSize() < getStreamMax()) {
                int hashIndex = KernelByte.getLength(buffer, 1);
                NextOutputStream outputStream = pipedStream.createNextOutputStream(hashIndex);
                final PipedInputStream inputStream = new PipedInputStream();
                try {
                    outputStream.connect(inputStream);

                } catch (IOException e) {
                }

                UtilContext.getThreadPoolExecutor().execute(new Runnable() {

                    @Override
                    public void run() {
                        doDispath(selSession, socketChannel, socketBuffer.getId(), buffer, socketBuffer, inputStream, currentTime);
                    }
                });
            }

            return true;

        } else if ((flag & SocketAdapter.STREAM_CLOSE_FLAG) != 0 && buffer.length == 5) {
            int hashIndex = KernelByte.getLength(buffer, 1);
            if ((flag & SocketAdapter.POST_FLAG) != 0) {
                socketBuffer.getActivePool().remove(hashIndex);

            } else {
                NextOutputStream outputStream = socketBuffer.getPipedStream().getOutputStream(hashIndex);
                if (outputStream != null) {
                    try {
                        outputStream.close();

                    } catch (IOException e) {
                        Environment.throwable(e);
                    }
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void receiveByteBuffer(SocketChannel socketChannel, SelSession selSession, SocketBuffer socketBuffer,
                                  byte[] buffer, long currentTime) {
        doDispath(selSession, socketChannel, socketBuffer.getId(), buffer, socketBuffer, null, currentTime);
    }

    @Override
    public void unRegister(Serializable id, SocketChannel socketChannel, SelSession selSession, long currentTime) {
    }

    protected void doDispath(SelSession selSession, SocketChannel socketChannel, Serializable id, byte[] buffer,
                             SocketBuffer socketBuffer, InputStream inputStream, long currentTime) {
        if (buffer.length > 0) {
            byte flag = buffer[0];
            if ((flag & SocketAdapter.STREAM_FLAG) != 0 && buffer.length > 5) {
                int hashIndex = KernelByte.getLength(buffer, 1);
                socketBuffer.getPipedStream().getOutputStream(hashIndex);
                NextOutputStream stream = socketBuffer.getPipedStream().getOutputStream(hashIndex);
                try {
                    if (stream != null) {
                        stream.write(buffer, 5, buffer.length);
                        return;
                    }

                } catch (Exception e) {
                    InputSocketImpl.writeByteBuffer(InputSocketContext.ME.getBufferResolver(), selSession,
                            socketChannel, (byte) (SocketAdapter.STREAM_FLAG | SocketAdapter.POST_FLAG), 0, buffer, 1,
                            4);
                }

            } else if ((flag & SocketAdapter.RESPONSE_FLAG) == 0) {
                InputSocketAtt inputSocketAtt = new InputSocketAtt(id, buffer, selSession, inputStream);
                try {
                    if (on(inputSocketAtt.getUrl(), inputSocketAtt, socketChannel)) {
                        return;
                    }

                } catch (Throwable e) {
                }

                UtilPipedStream.closeCloseable(inputStream);
                InputSocketImpl.writeByteBufferSuccess(selSession, socketChannel, false,
                        inputSocketAtt.getCallbackIndex(), InputSocket.NONE_RESPONSE_BYTES);

            } else {
                doResponse(socketChannel, id, flag, buffer);
            }
        }
    }

    protected void doResponse(SocketChannel socketChannel, Serializable id, byte flag, byte[] buffer) {
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
