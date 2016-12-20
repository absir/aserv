package com.absir.developer;

import com.absir.client.SocketAdapter;
import com.absir.client.helper.HelperJson;
import com.absir.core.kernel.KernelLang;
import com.absir.server.socket.resolver.IBufferResolver;
import com.absir.server.socket.resolver.SocketBufferResolver;
import com.absir.thrift.TAdapterProtocol;
import com.absir.thrift.TAdapterTransport;
import com.absir.thrift.TMultiplexedProcessorProxy;
import com.absir.thrift.ThriftService;
import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tbase_test.RpcService;
import tbase_test.TPlatformFrom;

import java.net.Socket;

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

            @Override
            public TPlatformFrom setting(TPlatformFrom platformFrom) throws TException {
                System.out.println("invoke = " + HelperJson.encodeNull(platformFrom));
                platformFrom.setChannel("ddddd");
                //System.out.println(Arrays.toString(ThriftBaseSerializer.serializerBytes(platformFrom)));
                return platformFrom;
            }
        };

        processorProxy.registerProcessor(RpcService.class.getSimpleName(), rpcService, new RpcService.Processor<RpcService.Iface>(rpcService));
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

        TAdapterTransport adapterTransport = new TAdapterTransport(socketAdapter);
        //TAdapterProtocol adapterProtocol = new TAdapterProtocol(adapterTransport);
        RpcService.Client client = new RpcService.Client(new TAdapterProtocol(adapterTransport, RpcService.class.getSimpleName()));
        TPlatformFrom platformFrom = new TPlatformFrom();
        platformFrom.setChannel("dsdsadsadasd");

        //System.out.println(Arrays.toString(ThriftBaseSerializer.serializerBytes(platformFrom)));

        platformFrom = client.setting(platformFrom);
        System.out.println("return = " + HelperJson.encodeNull(platformFrom));
    }

}
