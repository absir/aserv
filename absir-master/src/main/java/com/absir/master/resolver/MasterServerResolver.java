/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月9日 上午10:19:41
 */
package com.absir.master.resolver;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.client.SocketAdapter;
import com.absir.client.SocketAdapter.CallbackAdapter;
import com.absir.client.SocketAdapter.CallbackTimeout;
import com.absir.client.SocketAdapterSel;
import com.absir.client.callback.CallbackMsg;
import com.absir.client.helper.HelperEncrypt;
import com.absir.context.core.ContextUtils;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelByte;
import com.absir.core.kernel.KernelLang.ObjectTemplate;
import com.absir.core.util.UtilContext;
import com.absir.core.util.UtilPipedStream;
import com.absir.data.helper.HelperDatabind;
import com.absir.master.InputMaster;
import com.absir.master.InputMasterContext;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.route.returned.ReturnedResolverBody;
import com.absir.server.socket.InputSocket;
import com.absir.server.socket.InputSocket.InputSocketAtt;
import com.absir.server.socket.InputSocketContext;
import com.absir.server.socket.SelSession;
import com.absir.server.socket.resolver.BodyMsgResolver;
import com.absir.server.socket.resolver.SocketServerResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.channels.SocketChannel;

@Base
@Bean
public class MasterServerResolver extends SocketServerResolver {

    public static final MasterServerResolver ME = BeanFactoryUtils.get(MasterServerResolver.class);

    protected SocketAdapterSel masterAdapter = createMasterAdapter();

    @Override
    public long acceptTimeoutNIO(final SocketChannel socketChannel) throws Throwable {
        ContextUtils.getThreadPoolExecutor().execute(new Runnable() {

            @Override
            public void run() {
                InputSocket.writeByteBuffer(MasterBufferResolver.ME, null, socketChannel, 0,
                        KernelByte.getLengthBytes(socketChannel.hashCode()));
            }
        });

        return InputSocketContext.getAcceptTimeout();
    }

    @Override
    public void register(SocketChannel socketChannel, SelSession selSession, byte[] buffer, long currentTime) throws Throwable {
        String[] params = new String(buffer).split(",", 16);
        if (params.length >= 2) {
            byte[] secrets = KernelByte.getLengthBytes(socketChannel.hashCode());
            String validate = HelperEncrypt.encryptionMD5(InputMasterContext.ME.getKey(), secrets);
            if (validate.equals(params[0])) {
                String id = idForMaster(params, socketChannel, selSession);
                if (id != null) {
                    selSession.getSocketBuffer().setId(id);
                    InputMasterContext.ME.registerSlaveKey(id, secrets, validate, params, socketChannel, currentTime);
                }
            }
        }
    }

    public String idForMaster(String[] params, SocketChannel socketChannel, SelSession selSession) {
        return params[1] + ',' + socketChannel.socket().getInetAddress().getHostAddress();
    }

    @Override
    public void unRegister(Serializable id, SocketChannel socketChannel, SelSession selSession, long currentTime) {
        InputMasterContext.ME.unregisterSlaveKey((String) id, socketChannel, currentTime);
    }

    @Override
    protected Input input(String uri, InMethod inMethod, InModel model, InputSocketAtt req, SocketChannel res) {
        InputMaster input = new InputMaster(model, req, res);
        ReturnedResolverBody.ME.setBodyConverter(input, BodyMsgResolver.ME);
        return input;
    }

    @Override
    protected void doResponse(SocketChannel socketChannel, Serializable id, byte flag, byte[] buffer) {
        masterAdapter.receiveCallback(0, buffer, (byte) flag);
    }

    protected SocketAdapterSel createMasterAdapter() {
        return new SocketAdapterSel();
    }

    public SocketAdapterSel getMasterAdapter() {
        return masterAdapter;
    }

    public void sendData(SocketChannel socketChannel, String uri, Object postData, CallbackMsg<?> callbackMsg)
            throws IOException {
        sendDataBytes(socketChannel, uri, postData == null ? null : HelperDatabind.writeAsBytes(postData), callbackMsg);
    }

    public void sendDataBytes(SocketChannel socketChannel, String uri, byte[] postData, CallbackMsg<?> callbackMsg)
            throws IOException {
        sendDataBytes(socketChannel, uri.getBytes(ContextUtils.getCharset()), true, false, postData, 60000,
                callbackMsg);
    }

    public void sendDataBytes(SocketChannel socketChannel, byte[] dataBytes, boolean head, boolean debug,
                              byte[] postData, int timeout, CallbackAdapter callbackAdapter) {
        int callbackIndex = masterAdapter.generateCallbackIndex();
        if (callbackAdapter != null) {
            masterAdapter.putReceiveCallbacks(callbackIndex, timeout, callbackAdapter);
        }

        byte[] buffer = masterAdapter.sendDataBytes(5, dataBytes, head, debug, callbackIndex, postData);
        buffer[4] = SocketAdapter.CALLBACK_FLAG;
        KernelByte.setLength(buffer, 5, 1);
        if (!InputSocket.writeBuffer(socketChannel, buffer)) {
            masterAdapter.receiveCallback(0, null, (byte) 0, callbackIndex);
        }
    }

    public void sendDataBytes(final SocketChannel socketChannel, byte[] dataBytes, boolean head, boolean debug,
                              final InputStream inputStream, final int timeout, CallbackAdapter callbackAdapter) {
        boolean sended = false;
        final ObjectTemplate<Integer> nextIndex = masterAdapter.getActivePool().addObject();
        try {
            CallbackTimeout callbackTimeout = null;
            int callbackIndex = masterAdapter.generateCallbackIndex();
            if (callbackAdapter != null) {
                callbackTimeout = masterAdapter.putReceiveCallbacks(callbackIndex, timeout, callbackAdapter);
            }

            byte[] buffer = masterAdapter.sendDataBytes(9, dataBytes, head, debug, SocketAdapter.STREAM_FLAG,
                    callbackIndex, null);
            System.arraycopy(buffer, 9, buffer, 5, buffer.length - 9);
            buffer[4] = SocketAdapter.CALLBACK_FLAG;
            KernelByte.setLength(buffer, 5, 1);
            final int streamIndex = nextIndex.object;
            KernelByte.setLength(buffer, buffer.length - 4, streamIndex);
            final CallbackTimeout CallbackTimeout = callbackTimeout;
            if (InputSocket.writeBuffer(socketChannel, buffer)) {
                ContextUtils.getThreadPoolExecutor().execute(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            int postBuffLen = SocketAdapterSel.POST_BUFF_LEN;
                            byte[] sendBufer = masterAdapter.sendDataBytes(9 + postBuffLen, null, true, false,
                                    SocketAdapter.STREAM_FLAG | SocketAdapter.POST_FLAG, 0, null);
                            sendBufer[4] = SocketAdapter.CALLBACK_FLAG;
                            KernelByte.setLength(sendBufer, 5, 1);
                            sendBufer[9] = sendBufer[9 + postBuffLen];
                            KernelByte.setLength(sendBufer, 10, streamIndex);
                            int len;
                            try {
                                while ((len = inputStream.read(sendBufer, 14, sendBufer.length)) > 0) {
                                    len += 10;
                                    KernelByte.setLength(sendBufer, 0, len);
                                    if (nextIndex.object == null
                                            || !InputSocket.writeBuffer(socketChannel, sendBufer, 0, len)) {
                                        return;
                                    }

                                    if (CallbackTimeout != null) {
                                        CallbackTimeout.timeout = UtilContext.getCurrentTime() + timeout;
                                    }
                                }

                            } catch (Exception e) {
                                if (Environment.getEnvironment() == Environment.DEVELOP) {
                                    e.printStackTrace();
                                }

                                return;
                            }

                        } finally {
                            masterAdapter.getActivePool().remove(streamIndex);
                            UtilPipedStream.closeCloseable(inputStream);
                        }
                    }
                });

                sended = true;
            }

        } finally {
            if (!sended) {
                masterAdapter.getActivePool().remove(nextIndex.object);
                UtilPipedStream.closeCloseable(inputStream);
            }
        }
    }

}
