package com.absir.thrift;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.InjectBeanUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.bean.inject.value.Value;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilContext;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Created by absir on 2016/12/19.
 */
@Base
@Bean
public class ThriftService {

    public static final ThriftService ME = BeanFactoryUtils.get(ThriftService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(ThriftService.class);

    private TMultiplexedProcessor multiplexedProcessor;

    private TServer server;

    @Value("thrift.address")
    //"localhost"
    private String thriftAddress;

    @Value("thrift.port")
    private int thriftPort = 9292;

    @Value("thrift.timeout")
    private int thriftTimeout = 10000;

    @Value("thrift.server")
    private int thriftServer = 0;

    @Value("thrift.trust.store")
    private String trustStore;

    @Value("thrift.trust.password")
    private String trustPass;

    public TMultiplexedProcessor getMultiplexedProcessor() {
        return multiplexedProcessor;
    }

    public TServer getServer() {
        return server;
    }

    @Inject(type = InjectType.Selectable)
    protected void initService(TProcessor[] processors) throws UnknownHostException, TTransportException {
        multiplexedProcessor = new TMultiplexedProcessor();
        if (processors != null) {
            for (TProcessor processor : processors) {
                multiplexedProcessor.registerProcessor(InjectBeanUtils.getBeanType(processor).getSimpleName(), processor);
            }
        }

        if (thriftPort > 0) {
            //SocketServer socketServer = new SocketServer();
            //socketServer.start()

            InetSocketAddress inetSocketAddress = KernelString.isEmpty(thriftAddress) ? null : InetSocketAddress.createUnresolved(thriftAddress, thriftPort);
            TNonblockingServerSocket serverSocket = inetSocketAddress == null ? new TNonblockingServerSocket(thriftPort, thriftTimeout) : new TNonblockingServerSocket(inetSocketAddress, thriftTimeout);
            TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(serverSocket);
            args.processor(multiplexedProcessor);
            args.transportFactory(new TFramedTransport.Factory());
            args.protocolFactory(new TCompactProtocol.Factory());
            args.executorService(UtilContext.getThreadPoolExecutor());
            server = new TThreadedSelectorServer(args);
            server.serve();
            LOGGER.info("Thrift service start at " + (thriftAddress == null ? "" : thriftAddress) + "[" + serverSocket.getPort() + "]");
        }

    }


}
