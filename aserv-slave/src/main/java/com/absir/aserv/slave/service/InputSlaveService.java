/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月16日 下午9:18:08
 */
package com.absir.aserv.slave.service;

import com.absir.aserv.init.InitBeanFactory;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.client.helper.HelperEncrypt;
import com.absir.server.route.RouteAdapter;
import com.absir.slave.InputSlaveAdapter;
import com.absir.slave.InputSlaveContext;

@Base
@Bean
public class InputSlaveService extends InputSlaveContext {

    @Override
    public byte[] registerData(InputSlaveAdapter adapter, byte[] buffer) {
        String group = InitBeanFactory.ME.getAppCode() + '_' + adapter.getGroup();
        String registerKey = HelperEncrypt.encryptionMD5(adapter.getKey(), buffer) + ',' + group + ',' + RouteAdapter.ADAPTER_TIME + ',' +
                InitBeanFactory.ME.getVersion() + ',' + InitBeanFactory.ME.getAppRoute() + ',' + InitBeanFactory.ME.getAppCode();
        return adapter.sendDataBytes(registerKey.getBytes(), false, false, 0, null);
    }

}
