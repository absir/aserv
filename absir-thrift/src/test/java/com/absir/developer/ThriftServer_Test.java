package com.absir.developer;

import com.absir.client.SocketAdapter;
import com.absir.client.helper.HelperJson;
import com.absir.core.kernel.KernelLang;
import com.absir.data.json.ThriftBaseSerializer;
import com.absir.server.on.OnPut;
import com.absir.server.socket.InputSocket;
import com.absir.server.socket.resolver.IBufferResolver;
import com.absir.server.socket.resolver.SocketBufferResolver;
import com.absir.thrift.*;
import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tbase_test.PushService;
import tbase_test.RpcService;
import tbase_test.TPlatformFrom;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by absir on 2016/12/6.
 */
@RunWith(value = JUnit4.class)
public class ThriftServer_Test extends ThriftService {

    @Override
    protected IBufferResolver getBufferResolver() {
        return new SocketBufferResolver();
    }

    @Test
    public void test() throws Exception {
        TMultiplexedProcessorProxy processorProxy = new TMultiplexedProcessorProxy();
        RpcService.Iface rpcService = new RpcService.Iface() {

            private PushService.Client push;

            @Override
            public TPlatformFrom setting(TPlatformFrom platformFrom) throws TException {
                System.out.println("invoke = " + HelperJson.encodeNull(platformFrom));
                platformFrom.setChannel("ddddd");
                System.out.println(Arrays.toString(ThriftBaseSerializer.serializerBytes(platformFrom)));
                return platformFrom;
            }

            @Override
            public TPlatformFrom setting2(TPlatformFrom platformFrom) throws TException {
                push = getPushClient(PushService.Client.class, new PushService.Client.Factory(), ((InputSocket) OnPut.get().getInput()).getSocketAtt().getSelSession());
                push.setting(platformFrom);
                System.out.println("invoke2 = " + HelperJson.encodeNull(platformFrom));
                platformFrom.setChannel("ddddd2");
                System.out.println(Arrays.toString(ThriftBaseSerializer.serializerBytes(platformFrom)));
                return platformFrom;
            }

            @Override
            public TPlatformFrom setting3(TPlatformFrom platformFrom) throws TException {
                System.out.println("invoke3 = " + HelperJson.encodeNull(platformFrom));
                platformFrom.setChannel("ddddd3");
                push.setting(platformFrom);
                System.out.println(Arrays.toString(ThriftBaseSerializer.serializerBytes(platformFrom)));
                return platformFrom;
            }
        };


        processorProxy.registerTypeProcessor(RpcService.Iface.class, rpcService, new RpcService.Processor<RpcService.Iface>(rpcService));
        this.processorProxy = processorProxy;
        this.startServer();

        SocketAdapter socketAdapter = new SocketAdapter();
        socketAdapter.setCallbackConnect(new SocketAdapter.CallbackAdapter() {
            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                try {
//                    SocketChannel socketChannel = SocketChannel.open();
//                    socketChannel.connect(new InetSocketAddress("127.0.0.1", thriftPort));
//                    adapter.setSocket(socketChannel.socket());
                    Socket socket = new Socket("127.0.0.1", thriftPort);
                    adapter.setSocket(socket);
                    adapter.receiveSocketChannelStart();
                    //adapter.lastedBeat();
                    adapter.sendData(adapter.sendDataBytes(KernelLang.NULL_BYTES, false, false, 0, null));

                } catch (Exception e) {
                    LOGGER.error("connectAdapter error", e);
                }
            }
        });

        socketAdapter.setAcceptCallback(new SocketAdapter.CallbackAdapter() {
            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                adapter.setRegistered(true);
            }
        });

        socketAdapter.setRegisterCallback(new SocketAdapter.CallbackAdapter() {
            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                adapter.setRegistered(true);
            }
        });


        TAdapterTransport<SocketAdapter> adapterTransport = new TAdapterTransport(socketAdapter);
        TSocketAdapterProtocol socketAdapterProtocol = new TSocketAdapterProtocol(adapterTransport, RpcService.class.getSimpleName());

        TSocketAdapterReceiver socketAdapterReceiver = new TSocketAdapterReceiver();
        socketAdapterReceiver.registerTypeProcessor(PushService.Iface.class, new PushService.Iface() {
            @Override
            public void setting(TPlatformFrom platformFrom) throws TException {
                System.out.println("receiver push = " + HelperJson.encodeNull(platformFrom));
            }

        }, new PushService.Processor(null));

        socketAdapterReceiver.bind(socketAdapterProtocol);

        RpcService.Client client = new RpcService.Client(socketAdapterProtocol);
        TPlatformFrom platformFrom = new TPlatformFrom();
        //platformFrom.setChannel("dsdsadsadasd");
        platformFrom.setStrList(new ArrayList<String>());
        platformFrom.getStrList().add("ccc");
        //platformFrom.getStrList().add(null);
        platformFrom.getStrList().add("ddd");

        System.out.println(Arrays.toString(ThriftBaseSerializer.serializerBytes(platformFrom)));

        platformFrom = client.setting(platformFrom);
        System.out.println("return = " + HelperJson.encodeNull(platformFrom));

        platformFrom = new TPlatformFrom();
        platformFrom = client.setting(platformFrom);
        System.out.println("return = " + HelperJson.encodeNull(platformFrom));

        platformFrom.setChannel(null);
        platformFrom = client.setting(platformFrom);
        System.out.println("return = " + HelperJson.encodeNull(platformFrom));

        platformFrom = client.setting2(platformFrom);
        System.out.println("return = " + HelperJson.encodeNull(platformFrom));

        platformFrom = client.setting3(platformFrom);
        System.out.println("return = " + HelperJson.encodeNull(platformFrom));
    }

}
