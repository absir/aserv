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
import com.absir.bean.inject.value.Value;
import com.absir.client.SocketAdapter;
import com.absir.client.helper.HelperEncrypt;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelByte;
import com.absir.master.InputMaster;
import com.absir.master.InputMasterContext;
import com.absir.master.MasterChannelContext;
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

import java.io.InputStream;
import java.io.Serializable;
import java.nio.channels.SocketChannel;

@Base
@Bean
public class MasterServerResolver extends SocketServerResolver {

    public static final MasterServerResolver ME = BeanFactoryUtils.get(MasterServerResolver.class);
    @Value("server.socket.stream.max.master")
    private static int streamMaxMaster = Integer.MAX_VALUE;

    @Override
    public int getStreamMax() {
        return streamMaxMaster;
    }

    @Override
    public long acceptTimeoutNIO(final SocketChannel socketChannel) throws Throwable {
        ContextUtils.getThreadPoolExecutor().execute(new Runnable() {

            @Override
            public void run() {
                InputSocket.writeByteBuffer(null, socketChannel, (byte) 0, 0, KernelByte.getLengthBytes(socketChannel.hashCode()));
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
        InputMasterContext.ME.unRegisterSlaveKey((String) id, socketChannel, currentTime);
    }

    @Override
    protected Input input(String uri, InMethod inMethod, InModel model, InputSocketAtt req, SocketChannel res) {
        InputMaster input = new InputMaster(model, req, res);
        ReturnedResolverBody.ME.setBodyConverter(input, BodyMsgResolver.ME);
        return input;
    }

    @Override
    protected void doResponse(SocketChannel socketChannel, Serializable id, byte flag, int offset, byte[] buffer, InputStream inputStream) {
        MasterChannelContext channelContext = InputMasterContext.ME.getServerContext().getChannelContexts().get(id);
        if (channelContext != null) {
            flag &= SocketAdapter.RESPONSE_FLAG_REMOVE;
            channelContext.getMasterChannelAdapter().receiveCallback(1, buffer, flag);
        }
    }

}
