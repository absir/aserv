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
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.context.core.ContextUtils;
import com.absir.core.base.Environment;
import com.absir.master.InputMasterContext;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;

import java.nio.channels.SocketChannel;

/**
 * @author absir
 *
 */
@Base
@Bean
public class InputMasterService extends InputMasterContext {

    /*
     * (non-Javadoc)
     *
     * @see com.absir.master.InputMasterContext#registerSlaveKey(java.io.
     * Serializable, byte[], java.lang.String, java.lang.String[],
     * java.nio.channels.SocketChannel)
     */
    @Transaction
    @Override
    public void registerSlaveKey(String id, byte[] secerets, String validate, String[] params,
                                 SocketChannel socketChannel) {
        super.registerSlaveKey(id, secerets, validate, params, socketChannel);
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

        if (params.length > 2) {
            slave.setVersion(params[2]);
        }

        if (params.length > 3) {
            slave.setPath(params[3]);
        }

        slave.setIp(socketChannel.socket().getInetAddress().getHostAddress());
        slave.setConnecting(true);
        slave.setLastConnectTime(ContextUtils.getContextTime());
        slave.setSlaveKey(validate);
        if (id == null) {
            session.persist(slave);
            MasterSlaveService.ME.checkSlaveSynch(slave);

        } else {
            session.merge(slave);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.master.InputMasterContext#unregisterSlaveKey(java.io.
     * Serializable, java.nio.channels.SocketChannel)
     */
    @Transaction
    @Override
    public MasterChannelContext unregisterSlaveKey(String id, SocketChannel socketChannel) {
        MasterChannelContext context = super.unregisterSlaveKey(id, socketChannel);
        if (context != null) {
            if (Environment.isStarted()) {
                Session session = BeanDao.getSession();
                JSlave slave = BeanDao.get(session, JSlave.class, id);
                if (slave != null) {
                    slave.setConnecting(false);
                    slave.setLastConnectTime(ContextUtils.getContextTime());
                    session.merge(slave);
                    session.flush();
                    if (InputMasterContext.ME.getSlaveKey(id) != null) {
                        // session.getTransaction().rollback();
                    }

                    session.evict(slave);
                }
            }
        }

        return context;
    }

}
