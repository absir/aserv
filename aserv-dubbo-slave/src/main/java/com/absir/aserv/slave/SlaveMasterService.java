package com.absir.aserv.slave;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Value;
import com.absir.context.core.ContextService;
import com.absir.context.core.ContextUtils;
import com.absir.core.base.LBean;
import com.alibaba.dubbo.config.ReferenceConfig;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by absir on 16/5/25.
 */
@Base
@Bean
public class SlaveMasterService extends ContextService {

    @Value("slave.service.expireTime")
    protected long expireTime = 1800000;

    protected Map<String, LBean<Object>> slaveInterfaceMapService = new ConcurrentHashMap<String, LBean<Object>>();

    @Override
    public void step(long contextTime) {
        Iterator<Map.Entry<String, LBean<Object>>> iterator = slaveInterfaceMapService.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, LBean<Object>> entry = iterator.next();
            if (entry.getValue().l < contextTime) {
                iterator.remove();
            }
        }
    }

    public <T> T getSlaveReferenceService(String address, Class<T> serviceInterface) {
        String id = address + serviceInterface.getName();
        LBean<Object> lBean = (LBean<Object>) slaveInterfaceMapService.get(id);
        if (lBean == null) {
            synchronized (slaveInterfaceMapService) {
                lBean = (LBean<Object>) slaveInterfaceMapService.get(id);
                if (lBean == null) {
                    ReferenceConfig<T> reference = new ReferenceConfig<T>();
                    reference.setInterface(serviceInterface);
                    reference.setUrl(address);
                    T service = reference.get();
                    lBean = new LBean<Object>();
                    lBean.l = ContextUtils.getContextTime() + expireTime;
                    lBean.bean = service;
                    slaveInterfaceMapService.put(id, lBean);
                }
            }
        }

        return (T) lBean.bean;
    }

}
