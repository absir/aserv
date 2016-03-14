/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月16日 下午9:18:08
 */
package com.absir.aserv.slave.service;

import com.absir.aserv.configure.JConfigureUtils;
import com.absir.aserv.init.InitBeanFactory;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.client.helper.HelperEncrypt;
import com.absir.slave.InputSlaveAdapter;
import com.absir.slave.InputSlaveContext;

/**
 * @author absir
 *
 */
@Base
@Bean
public class InputSlaveService extends InputSlaveContext {

    /**
     * SLAVE_ID_NAME
     */
    protected static final String SLAVE_ID_NAME = InputSlaveService.class.getName() + "@SLAVE_ID_NAME";

    /**
     * slaveId
     */
    protected String slaveId = JConfigureUtils.getOption(SLAVE_ID_NAME, String.class);

    /*
     * (non-Javadoc)
     *
     * @see com.absir.slave.InputSlaveContext#registerData(com.absir.slave.
     * InputSlaveAdapter, byte[])
     */
    @Override
    public byte[] registerData(InputSlaveAdapter adapter, byte[] buffer) {
        String registerKey = HelperEncrypt.encryptionMD5(key, buffer) + ',' + group + ','
                + InitBeanFactory.ME.getVersion() + ',' + InitBeanFactory.ME.getAppRoute() + ','
                + (slaveId == null ? "" : slaveId);
        return adapter.sendDataBytes(registerKey.getBytes(), false, false, 0, null);
    }

}
