package com.absir.thrift;

import com.absir.client.SocketAdapter;
import com.absir.core.kernel.KernelLang;
import org.apache.thrift.ProcessFunction;
import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TMemoryInputTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tbase.TMapSI;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 2016/12/20.
 */
public class TAdapterReceiver implements SocketAdapter.CallbackAdapter {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TAdapterReceiver.class);

    protected Map<String, KernelLang.ObjectEntry<Object, ProcessFunction>> nameMapIFaceProcessFunction;

    protected TAdapterProtocol adapterProtocol;

    protected Map<Integer, KernelLang.ObjectEntry<SocketAdapter.CallbackAdapter, SocketAdapter.CallbackTimeout>> receiveCallbacks;

    protected Map<String, Integer> nameMapCallbackIndex;

    public void addBaseProcessor(String name, Object iface, TBaseProcessor baseProcessor) {
        if (nameMapIFaceProcessFunction == null) {
            nameMapIFaceProcessFunction = new HashMap<String, KernelLang.ObjectEntry<Object, ProcessFunction>>();
        }

        for (Map.Entry<String, ProcessFunction> entry : ((Map<String, ProcessFunction>) (Object) baseProcessor.getProcessMapView()).entrySet()) {
            nameMapIFaceProcessFunction.put(name + ":" + entry.getKey(), new KernelLang.ObjectEntry<Object, ProcessFunction>(iface, entry.getValue()));
        }
    }

    public static final int PUSH_MAP_CALLBACK = 9;

    public void bind(TAdapterProtocol protocol) {
        adapterProtocol = protocol;
        receiveCallbacks = protocol.getTransport().getSocketAdapter().getReceiveCallbacks();
        receiveCallbacks.put(PUSH_MAP_CALLBACK, new KernelLang.ObjectEntry<SocketAdapter.CallbackAdapter, SocketAdapter.CallbackTimeout>(this, null));
    }

    @Override
    public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
        TMapSI bean = new TMapSI();
        try {
            bean.read(new TCompactProtocol(new TMemoryInputTransport(buffer, offset, buffer.length)));

        } catch (TException e) {
            LOGGER.error("PUSH_MAP_CALLBACK parse error", e);
        }

        if (nameMapCallbackIndex != null) {
            for (Integer index : nameMapCallbackIndex.values()) {
                receiveCallbacks.remove(index);
            }
        }

        nameMapCallbackIndex = bean.getTmap();
        if (nameMapCallbackIndex != null) {
            for (Map.Entry<String, Integer> entry : nameMapCallbackIndex.entrySet()) {
                KernelLang.ObjectEntry<Object, ProcessFunction> ifaceProcessFunction = nameMapIFaceProcessFunction.get(entry.getKey());
                if (ifaceProcessFunction != null) {
                    final Object iface = ifaceProcessFunction.getKey();
                    final ProcessFunction processFunction = ifaceProcessFunction.getValue();
                    receiveCallbacks.put(entry.getValue(), new KernelLang.ObjectEntry<SocketAdapter.CallbackAdapter, SocketAdapter.CallbackTimeout>(new SocketAdapter.CallbackAdapter() {

                        @Override
                        public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                            try {
                                processFunction.process(0, new TCompactProtocol(new TIOStreamTransport(adapterProtocol.decrypt(offset, buffer))), null, iface);

                            } catch (TException e) {
                                LOGGER.error("PUSH callback error[" + processFunction.getMethodName() + "]", e);
                            }
                        }

                    }, null));
                }
            }
        }
    }
}
