package com.absir.thrift;

import com.absir.core.util.UtilContext;
import com.absir.server.socket.InputSocket;
import com.absir.server.socket.SelSession;
import com.absir.server.socket.SocketBuffer;
import com.absir.server.socket.resolver.ISessionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * Created by absir on 2016/12/20.
 */
public class ThriftServer implements ISessionResolver {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ThriftServer.class);

    @Override
    public long acceptTimeout(SocketChannel socketChannel) throws Throwable {
        return 0;
    }

    @Override
    public void idle(SocketChannel socketChannel, SelSession selSession, long contextTime) {
    }

    @Override
    public void register(SocketChannel socketChannel, SelSession selSession) throws Throwable {
        String uuid = UUID.randomUUID().toString();
        selSession.getSocketBuffer().setId(uuid);
        InputSocket.writeByteBuffer(selSession, socketChannel, 0, uuid.getBytes());
    }

    @Override
    public void receiveByteBuffer(final SocketChannel socketChannel, final SelSession selSession) throws Throwable {
        final SocketBuffer socketBuffer = selSession.getSocketBuffer();
        final Serializable id = socketBuffer.getId();
        final byte[] buffer = socketBuffer.getBuff();
        socketBuffer.setBuff(null);
        if (socketBuffer.addBufferQueue(buffer)) {
            return;
        }

        final long currentTime = System.currentTimeMillis();
        UtilContext.getThreadPoolExecutor().execute(new Runnable() {

            @Override
            public void run() {
                byte[] queueBuffer = buffer;
                while (queueBuffer != null) {
                    try {
                        receiveByteBuffer(socketChannel, selSession, socketBuffer, queueBuffer, currentTime);

                    } catch (Throwable e) {
                        LOGGER.error("receiveByteBuffer", e);
                    }

                    if (!socketChannel.isConnected()) {
                        break;
                    }

                    queueBuffer = socketBuffer.readBufferQueue();
                }
            }
        });
    }

    @Override
    public void unRegister(Serializable id, SocketChannel socketChannel, SelSession selSession) throws Throwable {
    }

    public void receiveByteBuffer(SocketChannel socketChannel, SelSession selSession, SocketBuffer socketBuffer,
                                  byte[] buffer, long currentTime) {
        if (buffer.length > 0) {
            byte flag = buffer[0];
            doDispatch(selSession, socketChannel, socketBuffer.getId(), buffer, flag, 1, socketBuffer, null, currentTime);
        }
    }

    protected InputSocket.InputSocketAtt createSocketAtt(SelSession selSession, Serializable id, byte[] buffer, byte flag, int off,
                                                         SocketBuffer socketBuffer, InputStream inputStream) {
        return new InputSocket.InputSocketAtt(id, buffer, flag, off, selSession, inputStream);
    }

    public void doDispatch(SelSession selSession, SocketChannel socketChannel, Serializable id, byte[] buffer, byte flag, int off,
                           SocketBuffer socketBuffer, InputStream inputStream, long currentTime) {
//        if ((flag & SocketAdapter.RESPONSE_FLAG) == 0) {
//            InputSocket.InputSocketAtt socketAtt = createSocketAtt(selSession, id, buffer, flag, off, socketBuffer, inputStream);
//            try {
//                if (socketAtt != null && on(socketAtt.getUrl(), socketAtt, socketChannel)) {
//                    return;
//                }
//
//            } catch (Throwable e) {
//                Environment.throwable(e);
//            }
//
//            UtilPipedStream.closeCloseable(inputStream);
//            int callbackIndex = socketAtt.getCallbackIndex();
//            if (callbackIndex != 0) {
//                InputSocket.writeByteBufferSuccess(selSession, socketChannel, false, callbackIndex, InputSocket.NONE_RESPONSE_BYTES);
//            }
//
//        } else {
//            doResponse(socketChannel, id, flag, off, buffer, inputStream);
//        }
    }

    protected void doResponse(SocketChannel socketChannel, Serializable id, byte flag, int off, byte[] buffer, InputStream inputStream) {
    }
}
