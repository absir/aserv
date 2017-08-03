package com.absir.thrift;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.*;
import com.absir.client.ServerEnvironment;
import com.absir.client.SocketAdapter;
import com.absir.client.helper.HelperEncrypt;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilContext;
import com.absir.core.util.UtilPipedStream;
import com.absir.server.in.InModel;
import com.absir.server.on.OnPut;
import com.absir.server.socket.InputSocket;
import com.absir.server.socket.SelSession;
import com.absir.server.socket.SocketBuffer;
import com.absir.server.socket.SocketServer;
import com.absir.server.socket.resolver.*;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 2016/12/19.
 */
@Base
@Bean
public class ThriftService implements ISessionResolver, IBufferResolver.IServerDispatch {

    public static final ThriftService ME = BeanFactoryUtils.get(ThriftService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(ThriftService.class);

    protected static final TypeVariable BASE_VARIABLE = TBaseProcessor.class.getTypeParameters()[0];

    protected TMultiplexedProcessorProxy processorProxy;

    protected SocketServer server;

    @Value("thrift.host")
    //"localhost"
    protected String thriftHost;

    @Value("thrift.port")
    protected int thriftPort = getDefaultThriftPort();

    @Value("thrift.accept.timeout")
    protected long thriftAcceptTimeout = 30000;

    @Value("thrift.idle.timeout")
    protected long thriftIdleTimeout = 30000;

    @Value("thrift.backlog")
    protected int backlog = 50;

    @Value("thrift.bufferSize")
    protected int bufferSize = 1024;

    @Value("thrift.receiveBufferSize")
    protected int receiveBufferSize = 2048;

    @Value("thrift.sendBufferSize")
    protected int sendBufferSize = 2048;

    protected Map<Class, String> classMapServiceName;

    @Value("thrift.encryptKey")
    protected String encryptKey = "absir.thrift";

    public static String getServiceName(TBaseProcessor baseProcessor) {
        Class<?> faceType = KernelClass.typeClass(baseProcessor.getClass(), BASE_VARIABLE);
        String parentName = KernelClass.parentName(faceType);
        return parentName.substring(parentName.lastIndexOf('.') + 1, parentName.length());
    }

    protected int getDefaultThriftPort() {
        return 9292;
    }

    public TMultiplexedProcessorProxy getProcessorProxy() {
        return processorProxy;
    }

    public SocketServer getServer() {
        return server;
    }

    @Inject(type = InjectType.Selectable)
    protected final void initService(IFaceServer[] faceServers) throws IOException, TTransportException {
        processorProxy = new TMultiplexedProcessorProxy();
        if (faceServers != null) {
            for (IFaceServer faceServer : faceServers) {
                TBaseProcessor baseProcessor = faceServer.getBaseProcessor();
                processorProxy.registerBaseProcessor(getServiceName(baseProcessor), faceServer, baseProcessor);
            }
        }
    }

    @Started
    protected void startServer() throws IOException {
        if (thriftPort > 0) {
            server = new SocketServer();
            startThriftServer(thriftPort, InetAddress.getByName(thriftHost), server);
        }
    }

    public void startThriftServer(int port, InetAddress inetAddress, SocketServer server) throws IOException {
        server.start(thriftAcceptTimeout, thriftIdleTimeout, port, backlog, inetAddress, bufferSize, receiveBufferSize, sendBufferSize, getBufferResolver(), this);
    }

    @Override
    public long acceptTimeout(SocketChannel socketChannel) throws Throwable {
        return thriftAcceptTimeout;
    }

    @Override
    public boolean allowBuffLength(SelSession selSession, int buffLength) {
        return buffLength < SocketBufferResolver.getBufferMax();
    }

    @Override
    public void idle(final SocketChannel socketChannel, final SelSession selSession, long contextTime) {
        selSession.retainIdleTimeout();
        UtilContext.getThreadPoolExecutor().execute(new Runnable() {

            @Override
            public void run() {
                InputSocket.writeByteBuffer(selSession, socketChannel, 0, SocketSessionResolver.getBeat());
            }
        });
    }

    protected String getEncryptKey(SocketChannel socketChannel, SelSession selSession) {
        if (KernelString.isEmpty(encryptKey)) {
            return null;
        }

        String key = String.valueOf(socketChannel.hashCode());
        selSession.getSocketBuffer().setEncryptKey(HelperEncrypt.getSROREncryptKey(encryptKey + key));
        return key;
    }

    @Override
    public void register(SocketChannel socketChannel, SelSession selSession) throws Throwable {
        int hashId = socketChannel.hashCode();
        selSession.getSocketBuffer().setId(hashId);
        String entryKey = getEncryptKey(socketChannel, selSession);
        if (entryKey == null) {
            InputSocket.writeByteBuffer(selSession, socketChannel, 0, ("ok" + ',' + ServerEnvironment.getStartTime()).getBytes());

        } else {
            InputSocket.writeByteBuffer(selSession, socketChannel, 0, ("ok" + ',' + ServerEnvironment.getStartTime() + "," + entryKey).getBytes());
        }
    }

    @Override
    public void receiveByteBuffer(final SocketChannel socketChannel, final SelSession selSession) throws Throwable {
        final SocketBuffer socketBuffer = selSession.getSocketBuffer();
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

    protected IBufferResolver getBufferResolver() {
        return InputBufferResolver.ME;
    }

    protected InputSocket.InputSocketAtt createSocketAtt(SelSession selSession, Serializable id, byte[] buffer, byte flag, int off,
                                                         SocketBuffer socketBuffer, InputStream inputStream) {
        return new InputSocket.InputSocketAtt(id, buffer, flag, off, selSession, inputStream);
    }

    public void doDispatch(SelSession selSession, SocketChannel socketChannel, Serializable id, byte[] buffer, byte flag, int off,
                           SocketBuffer socketBuffer, InputStream inputStream, long currentTime) {
        if ((flag & SocketAdapter.RESPONSE_FLAG) == 0) {
            InputSocket.InputSocketAtt socketAtt = createSocketAtt(selSession, id, buffer, flag, off, socketBuffer, inputStream);
            try {
                if (socketAtt != null) {
                    TMultiplexedProcessorProxy.IFaceProcessProxy faceProcessProxy = processorProxy.getNameMapIFaceProcessFunction().get(socketAtt.getUrl());
                    if (faceProcessProxy != null) {
                        onProcess(socketAtt, faceProcessProxy);
                        return;
                    }
                }

            } catch (Throwable e) {
                Environment.throwable(e);
            }

            UtilPipedStream.closeCloseable(inputStream);
            int callbackIndex = socketAtt.getCallbackIndex();
            if (callbackIndex != 0) {
                InputSocket.writeByteBufferSuccess(selSession, socketChannel, false, callbackIndex, InputSocket.NONE_RESPONSE_BYTES);
            }

        } else {
            if ((flag & SocketAdapter.URI_DICT_FLAG) != 0) {
                // PUSH URI_DICT_FLAG 字典压缩
                int varint = SocketAdapter.getVarints(buffer, off, buffer.length);
                String uri = SocketAdapter.uriForIndex(varint);
                if (uri == null) {
                    LOGGER.warn("ThriftService URI_DICT_FLAG not found " + varint);
                }

                int varintLength = SocketAdapter.getVarintsLength(varint);
                int uriLength = uri == null ? 0 : uri.length();
                byte[] dataBytes = new byte[varintLength + uriLength];
                SocketAdapter.setVarintsLength(dataBytes, 0, varint);
                if (uriLength > 0) {
                    System.arraycopy(uri.getBytes(), 0, dataBytes, varintLength, uriLength);
                }

                InputSocket.writeByteBuffer(selSession, socketChannel, TSocketAdapterReceiver.DICT_CALLBACK_INDEX, dataBytes);

            } else {
                doResponse(socketChannel, id, flag, off, buffer, inputStream);
            }
        }
    }

    protected void doResponse(SocketChannel socketChannel, Serializable id, byte flag, int off, byte[] buffer, InputStream inputStream) {
    }

    public InputStream decrypt(SelSession selSession, InputSocket inputSocket) throws IOException {
        InputStream inputStream = inputSocket.getInputStream();
        Object encryptKey = selSession.getSocketBuffer().getEncryptKey();
        if (encryptKey != null) {
            if (inputStream.getClass() == ByteArrayInputStream.class) {
                InputSocket.InputSocketAtt att = inputSocket.getSocketAtt();
                byte[] inBuffer = att.getBuffer();
                HelperEncrypt.decryptSRORKey(inBuffer, inBuffer.length - att.getPostDataLength(), inBuffer.length, (byte[]) encryptKey);
            }
        }

        return inputStream;
    }

    public byte[] encrypt(SelSession selSession, int offset, ByteArrayOutputStream outputStream) throws IOException {
        Object encryptKey = selSession.getSocketBuffer().getEncryptKey();
        if (encryptKey == null) {
            return outputStream.toByteArray();

        } else {
            byte[] bytes = outputStream.toByteArray();
            return HelperEncrypt.encryptSRORKey(bytes, offset, bytes.length, (byte[]) encryptKey);
        }
    }

    // Processor处理入口
    public void onProcess(InputSocket.InputSocketAtt socketAtt, TMultiplexedProcessorProxy.IFaceProcessProxy faceProcessProxy) throws IOException, TException {
        ThriftInput input = new ThriftInput(new InModel(), socketAtt, null);
        input.writeUriDict();
        OnPut onPut = new OnPut(input);
        try {
            onPut.open();
            processorProxy.process(faceProcessProxy, input, this);

        } finally {
            onPut.close();
        }
    }

    public TPushProtocol getPushProtocol(Class<? extends TServiceClient> clientType, SelSession selSession) {
        if (classMapServiceName == null) {
            synchronized (this) {
                if (classMapServiceName == null) {
                    classMapServiceName = new HashMap<Class, String>();
                }
            }
        }

        String serviceName = classMapServiceName.get(clientType);
        String parentName = null;
        if (serviceName == null) {
            parentName = KernelClass.parentName(clientType);
            serviceName = KernelClass.forName(parentName).getSimpleName();
        }

        TPushProtocol pushProtocol = new TPushProtocol(new TAdapterTransport<SelSession>(selSession), serviceName);
        //T client = factory.getClient(null, pushProtocol);
        if (parentName != null) {
            //Processor URI_DICT_FLAG
            synchronized (this) {
                if (!classMapServiceName.containsKey(clientType)) {
                    try {
                        Class<?> processorClass = KernelClass.forName(parentName + "$Processor");
                        Map<String, org.apache.thrift.ProcessFunction> processMap = new HashMap<String, org.apache.thrift.ProcessFunction>();
                        Method method = KernelReflect.declaredMethod(processorClass, "getProcessMap", Map.class);
                        method.invoke(null, processMap);
                        for (String name : processMap.keySet()) {
                            SocketAdapter.registerIndexUri(TSocketAdapterReceiver.getServiceUri(serviceName, name));
                        }

                        classMapServiceName.put(clientType, serviceName);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return pushProtocol;
    }

    public class TPushProtocol extends TAdapterProtocol<SelSession> {

        public TPushProtocol(TAdapterTransport<SelSession> adapterTransport, String serviceName) {
            super(adapterTransport, serviceName);
        }

        @Override
        public void writeMessageBegin(TMessage message) throws TException {
            super.writeMessageBegin(message);
            String uri = TSocketAdapterReceiver.getServiceUri(serviceName, message.name);
            Integer callbackIndex = SocketAdapter.indexForUri(uri);
            if (callbackIndex == null) {
                throw new RuntimeException("ThriftService push  " + uri + " not registered!?");
            }

            writeI32(callbackIndex);
        }

        @Override
        protected void sendMessage(TMessage message, ByteArrayOutputStream outputStream) throws IOException {
            SelSession selSession = getTransport().getAdapter();
            InputSocket.writeByteBuffer(selSession, selSession.getSocketChannel(), TSocketAdapterReceiver.PUSH_CALLBACK_INDEX, ThriftService.this.encrypt(selSession, 0, outputStream));
        }
    }

}
