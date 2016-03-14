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

/**
 * @author absir
 *
 */
public class SocketServerResolver extends InDispatcher<InputSocketAtt, SocketChannel> implements IServerResolver {

    /**
     * streamMax
     */
    @Value("server.socket.stream.max")
    private static int streamMax = 4;

    /**
     * @return the streamMax
     */
    public int getStreamMax() {
        return streamMax;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.socket.resolver.IServerResolver#acceptTimeoutNIO(java.
     * nio.channels.SocketChannel)
     */
    @Override
    public long acceptTimeoutNIO(SocketChannel socketChannel) throws Throwable {
        return InputSocketContext.getAcceptTimeout();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.socket.resolver.IServerResolver#doBeat(java.nio.channels
     * .SocketChannel, com.absir.server.socket.SelSession, long)
     */
    @Override
    public void doBeat(SocketChannel socketChannel, SelSession selSession, long contextTime) {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.server.socket.resolver.IServerResolver#register(java.nio.
     * channels.SocketChannel, com.absir.server.socket.SelSession, byte[])
     */
    @Override
    public void register(SocketChannel socketChannel, SelSession selSession, byte[] buffer) throws Throwable {
        selSession.getSocketBuffer().setId(new String(buffer));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.socket.resolver.IServerResolver#reciveBeatNIO(java.nio.
     * channels.SocketChannel, com.absir.server.socket.SelSession)
     */
    @Override
    public void reciveBeatNIO(SocketChannel socketChannel, SelSession selSession) {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.socket.resolver.IServerResolver#receiveBufferNIO(java.
     * nio.channels.SocketChannel, com.absir.server.socket.SelSession,
     * com.absir.server.socket.SocketBuffer, byte[])
     */
    @Override
    public boolean receiveBufferNIO(final SocketChannel socketChannel, final SelSession selSession,
                                    final SocketBuffer socketBuffer, final byte[] buffer) {
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
                        doDispath(selSession, socketChannel, socketBuffer.getId(), buffer, socketBuffer, inputStream);
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
                        if (Environment.getEnvironment() == Environment.DEVELOP) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.socket.resolver.IServerResolver#receiveByteBuffer(java.
     * nio.channels.SocketChannel, com.absir.server.socket.SelSession,
     * com.absir.server.socket.SocketBuffer, byte[])
     */
    @Override
    public void receiveByteBuffer(SocketChannel socketChannel, SelSession selSession, SocketBuffer socketBuffer,
                                  byte[] buffer) {
        doDispath(selSession, socketChannel, socketBuffer.getId(), buffer, socketBuffer, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.server.socket.resolver.IServerResolver#unRegister(java.io.
     * Serializable, java.nio.channels.SocketChannel,
     * com.absir.server.socket.SelSession)
     */
    @Override
    public void unRegister(Serializable id, SocketChannel socketChannel, SelSession selSession) {
    }

    /**
     * @param selSession
     * @param socketChannel
     * @param id
     * @param buffer
     * @param socketBuffer
     * @param inputStream
     */
    protected void doDispath(SelSession selSession, SocketChannel socketChannel, Serializable id, byte[] buffer,
                             SocketBuffer socketBuffer, InputStream inputStream) {
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

    /**
     * @param socketChannel
     * @param id
     * @param flag
     * @param buffer
     */
    protected void doResponse(SocketChannel socketChannel, Serializable id, byte flag, byte[] buffer) {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.server.in.IDispatcher#getInMethod(java.lang.Object)
     */
    @Override
    public InMethod getInMethod(InputSocketAtt req) {
        return req.getMethod();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.server.in.IDispatcher#decodeUri(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public String decodeUri(String uri, InputSocketAtt req) {
        return uri;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.server.in.InDispatcher#input(java.lang.String,
     * com.absir.server.in.InMethod, com.absir.server.in.InModel,
     * java.lang.Object, java.lang.Object)
     */
    @Override
    protected Input input(String uri, InMethod inMethod, InModel model, InputSocketAtt req, SocketChannel res) {
        InputSocketImpl socketInput = new InputSocketImpl(model, req, res);
        return socketInput;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.in.IDispatcher#resolveReturnedValue(java.lang.Object,
     * com.absir.server.on.OnPut)
     */
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
