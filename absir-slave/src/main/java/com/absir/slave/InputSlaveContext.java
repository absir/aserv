/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月9日 下午8:25:06
 */
package com.absir.slave;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.*;
import com.absir.client.SocketAdapter;
import com.absir.client.helper.HelperEncrypt;
import com.absir.client.rpc.RpcData;
import com.absir.client.rpc.RpcInterface;
import com.absir.client.rpc.RpcSocketAdapter;
import com.absir.context.schedule.value.Schedule;
import com.absir.core.base.Environment;
import com.absir.server.route.RouteAdapter;
import com.absir.slave.resolver.ISlaveCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Base
@Bean
public class InputSlaveContext {

    public static final InputSlaveContext ME = BeanFactoryUtils.get(InputSlaveContext.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(InputSlaveContext.class);

    @Value("server.slave.ip")
    protected String ip = "127.0.0.1";

    @Value("server.slave.port")
    protected int port = 28890;

    @Value("server.slave.group")
    protected String group = "0";

    @Value("server.slave.key")
    protected String key = "absir@qq.com";

    @Value("server.slave.url")
    protected String url;

    protected String slaveKey;

    @Inject(type = InjectType.Selectable)
    @Orders
    protected ISlaveCallback[] slaveCallbacks;

    protected SlaveRpcAdapter slaveAdapter;

    protected List<SlaveRpcAdapter> slaveAdapters = new ArrayList<SlaveRpcAdapter>();

    protected List<SlaveRpcAdapter> slaveAdapterAdds;

    protected class SlaveRpcAdapter extends RpcSocketAdapter<InputSlaveAdapter> {

        protected int slaveIndex;

        public SlaveRpcAdapter(InputSlaveAdapter adapter) {
            super(adapter);
        }

        @Override
        protected void resolverRpcData(RpcInterface.RpcAttribute attribute, RpcData rpcData) {
            ME.resolverRpcData(attribute, rpcData, slaveIndex);
        }

    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

    public String getSlaveKey() {
        return slaveKey;
    }

    public RpcSocketAdapter<InputSlaveAdapter> getSlaveAdapter() {
        return slaveAdapter;
    }

    public RpcSocketAdapter<InputSlaveAdapter> getSlaveAdapter(int index) {
        if (index == 0) {
            return slaveAdapter;
        }

        return index < slaveAdapters.size() ? slaveAdapters.get(index) : null;
    }

    /**
     * 创建
     */
    protected SlaveRpcAdapter createSlaveAdapter() {
        return new SlaveRpcAdapter(new InputSlaveAdapter(ip, port, group, key, url, slaveCallbacks));
    }

    public synchronized void addSlaveAdapter(SlaveRpcAdapter adapter) {
        if (slaveAdapterAdds == null) {
            slaveAdapterAdds = new ArrayList<SlaveRpcAdapter>();
        }

        adapter.slaveIndex = slaveAdapters.size() + slaveAdapterAdds.size();
        slaveAdapterAdds.add(adapter);
    }

    /**
     * 返回注册字符
     */
    public byte[] registerData(InputSlaveAdapter adapter, byte[] buffer) {
        String registerKey = HelperEncrypt.encryptionMD5(adapter.getKey(), buffer) + ',' + adapter.getGroup() + ',' + RouteAdapter.ADAPTER_TIME;
        return adapter.sendDataBytes(registerKey.getBytes(), false, false, 0, null);
    }

    public boolean isRegisterData(InputSlaveAdapter adapter, byte[] buffer) {
        return Arrays.equals(buffer, SocketAdapter.ok);
    }

    /**
     * 初始化服务
     */
    @Inject
    public void inject() {
        slaveAdapter = createSlaveAdapter();
        addSlaveAdapter(slaveAdapter);
    }

    protected void syncAdapters() {
        if (slaveAdapterAdds != null) {
            synchronized (this) {
                if (slaveAdapterAdds != null) {
                    List<SlaveRpcAdapter> adapters = new ArrayList<SlaveRpcAdapter>(slaveAdapters);
                    adapters.addAll(slaveAdapterAdds);
                    slaveAdapterAdds = null;
                    slaveAdapters = adapters;
                }
            }
        }
    }

    /**
     * 连接服务器
     */
    @Schedule(fixedDelay = 60000, initialDelay = 15000)
    public void connectAdapters() {
        if (Environment.isActive()) {
            syncAdapters();
            for (RpcSocketAdapter<InputSlaveAdapter> slaveAdapter : slaveAdapters) {
                SocketAdapter adapter = slaveAdapter.getSocketAdapter();
                adapter.clearRetryConnect();
                adapter.connect();
            }
        }
    }

    /**
     * 关闭服务
     */
    @Stopping
    public void closeAdapters() {
        syncAdapters();
        for (RpcSocketAdapter<InputSlaveAdapter> slaveAdapter : slaveAdapters) {
            SocketAdapter adapter = slaveAdapter.getSocketAdapter();
            adapter.disconnect(null);
        }
    }

    protected void resolverRpcData(RpcInterface.RpcAttribute attribute, RpcData rpcData, int rpcIndex) {
    }

}
