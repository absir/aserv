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
import com.absir.context.core.ContextUtils;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelByte;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelLang;
import com.absir.core.util.UtilContext;
import com.absir.core.util.UtilPipedStream;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.route.RouteAdapter;
import com.absir.server.socket.resolver.SocketBufferResolver;

import java.io.*;
import java.nio.channels.SocketChannel;
import java.util.Map;

@Configure
public abstract class InputSocket extends Input {

    public static final String NONE_RESPONSE = "";

    public static final byte[] NONE_RESPONSE_BYTES = NONE_RESPONSE.getBytes();

    protected static byte INPUT_FLAG = SocketAdapter.HUMAN_FLAG;

    protected InputSocketAtt socketAtt;
    private int status = ServerStatus.ON_SUCCESS.getCode();
    private byte flag;
    private int urlVarints;
    private InputStream inputStream;
    private SocketChannel socketChannel;
    private String input;
    private OutputStream outputStream;

    public InputSocket(InModel model, InputSocketAtt socketAtt, SocketChannel socketChannel) {
        super(model);
        this.socketAtt = socketAtt;
        setId(socketAtt.getId());
        flag = (byte) (socketAtt.getFlag() & INPUT_FLAG);
        urlVarints = socketAtt.urlVarints;
        this.inputStream = socketAtt.inputStream;
        this.socketChannel = socketChannel == null ? socketAtt.getSelSession().getSocketChannel() : socketChannel;
    }

    public static boolean writeBuffer(SocketChannel socketChannel, byte[] bytes) {
        return writeByteBuffer(null, socketChannel, (byte) 0, 0, bytes);
    }

    public static boolean writeBuffer(SocketChannel socketChannel, byte[] bytes, int off, int len) {
        return writeByteBuffer(null, socketChannel, (byte) 0, 0, bytes, off, len);
    }

    public static boolean writeByteBufferSuccess(SelSession selSession, SocketChannel socketChannel, boolean success, int callbackIndex, byte[] bytes) {
        return writeByteBuffer(selSession, socketChannel, success == true ? 0 : SocketAdapter.ERROR_OR_SPECIAL_FLAG, callbackIndex, bytes);
    }

    public static boolean writeByteBuffer(SelSession selSession, SocketChannel socketChannel, int callbackIndex, byte[] bytes) {
        return InputSocketContext.ME.getBufferResolver().writeByteBuffer(selSession, socketChannel, (byte) 0, callbackIndex, bytes, 0, bytes.length, null, null);
    }

    public static boolean writeByteBuffer(SelSession selSession, SocketChannel socketChannel, byte flag, int callbackIndex, byte[] bytes) {
        return writeByteBuffer(selSession, socketChannel, flag, callbackIndex, bytes, 0, bytes.length);
    }

    public static boolean writeByteBuffer(SelSession selSession, SocketChannel socketChannel, byte flag, int callbackIndex, byte[] bytes, int off, int len) {
        return InputSocketContext.ME.getBufferResolver().writeByteBuffer(selSession, socketChannel, flag, callbackIndex, bytes, off, len, null, null);
    }

    public InputSocketAtt getSocketAtt() {
        return socketAtt;
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
        return socketAtt.getUrl();
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
        return socketAtt.getMethod();
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean paramHuman() {
        return (flag & SocketAdapter.HUMAN_FLAG) != 0;
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
        if (inputStream == null) {
            inputStream = socketAtt.getPostInputStream();
        }

        return inputStream;
    }

    @Override
    public String getInput() {
        if (input == null) {
            input = socketAtt.getPostInput();
            if (input == null) {
                input = KernelLang.NULL_STRING;
            }
        }

        return input;
    }

    @Override
    public boolean setCode(int code) {
        return false;
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

    public abstract SocketBufferResolver getSocketBufferResolver();

    protected byte writeFlag(byte flag) {
        if (status != ServerStatus.ON_SUCCESS.getCode()) {
            flag |= SocketAdapter.ERROR_OR_SPECIAL_FLAG;
        }

        return flag;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (outputStream == null) {
            getSocketBufferResolver().writeByteBuffer(socketAtt.getSelSession(), socketChannel, writeFlag(flag), socketAtt.getCallbackIndex(), b, 0, len, null, null);

        } else {
            outputStream.write(b, off, len);
        }
    }

    @Override
    public void close() {
        if (outputStream != null) {
            UtilPipedStream.closeCloseable(outputStream);
        }

        if (inputStream != null) {
            UtilPipedStream.closeCloseable(inputStream);
        }
    }

    public void writeUriDict() {
        if (urlVarints > 0) {
            // 字典通知
            int varints = RouteAdapter.addVarintsMapUri(getUri());
            int urlLength = KernelByte.getVarintsLength(urlVarints);
            int dataLength = urlLength + KernelByte.getVarintsLength(varints);
            byte[] dataBytes = new byte[dataLength];
            KernelByte.setVarintsLength(dataBytes, 0, urlVarints);
            KernelByte.setVarintsLength(dataBytes, urlLength, varints);
            getSocketBufferResolver().writeByteBuffer(socketAtt.getSelSession(), socketChannel, SocketAdapter.URI_DICT_FLAG, 0, dataBytes, 0, dataLength, null, null);
            urlVarints = 0;
        }
    }

    @Override
    public boolean readyOutputStream() throws IOException {
        if (outputStream == null && !UtilContext.isWarnIdlePool()) {
            SelSession selSession = socketAtt.getSelSession();
            if (selSession != null) {
                UtilPipedStream.OutInputStream inputStream = new UtilPipedStream.OutInputStream();
                outputStream = new UtilPipedStream.WrapOutStream(inputStream);
                if (!getSocketBufferResolver().writeByteBuffer(selSession, socketChannel, writeFlag(flag), socketAtt.getCallbackIndex(), KernelLang.NULL_BYTES, 0, 0, inputStream, outputStream)) {
                    UtilPipedStream.closeCloseable(outputStream);
                    return false;
                }

                return true;
            }
        }

        return true;
    }

    public static class InputSocketAtt {

        protected Serializable id;

        protected byte[] buffer;

        protected byte flag;

        protected SelSession selSession;

        protected int callbackIndex;

        protected String url;

        protected int urlVarints;

        protected int postDataLength;

        protected InputStream inputStream;

        public InputSocketAtt(Serializable id, byte[] buffer, byte flag, int off, SelSession selSession, InputStream inputStream) {
            this.id = id;
            this.flag = flag;
            this.buffer = buffer;
            this.selSession = selSession;
            int headerLength = off;
            int bufferLength = buffer.length;
            boolean inVarints = false;
            if ((flag & SocketAdapter.URI_DICT_FLAG) != 0) {
                if ((flag & SocketAdapter.ERROR_OR_SPECIAL_FLAG) == 0) {
                    inVarints = true;

                } else {
                    urlVarints = SocketAdapter.getVarints(buffer, headerLength, bufferLength);
                    headerLength += SocketAdapter.getVarintsLength(urlVarints);
                }
            }

            if ((flag & SocketAdapter.CALLBACK_FLAG) != 0) {
                callbackIndex = SocketAdapter.getVarints(buffer, headerLength, bufferLength);
                headerLength += SocketAdapter.getVarintsLength(callbackIndex);
            }

            if (inVarints) {
                int varints = SocketAdapter.getVarints(buffer, headerLength, bufferLength);
                headerLength += SocketAdapter.getVarintsLength(varints);

                url = RouteAdapter.UriForVarints(varints);
                postDataLength = bufferLength - headerLength;

            } else {
                if (inputStream == null && (flag & SocketAdapter.POST_FLAG) != 0) {
                    postDataLength = SocketAdapter.getVarints(buffer, headerLength, bufferLength);
                    headerLength += SocketAdapter.getVarintsLength(postDataLength);
                }

                url = new String(buffer, headerLength, buffer.length - headerLength - postDataLength,
                        ContextUtils.getCharset());
            }

            this.inputStream = inputStream;
        }

        public Serializable getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }

        public int getUrlVarints() {
            return urlVarints;
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

        public int getPostDataLength() {
            return postDataLength;
        }

        protected InputStream getPostInputStream() {
            return buffer == null ? null : new ByteArrayInputStream(buffer, buffer.length - postDataLength, postDataLength);
        }

        protected String getPostInput() {
            if (buffer != null) {
                return new String(buffer, buffer.length - postDataLength, postDataLength, ContextUtils.getCharset());

            } else if (inputStream != null) {
                try {
                    return HelperIO.toString(inputStream, ContextUtils.getCharset());

                } catch (IOException e) {
                    return KernelLang.NULL_STRING;
                }
            }

            return null;
        }

        public Object getMeta(String name) {
            return selSession == null ? null : selSession.getMeta(name);
        }

        public void setMeta(String name, Object value) {
            if (selSession != null) {
                selSession.setMeta(name, value);
            }
        }
    }
}
