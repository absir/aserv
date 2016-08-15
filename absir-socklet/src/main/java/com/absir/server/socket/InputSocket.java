/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-2-17 下午4:06:43
 */
package com.absir.server.socket;

import com.absir.bean.basis.Configure;
import com.absir.client.SocketAdapter;
import com.absir.client.SocketAdapterSel;
import com.absir.client.SocketNIO;
import com.absir.context.core.ContextUtils;
import com.absir.core.base.Environment;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelByte;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelLang.ObjectTemplate;
import com.absir.core.util.UtilActivePool;
import com.absir.core.util.UtilPipedStream;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.socket.resolver.SocketBufferResolver;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

@Configure
public abstract class InputSocket extends Input {

    public static final String NONE_RESPONSE = "";

    public static final byte[] NONE_RESPONSE_BYTES = NONE_RESPONSE.getBytes();

    protected SelSession selSession;

    private SocketChannel socketChannel;

    private String uri;

    private int status = ServerStatus.ON_SUCCESS.getCode();

    private byte flag;

    private int callbackIndex;

    private byte[] inputBuffer;

    private int inputPos;

    private int inputCount;

    private InputStream inputStream;

    private String input;

    private OutputStream outputStream;

    public InputSocket(InModel model, InputSocketAtt inputSocketAtt, SocketChannel socketChannel) {
        super(model);
        this.selSession = inputSocketAtt.selSession;
        this.socketChannel = socketChannel;
        setId(inputSocketAtt.getId());
        uri = inputSocketAtt.getUrl();
        flag = inputSocketAtt.getFlag();
        callbackIndex = inputSocketAtt.getCallbackIndex();
        inputBuffer = inputSocketAtt.getBuffer();
        if (inputBuffer != null) {
            inputCount = inputSocketAtt.getPostDataLength();
            inputPos = inputBuffer.length - inputCount;
        }

        inputStream = inputSocketAtt.inputStream;
    }

    public static boolean writeBuffer(SocketChannel socketChannel, byte[] buffer) {
        return writeBuffer(socketChannel, buffer, 0, buffer.length);
    }

    public static boolean writeBuffer(SocketChannel socketChannel, byte[] buffer, int offset, int length) {
        try {
            SocketNIO.writeTimeout(socketChannel, ByteBuffer.wrap(buffer, offset, length));
            return true;

        } catch (IOException e) {
            SocketServer.close(socketChannel);
        }

        return false;
    }

    public static boolean writeByteBuffer(SocketBufferResolver bufferResolver, SelSession selSession,
                                          SocketChannel socketChannel, int callbackIndex, byte[] bytes) {
        return writeByteBuffer(bufferResolver, selSession, socketChannel, (byte) 0, callbackIndex, bytes, 0,
                bytes.length);
    }

    public static boolean writeByteBuffer(SocketBufferResolver bufferResolver, SelSession selSession,
                                          SocketChannel socketChannel, byte flag, int callbackIndex, byte[] bytes) {
        return writeByteBuffer(bufferResolver, selSession, socketChannel, flag, callbackIndex, bytes, 0, bytes.length);
    }

    public static boolean writeByteBuffer(SocketBufferResolver bufferResolver, SelSession selSession,
                                          SocketChannel socketChannel, byte flag, int callbackIndex, byte[] bytes, int offset, int length) {
        int headerLength = callbackIndex == 0 ? flag == 0 ? 0 : 1 : 5;
        byte[] headerBytes = bufferResolver.createByteHeader(headerLength);
        if (headerBytes == null) {
            return false;
        }

        int headerOffset = headerBytes.length - headerLength;
        if (callbackIndex != 0) {
            flag |= SocketAdapter.CALLBACK_FLAG;
            KernelByte.setLength(headerBytes, headerOffset + 1, callbackIndex);
        }

        if (headerLength > 0) {
            headerBytes[headerOffset] = flag;
        }

        ByteBuffer byteBuffer = bufferResolver.createByteBuffer(socketChannel, headerLength, headerBytes, bytes, offset,
                length);
        synchronized (socketChannel) {
            try {
                SocketNIO.writeTimeout(socketChannel, ByteBuffer.wrap(headerBytes));
                SocketNIO.writeTimeout(socketChannel, byteBuffer);
                return true;

            } catch (Throwable e) {
                SocketServer.close(selSession, socketChannel);
            }
        }

        return false;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public Object getMeta(String name) {
        return selSession == null ? null : selSession.getMeta(name);
    }

    public void setMeta(String name, Object value) {
        if (selSession != null) {
            selSession.setMeta(name, value);
        }
    }

    @Override
    public Object getAttribute(String name) {
        return getModel().get(name);
    }

    @Override
    public void setAttribute(String name, Object obj) {
        getModel().put(name, obj);
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public Map<String, Object> getParamMap() {
        return getModel();
    }

    @Override
    public String getParam(String name) {
        return KernelDyna.to(getModel().get(name), String.class);
    }

    @Override
    public InMethod getMethod() {
        return inputBuffer == null && inputStream == null ? InMethod.GET : InMethod.POST;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean paramDebug() {
        return (flag & SocketAdapter.DEBUG_FLAG) != 0;
    }

    @Override
    public String getRemoteAddr() {
        return socketChannel.socket().getLocalAddress().getHostAddress();
    }

    @Override
    public String[] getParams(String name) {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputBuffer == null ? null : new ByteArrayInputStream(inputBuffer, inputPos, inputCount);
    }

    @Override
    public String getInput() {
        if (input == null) {
            if (inputBuffer != null) {
                input = new String(inputBuffer, inputPos, inputCount, ContextUtils.getCharset());

            } else if (inputStream != null) {
                try {
                    input = HelperIO.toString(inputStream, ContextUtils.getCharset());

                } catch (IOException e) {
                    input = KernelLang.NULL_STRING;
                }

            } else {
                input = KernelLang.NULL_STRING;
            }
        }

        return input;
    }

    @Override
    public void setContentTypeCharset(String contentTypeCharset) {
    }

    @Override
    public void setCharacterEncoding(String charset) {
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    public void readyOutputStream() {
        if (outputStream == null && selSession != null) {
            boolean sended = false;
            final UtilActivePool activePool = selSession.getSocketBuffer().getActivePool();
            final ObjectTemplate<Integer> nextIndex = activePool.addObject();
            final PipedInputStream inputStream = new PipedInputStream();
            try {
                outputStream = new PipedOutputStream(inputStream);
                final int streamIndex = nextIndex.object;
                writeByteBuffer(getSocketBufferResolver(), selSession, socketChannel,
                        (byte) (writeFlag(flag) | SocketAdapter.STREAM_FLAG), callbackIndex,
                        KernelByte.getLengthBytes(streamIndex));
                ContextUtils.getThreadPoolExecutor().execute(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            int postBuffLen = SocketAdapterSel.POST_BUFF_LEN;
                            byte[] sendBufer = SocketBufferResolver.createByteBufferFull(getSocketBufferResolver(),
                                    socketChannel, 4 + postBuffLen, null, 0, 0);
                            sendBufer[4] = SocketAdapter.STREAM_FLAG | SocketAdapter.POST_FLAG;
                            KernelByte.setLength(sendBufer, 5, streamIndex);
                            int len;
                            try {
                                while ((len = inputStream.read(sendBufer, 9, sendBufer.length)) > 0) {
                                    len += 5;
                                    KernelByte.setLength(sendBufer, 0, len);
                                    if (nextIndex.object == null
                                            || !InputSocket.writeBuffer(socketChannel, sendBufer, 0, len)) {
                                        return;
                                    }
                                }

                            } catch (Exception e) {
                                Environment.throwable(e);
                                return;
                            }

                        } finally {
                            activePool.remove(streamIndex);
                            UtilPipedStream.closeCloseable(inputStream);
                        }
                    }
                });

                sended = true;

            } catch (Exception e) {
                Environment.throwable(e);

            } finally {
                if (!sended) {
                    activePool.remove(nextIndex.object);
                    UtilPipedStream.closeCloseable(inputStream);
                }
            }
        }
    }

    public abstract SocketBufferResolver getSocketBufferResolver();

    protected byte writeFlag(byte flag) {
        if (status != ServerStatus.ON_SUCCESS.getCode()) {
            flag |= SocketAdapter.ERROR_FLAG;
        }

        return flag;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        writeByteBuffer(getSocketBufferResolver(), selSession, socketChannel, writeFlag(flag), callbackIndex, b, off,
                len);
    }

    public static class InputSocketAtt {

        protected Serializable id;

        protected byte[] buffer;

        protected byte flag;

        protected SelSession selSession;

        protected int callbackIndex;

        protected String url;

        protected int postDataLength;

        protected InputStream inputStream;

        /**
         * 空初始化
         */
        protected InputSocketAtt() {
        }

        public InputSocketAtt(Serializable id, byte[] buffer, SelSession selSession) {
            this(id, buffer, 0, selSession, null);
        }

        public InputSocketAtt(Serializable id, byte[] buffer, SelSession selSession, InputStream inputStream) {
            this(id, buffer, 0, selSession, inputStream);
        }

        public InputSocketAtt(Serializable id, byte[] buffer, int off, SelSession selSession, InputStream inputStream) {
            this.id = id;
            this.buffer = buffer;
            this.flag = buffer[off];
            this.selSession = selSession;
            int headerLength = off + 1;
            if ((flag & SocketAdapter.STREAM_FLAG) != 0) {
                headerLength += 4;
            }

            if ((flag & SocketAdapter.CALLBACK_FLAG) != 0) {
                callbackIndex = KernelByte.getLength(buffer, headerLength);
                headerLength += 4;
            }

            if ((flag & SocketAdapter.POST_FLAG) != 0) {
                postDataLength = KernelByte.getLength(buffer, headerLength);
                headerLength += 4;
            }

            url = new String(buffer, headerLength, buffer.length - headerLength - postDataLength,
                    ContextUtils.getCharset());
            this.inputStream = inputStream;
        }

        public Serializable getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }

        public byte getFlag() {
            return flag;
        }

        public SelSession getSelSession() {
            return selSession;
        }

        public InMethod getMethod() {
            return (flag & (SocketAdapter.STREAM_FLAG | SocketAdapter.POST_FLAG)) == 0 ? InMethod.GET : InMethod.POST;
        }

        public int getCallbackIndex() {
            return callbackIndex;
        }

        public byte[] getBuffer() {
            return buffer;
        }

        public int getPostDataLength() {
            return postDataLength;
        }
    }
}
