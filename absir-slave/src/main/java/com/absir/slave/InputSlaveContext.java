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
    protected String ip;

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

    protected InputSlaveAdapter slaveAdapter;

    protected List<InputSlaveAdapter> slaveAdapters = new ArrayList<InputSlaveAdapter>();

    protected List<InputSlaveAdapter> slaveAdapterAdds;

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

    public String getSlaveKey() {
        return slaveKey;
    }

    public InputSlaveAdapter getSlaveAdapter() {
        return slaveAdapter;
    }

    public InputSlaveAdapter getSlaveAdapter(int index) {
        if (index == 0) {
            return slaveAdapter;
        }

        return index < slaveAdapters.size() ? slaveAdapters.get(index) : null;
    }

    /**
     * 创建
     *
     * @return
     */
    protected InputSlaveAdapter createSlaveAdapter() {
        return new InputSlaveAdapter(ip, port, group, key, url, slaveCallbacks);
    }

    public synchronized void addSlaveAdapter(InputSlaveAdapter adapter) {
        if (slaveAdapterAdds == null) {
            slaveAdapterAdds = new ArrayList<InputSlaveAdapter>();
        }

        slaveAdapterAdds.add(adapter);
    }

    /**
     * 返回注册字符
     *
     * @param adapter
     * @param buffer
     * @return
     */
    public byte[] registerData(InputSlaveAdapter adapter, byte[] buffer) {
        String registerKey = HelperEncrypt.encryptionMD5(key, buffer) + ',' + group + ',' + RouteAdapter.ADAPTER_TIME;
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
                    List<InputSlaveAdapter> adapters = new ArrayList<InputSlaveAdapter>(slaveAdapters);
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
            for (InputSlaveAdapter adapter : slaveAdapters) {
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
        for (InputSlaveAdapter adapter : slaveAdapters) {
            adapter.disconnect(null);
        }
    }

}
