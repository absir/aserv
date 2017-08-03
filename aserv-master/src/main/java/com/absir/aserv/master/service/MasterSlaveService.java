package com.absir.aserv.master.service;

import com.absir.aserv.master.bean.JSlave;
import com.absir.aserv.master.bean.JSlaveServer;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.master.InputMasterContext;
import com.absir.master.MasterRpcAdapter;
import com.absir.orm.transaction.value.Transaction;

/**
 * Created by absir on 2/8/17.
 */
@Base
@Bean
public class MasterSlaveService {

    public static final MasterSlaveService ME = BeanFactoryUtils.get(MasterSlaveService.class);

    @Transaction(readOnly = true)
    public MasterRpcAdapter getMasterRpcAdapter(Long serverId) {
        JSlaveServer slaveServer = BeanDao.get(BeanDao.getSession(), JSlaveServer.class, serverId);
        if (slaveServer != null) {
            JSlave slave = slaveServer.getSlave();
            if (slave != null) {
                return InputMasterContext.ME.getMasterRpcAdapter(slave.getId());
            }
        }

        return null;
    }

}
