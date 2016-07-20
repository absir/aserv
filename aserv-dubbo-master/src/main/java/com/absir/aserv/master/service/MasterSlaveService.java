package com.absir.aserv.master.service;

import com.absir.aserv.master.bean.JSlave;
import com.absir.aserv.system.domain.DCache;
import com.absir.aserv.system.domain.DCacheEntity;
import com.absir.aserv.system.service.BeanService;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.context.core.ContextUtils;
import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.registry.NotifyListener;
import com.alibaba.dubbo.registry.RegistryService;
import org.hibernate.exception.ConstraintViolationException;

import java.util.*;

/**
 * Created by absir on 16/3/23.
 */
@Base
@Bean
public class MasterSlaveService implements NotifyListener {

    public static final MasterSlaveService ME = BeanFactoryUtils.get(MasterSlaveService.class);
    protected Map<String, Object> slaveInterfaceMapService = new HashMap<String, Object>();
    @Inject
    private RegistryService registryService;
    private DCache<JSlave, JSlave> slaveDCache;

    public RegistryService getRegistryService() {
        return registryService;
    }

    public DCache<JSlave, JSlave> getSlaveDCache() {
        return slaveDCache;
    }

    @Inject
    protected void injectService() {
        registryService.subscribe(new URL(Constants.ADMIN_PROTOCOL, NetUtils.getLocalHost(), 0, "",
                Constants.INTERFACE_KEY, Constants.ANY_VALUE,
                Constants.GROUP_KEY, "slave",
                Constants.VERSION_KEY, Constants.ANY_VALUE,
                Constants.CLASSIFIER_KEY, Constants.ANY_VALUE,
                Constants.CATEGORY_KEY, Constants.CONSUMERS_CATEGORY,
                Constants.ENABLED_KEY, Constants.ANY_VALUE,
                Constants.CHECK_KEY, String.valueOf(false)), ME);

        slaveDCache = new DCacheEntity<JSlave>(JSlave.class, null);
    }

    @Override
    public void notify(List<URL> list) {
        Set<String> addresses = new HashSet<String>();
        for (URL url : list) {
            addresses.add(url.getAddress());
        }

        updateSlaves(addresses);
    }

    protected void updateSlaves(Set<String> addresses) {
        long contextTime = ContextUtils.getContextTime();
        for (String address : addresses) {
            JSlave slave = (JSlave) BeanService.ME.selectQuerySingle("SELECT o FROM JSlave o WHERE o.address = ?", address);
            if (slave == null) {
                slave = new JSlave();
                try {
                    slave.setAddress(address);
                    BeanService.ME.persist(slave);

                } catch (ConstraintViolationException e) {
                    continue;
                }
            }

            connect(slave, contextTime);
        }

        BeanService.ME.executeUpdate("UPDATE JSlave o SET o.connecting = ? WHERE o.lastConnectTime < ?", false, contextTime);
    }

    protected void connect(JSlave slave, long contextTime) {
        slave.setConnecting(true);
        slave.setLastConnectTime(contextTime);
        BeanService.ME.merge(slave);
    }

    public <T> T getSlaveReferenceService(JSlave slave, Class<T> serviceInterface) {
        String id = slave.getAddress() + serviceInterface.getName();
        T service = (T) slaveInterfaceMapService.get(id);
        if (service == null) {
            synchronized (slaveInterfaceMapService) {
                service = (T) slaveInterfaceMapService.get(id);
                if (service == null) {
                    ReferenceConfig<T> reference = new ReferenceConfig<T>();
                    reference.setInterface(serviceInterface);
                    reference.setUrl(slave.getAddress());
                    service = reference.get();
                    slaveInterfaceMapService.put(id, service);
                }
            }
        }

        return service;
    }
}
