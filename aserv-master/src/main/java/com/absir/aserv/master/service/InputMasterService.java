/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月16日 下午6:03:50
 */
package com.absir.aserv.master.service;

import com.absir.aserv.master.bean.JSlave;
import com.absir.aserv.master.bean.JSlaveAppCode;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.service.BeanService;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelDyna;
import com.absir.master.InputMasterContext;
import com.absir.master.MasterChannelContext;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;

import java.nio.channels.SocketChannel;

@Base
@Bean
public class InputMasterService extends InputMasterContext {

    @Transaction
    @Override
    public synchronized void registerSlaveKey(String id, byte[] secrets, String validate, String[] params,
                                              SocketChannel socketChannel, long currentTime) {
        super.registerSlaveKey(id, secrets, validate, params, socketChannel, currentTime);
        Session session = BeanDao.getSession();
        JSlave slave = BeanDao.get(session, JSlave.class, id);
        if (slave == null) {
            slave = new JSlave();
            slave.setId(id);
            slave.setOpen(true);
            id = null;
        }

        if (params.length > 1) {
            slave.setGroupId(params[1]);
        }

        if (params.length > 2) {
            slave.setStartTime(KernelDyna.to(params[2], long.class));
        }

        if (params.length > 3) {
            slave.setVersion(params[3]);
        }

        if (params.length > 4) {
            slave.setPath(params[4]);
        }

        if (params.length > 5) {
            slave.setAppCode(params[5]);
        }

        if (params.length > 6) {
            slave.setServerPort(KernelDyna.to(params[6], int.class));
        }

        slave.setIp(socketChannel.socket().getInetAddress().getHostAddress());
        slave.setConnecting(true);
        slave.setLastConnectTime(currentTime);
        slave.setSlaveKey(validate);
        if (id == null) {
            session.persist(slave);
            MasterSyncService.ME.checkSlaveSynch(slave);

        } else {
            session.merge(slave);
        }

        JSlaveAppCode slaveAppCode = BeanDao.get(session, JSlaveAppCode.class, slave.getAppCode());
        if (slaveAppCode == null) {
            slaveAppCode = new JSlaveAppCode();
            slaveAppCode.setId(slave.getAppCode());
            slaveAppCode.setOpen(true);
            session.merge(slaveAppCode);
        }
    }

    @Override
    public MasterChannelContext unRegisterSlaveKey(String id, SocketChannel socketChannel, long currentTime) {
        MasterChannelContext context = super.unRegisterSlaveKey(id, socketChannel, currentTime);
        if (context != null) {
            if (Environment.isStarted()) {
                BeanService.ME.executeUpdate("UPDATE JSlave o SET o.connecting = ?, o.lastConnectTime = ? WHERE o.id = ? AND o.lastConnectTime < ?",
                        false, currentTime, id, currentTime);
            }
        }

        return context;
    }

}
