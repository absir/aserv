package com.absir.thrift;

import com.absir.client.SocketAdapter;
import com.absir.context.core.ContextUtils;
import com.absir.context.schedule.cron.CronFixDelayRunnable;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilContext;
import org.apache.thrift.ProcessFunction;
import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.ThriftVisitor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by absir on 2016/12/20.
 */
public class TSocketAdapterReceiver implements SocketAdapter.CallbackAdapter {

    public static final int PUSH_CALLBACK_INDEX = 8;
    public static final int DICT_CALLBACK_INDEX = 9;
    protected static final Logger LOGGER = LoggerFactory.getLogger(TSocketAdapterReceiver.class);
    protected Map<String, KernelLang.ObjectEntry<Object, ProcessFunction>> nameMapIFaceProcessFunction;
    protected TSocketAdapterProtocol adapterProtocol;
    protected SocketAdapter socketAdapter;
    private Map<Integer, List<TProtocolTimeout>> callbackIndexMapProtocol = new HashMap<Integer, List<TProtocolTimeout>>();

    public static String getServiceName(Class<?> ifaceType) {
        return KernelClass.forName(KernelClass.parentName(ifaceType)).getSimpleName();
    }

    public static String getServiceUri(String serviceName, String name) {
        return serviceName + ':' + name;
    }

    public <T> void registerTypeProcessor(Class<T> ifaceType, T iface, TBaseProcessor<T> baseProcessor) {
        registerBaseProcessor(getServiceName(ifaceType), iface, baseProcessor);
    }

    public void registerBaseProcessor(String serviceName, Object iface, TBaseProcessor baseProcessor) {
        if (nameMapIFaceProcessFunction == null) {
            nameMapIFaceProcessFunction = new HashMap<String, KernelLang.ObjectEntry<Object, ProcessFunction>>();
        }

        for (Map.Entry<String, ProcessFunction> entry : ((Map<String, ProcessFunction>) (Object) baseProcessor.getProcessMapView()).entrySet()) {
            ProcessFunction processFunction = entry.getValue();
            if (!ThriftVisitor.isOneWay(processFunction)) {
                LOGGER.warn("TSocketAdapterReceiver registerBaseProcessor " + iface + " method:[" + processFunction.getMethodName() + "] is not oneway");
                continue;
            }

            nameMapIFaceProcessFunction.put(getServiceUri(serviceName, entry.getKey()), new KernelLang.ObjectEntry<Object, ProcessFunction>(iface, processFunction));
        }
    }

    public void bind(TSocketAdapterProtocol protocol) {
        adapterProtocol = protocol;
        socketAdapter = protocol.getTransport().getAdapter().getSocketAdapter();
        socketAdapter.putReceiveCallbacks(PUSH_CALLBACK_INDEX, 0, this);
        socketAdapter.putReceiveCallbacks(DICT_CALLBACK_INDEX, 0, new SocketAdapter.CallbackAdapter() {

            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                int varints = SocketAdapter.getVarints(buffer, offset, buffer.length);
                offset += SocketAdapter.getVarintsLength(varints);
                int length = buffer.length - offset;
                String uri = length <= 0 ? null : new String(buffer, offset, length);
                triggerCallbackProtocol(varints, uri, adapter);
            }

        });

        // 清除之前PUSH数据
        socketAdapter.addCloseRunnable(new Runnable() {
            @Override
            public void run() {
                synchronized (callbackIndexMapProtocol) {
                    callbackIndexMapProtocol.clear();
                }
            }
        });

        try {
            ContextUtils.getScheduleFactory().addRunnable(new CronFixDelayRunnable(new Runnable() {

                @Override
                public void run() {
                    checkReceiverTimeout();
                }

            }, 3000));

        } catch (Throwable e) {
            Environment.throwable(e);
        }
    }

    protected void checkReceiverTimeout() {
        synchronized (callbackIndexMapProtocol) {
            Iterator<Map.Entry<Integer, List<TProtocolTimeout>>> iterator = callbackIndexMapProtocol.entrySet().iterator();
            long currentTime = UtilContext.getCurrentTime();
            while (iterator.hasNext()) {
                Map.Entry<Integer, List<TProtocolTimeout>> entry = iterator.next();
                List<TProtocolTimeout> protocolTimeouts = entry.getValue();
                if (protocolTimeouts != null && !protocolTimeouts.isEmpty()) {
                    TProtocolTimeout protocolTimeout = protocolTimeouts.get(0);
                    if (protocolTimeout.disconnectNumber != socketAdapter.getDisconnectNumber()) {
                        return;
                    }

                    if (protocolTimeout.timeoutTime <= currentTime) {
                        int size = protocolTimeouts.size();
                        for (size--; size >= 0; size--) {
                            if (protocolTimeouts.get(size).timeoutTime <= currentTime) {
                                protocolTimeouts.remove(size);
                            }
                        }

                        if (!protocolTimeouts.isEmpty()) {
                            continue;
                        }
                    }
                }

                iterator.remove();
            }
        }
    }

    protected long getReceiverTimeout() {
        return 30000;
    }

    protected void addCallbackProtocol(int callbackIndex, TProtocol protocol, SocketAdapter adapter, long timeout) {
        synchronized (callbackIndexMapProtocol) {
            List<TProtocolTimeout> protocolTimeouts = callbackIndexMapProtocol.get(callbackIndex);
            if (protocolTimeouts == null) {
                protocolTimeouts = new ArrayList<TProtocolTimeout>();
                callbackIndexMapProtocol.put(callbackIndex, protocolTimeouts);
            }

            TProtocolTimeout protocolTimeout = new TProtocolTimeout();
            protocolTimeout.protocol = protocol;
            protocolTimeout.disconnectNumber = adapter.getDisconnectNumber();
            protocolTimeout.timeoutTime = timeout + UtilContext.getCurrentTime();
            protocolTimeouts.add(protocolTimeout);
        }
    }

    protected void triggerCallbackProtocol(int callbackIndex, String uri, SocketAdapter adapter) {
        synchronized (callbackIndexMapProtocol) {
            List<TProtocolTimeout> protocolTimeouts = callbackIndexMapProtocol.remove(callbackIndex);
            if (protocolTimeouts != null && !KernelString.isEmpty(uri)) {
                KernelLang.ObjectEntry<Object, ProcessFunction> ifaceProcessFunction = nameMapIFaceProcessFunction.get(uri);
                if (ifaceProcessFunction == null) {
                    LOGGER.warn("PUSH callback not found register [" + uri + "]");
                    return;
                }

                adapter.setVarintsUri(callbackIndex, uri);
                Object iface = ifaceProcessFunction.getKey();
                ProcessFunction processFunction = ifaceProcessFunction.getValue();
                for (TProtocolTimeout protocolTimeout : protocolTimeouts) {
                    if (protocolTimeout.disconnectNumber == adapter.getDisconnectNumber()) {
                        try {
                            processFunction.process(0, protocolTimeout.protocol, null, iface);

                        } catch (Throwable e) {
                            LOGGER.error("PUSH callback " + callbackIndex + " error [" + processFunction.getMethodName() + "]", e);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
        int callbackIndex = 0;
        String methodName = "";
        try {
            TCompactProtocol protocol = new TCompactProtocol(new TIOStreamTransport(adapterProtocol.decrypt(offset, buffer)));
            callbackIndex = protocol.readI32();
            String uri = adapter.getVarintsUri(callbackIndex);
            if (uri == null) {
                byte[] dataBytes = adapter.sendDataBytes(0, SocketAdapter.getVarintsLengthBytes(callbackIndex), true, false, SocketAdapter.RESPONSE_FLAG | SocketAdapter.URI_DICT_FLAG, 0, null);
                adapter.sendData(dataBytes);
                addCallbackProtocol(callbackIndex, protocol, adapter, getReceiverTimeout());
                return;
            }

            KernelLang.ObjectEntry<Object, ProcessFunction> ifaceProcessFunction = nameMapIFaceProcessFunction.get(uri);
            if (ifaceProcessFunction == null) {
                LOGGER.warn("PUSH callback not found register " + uri);
                return;
            }

            ifaceProcessFunction.getValue().process(0, protocol, null, ifaceProcessFunction.getKey());

        } catch (Exception e) {
            LOGGER.error("PUSH callback " + callbackIndex + " error[" + methodName + "]", e);
        }
    }

    protected static class TProtocolTimeout {

        public TProtocol protocol;

        public int disconnectNumber;

        public long timeoutTime;
    }

}
