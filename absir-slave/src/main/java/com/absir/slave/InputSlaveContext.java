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
import com.absir.slave.resolver.ISlaveCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author absir
 */
@Base
@Bean
public class InputSlaveContext {

    /**
     * ME
     */
    public static final InputSlaveContext ME = BeanFactoryUtils.get(InputSlaveContext.class);

    /**
     * LOGGER
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(InputSlaveContext.class);

    /**
     * ip
     */
    @Value("server.slave.ip")
    protected String ip;

    /**
     * port
     */
    @Value("server.slave.port")
    protected int port = 28890;

    /**
     * slave
     */
    @Value("server.slave.group")
    protected String group = "0";

    /**
     * key
     */
    @Value("server.slave.key")
    protected String key = "absir@qq.com";

    /**
     * url
     */
    @Value("server.slave.url")
    protected String url;

    /**
     * slaveKey
     */
    protected String slaveKey;

    /**
     * slaveCallbacks
     */
    @Inject(type = InjectType.Selectable)
    @Orders
    protected ISlaveCallback[] slaveCallbacks;

    /**
     * slaveAdapter
     */
    protected InputSlaveAdapter slaveAdapter;

    /**
     * slaveAdapters
     */
    protected List<InputSlaveAdapter> slaveAdapters = new ArrayList<InputSlaveAdapter>();

    /**
     * slaveAdapterAdds
     */
    protected List<InputSlaveAdapter> slaveAdapterAdds;

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the slaveKey
     */
    public String getSlaveKey() {
        return slaveKey;
    }

    /**
     * @return the slaveAdapter
     */
    public InputSlaveAdapter getSlaveAdapter() {
        return slaveAdapter;
    }

    /**
     * @param index
     * @return
     */
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

    /**
     * @param adapter
     */
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
        String registerKey = HelperEncrypt.encryptionMD5(key, buffer) + ',' + group;
        return adapter.sendDataBytes(registerKey.getBytes(), false, false, 0, null);
    }

    /**
     * @param adapter
     * @param buffer
     * @return
     */
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

    /**
     *
     */
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
