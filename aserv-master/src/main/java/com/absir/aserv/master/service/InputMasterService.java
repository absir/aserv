/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月16日 下午6:03:50
 */
package com.absir.aserv.master.service;

import com.absir.aserv.master.bean.JSlave;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.service.BeanService;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.core.base.Environment;
import com.absir.master.InputMasterContext;
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
            id = null;
        }

        if (params.length > 1) {
            slave.setGroupId(params[1]);
        }

        if (params.length > 3) {
            slave.setVersion(params[3]);
        }

        if (params.length > 4) {
            slave.setPath(params[4]);
        }

        slave.setIp(socketChannel.socket().getInetAddress().getHostAddress());
        slave.setConnecting(true);
        slave.setLastConnectTime(currentTime);
        slave.setSlaveKey(validate);
        if (id == null) {
            session.persist(slave);
            MasterSlaveService.ME.checkSlaveSynch(slave);

        } else {
            session.merge(slave);
        }
    }

    @Override
    public MasterChannelContext unRegisterSlaveKey(String id, SocketChannel socketChannel, long currentTime) {
        MasterChannelContext context = super.unRegisterSlaveKey(id, socketChannel, currentTime);
        if (context != null) {
            if (Environment.isStarted()) {
                BeanService.ME.executeUpdate("UPDATE JSlave o SET o.connecting = ? AND o.lastConnectTime = ? WHERE o.id = ? AND o.lastConnectTime < ?",
                        false, currentTime, id, currentTime);
            }
        }

        return context;
    }

}
