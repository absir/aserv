package com.absir.aserv.slave.service;

import G2.Protocol.PLoginData;
import G2.Protocol.PLoginMessage;
import G2.Protocol.PLoginStatusType;
import com.absir.aserv.game.bean.JPlayerSession;
import com.absir.aserv.game.context.GameComponent;
import com.absir.aserv.game.context.JbPlayerContext;
import com.absir.aserv.system.service.BeanService;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.context.core.ContextUtils;
import com.absir.data.value.IProto;
import com.absir.dubbo.service.master.MasterService;
import com.absir.server.converter.ProtoBodyConverter;
import com.absir.server.in.Input;
import com.absir.server.route.RouteMatcher;
import com.absir.server.route.returned.ReturnedResolverBody;
import com.absir.server.socket.InputSocket;
import com.absir.server.socket.InputSocketContext;
import com.absir.server.socket.SelSession;
import com.absir.server.socket.resolver.SocketServerResolver;
import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Created by absir on 16/3/22.
 */
@Base
@Bean
public class GameSocketService extends SocketServerResolver {

    public static final GameSocketService ME = BeanFactoryUtils.get(GameSocketService.class);

    @Inject
    MasterService masterService;

    @Inject
    ProtoBodyConverter protoBodyConverter;

    public void writeGoogleProto(SocketChannel socketChannel, SelSession selSession, IProto proto) throws IOException {
        writeGoogleProto(socketChannel, selSession, 0, proto);
    }

    public void writeGoogleProto(SocketChannel socketChannel, SelSession selSession, int callbackIndex, IProto proto) throws IOException {
        Codec<IProto> codec = (Codec<IProto>) ProtobufProxy.create(proto.getClass());
        InputSocket.writeByteBuffer(InputSocketContext.ME.getBufferResolver(), selSession, socketChannel, callbackIndex, codec.encode(proto));
    }

    @Override
    public boolean on(Input input, RouteMatcher routeMatcher) throws Throwable {
        ReturnedResolverBody.ME.setBodyConverter(input, protoBodyConverter);
        return super.on(input, routeMatcher);
    }

    @Override
    public void register(SocketChannel socketChannel, SelSession selSession, byte[] buffer, long currentTime) throws Throwable {
        PLoginData loginData = PLoginData.CODEC.decode(buffer);
        PLoginMessage loginMessage = new PLoginMessage();
        if (loginData.getPlayerId() <= 0) {
            //登录失效
            loginMessage.setStatusType(PLoginStatusType.LoginFailed);
            writeGoogleProto(socketChannel, selSession, loginMessage);
            return;
        }

        if (GameComponent.ME.findServerContext(loginData.getServerId()) == null) {
            //区未开放
            loginMessage.setStatusType(PLoginStatusType.ServerClosed);
            writeGoogleProto(socketChannel, selSession, loginMessage);
            return;
        }

        long contextTime = ContextUtils.getContextTime();
        long playerId = loginData.getPlayerId();
        JbPlayerContext playerContext = null;
        if (playerId > 0) {
            JPlayerSession playerSession = BeanService.ME.get(JPlayerSession.class, playerId);
            if (playerSession == null || playerSession.getPassTime() <= contextTime || playerSession.getLoginTime() > loginData.getLoginTime()) {
                //登录失效
                loginMessage.setStatusType(PLoginStatusType.LoginLose);
                writeGoogleProto(socketChannel, selSession, loginMessage);
                return;
            }
        }

        long lifeTime = masterService.login(playerId, loginData.getSessionId());
        if (lifeTime <= 0) {
            //登录失败
            loginMessage.setStatusType(PLoginStatusType.LoginFailed);
            writeGoogleProto(socketChannel, selSession, loginMessage);
            return;
        }

        playerContext = (JbPlayerContext) ContextUtils.getContext(GameComponent.ME.PLAYER_CONTEXT_CLASS, playerId);
        long banTime = playerContext.getPlayer().getBanTime() - contextTime;
        if (banTime > 0) {
            //账户被封
            loginMessage.setStatusType(PLoginStatusType.PlayerBaned);
            loginMessage.setStatusValue(banTime);
            writeGoogleProto(socketChannel, selSession, loginMessage);
            return;
        }

        //登录成功
        loginMessage.setStatusType(PLoginStatusType.Success);
        writeGoogleProto(socketChannel, selSession, loginMessage);
        selSession.getSocketBuffer().setId(playerId);
        playerContext.writeLoginMessage();
    }
}