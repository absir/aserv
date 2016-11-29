/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年6月15日 上午10:06:22
 */
package com.absir.aserv.slave.service;

import com.absir.aserv.slave.bean.JMasterSynch;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Value;
import com.absir.client.SocketAdapter;
import com.absir.client.callback.CallbackMsg;
import com.absir.client.rpc.RpcData;
import com.absir.context.core.ContextUtils;
import com.absir.context.schedule.value.Schedule;
import com.absir.core.util.UtilAbsir;
import com.absir.core.util.UtilAtom;
import com.absir.data.helper.HelperDataFormat;
import com.absir.orm.transaction.value.Transaction;
import com.absir.slave.InputSlaveAdapter;
import com.absir.slave.InputSlaveContext;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

@SuppressWarnings("unchecked")
@Base
@Bean
public class SlaverSyncService {

    public static final SlaverSyncService ME = BeanFactoryUtils.get(SlaverSyncService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(SlaverSyncService.class);

    @Value("slave.sync.timeout")
    private int syncTimeout = 60000;

    /**
     * 添加同步
     */
    @Transaction
    public boolean addMasterSynch(String id, String uri, Object postData) {
        return addMasterSynch(id, uri, postData, false);
    }

    @Transaction
    public boolean addMasterSynch(String id, String uri, Object postData, boolean varints) {
        return addMasterSynchIndex(0, id, uri, postData, varints);
    }

    @Transaction
    public boolean addMasterSynchIndex(int masterIndex, String id, String uri, Object postData, boolean varints) {
        try {
            return addMasterSynchDataIndex(masterIndex, id, uri, postData == null ? null
                    : postData.getClass() == byte[].class ? (byte[]) postData : HelperDataFormat.PACK.writeAsBytes(postData), varints);

        } catch (Exception e) {
            LOGGER.error("addMasterSynch failed " + uri + " => " + postData, e);
        }

        return false;
    }

    @Transaction
    public boolean addMasterSynchDataIndex(int masterIndex, String id, String uri, byte[] postData, boolean varints) {
        JMasterSynch masterSynch = new JMasterSynch();
        masterSynch.setId(id);
        masterSynch.setMasterIndex(masterIndex);
        masterSynch.setUri(uri);
        masterSynch.setUpdateTime(System.currentTimeMillis());
        masterSynch.setPostData(postData);
        try {
            Session session = BeanDao.getSession();
            session.merge(masterSynch);
            session.flush();
            ME.checkSyncs();
            return true;

        } catch (Exception e) {
            LOGGER.error("addMasterSynch failed " + uri + " => " + postData, e);
        }

        return false;
    }

    /*
     *添加RPC同步
     */
    public void addMasterSynchRpcIndex(int masterIndex, String id, RpcData rpcData) {
        addMasterSynchDataIndex(masterIndex, id, rpcData.getUri(), rpcData.getParamData(), true);
    }

    /**
     * 服务区同步完成
     */
    @Transaction
    public void syncComplete(JMasterSynch masterSynch, long updateTime) {
        QueryDaoUtils.createQueryArray(BeanDao.getSession(),
                "UPDATE JMasterSynch o SET o.synched = ? WHERE o.id = ? AND o.updateTime = ?", true,
                masterSynch.getId(), updateTime).executeUpdate();
    }

    /**
     * 检查数据同步
     */
    @Async(notifier = true)
    @Transaction(readOnly = true)
    @Schedule(fixedDelay = 300000, initialDelay = 20000)
    public void checkSyncs() {
        Iterator<JMasterSynch> iterator = QueryDaoUtils
                .createQueryArray(BeanDao.getSession(), "SELECT o FROM JMasterSynch o WHERE o.synched = ?", false)
                .iterate();
        final UtilAtom atom = new UtilAtom();
        while (iterator.hasNext()) {
            final JMasterSynch masterSynch = iterator.next();
            try {
                InputSlaveAdapter slaveAdapter = InputSlaveContext.ME.getSlaveAdapter(masterSynch.getMasterIndex()).getSocketAdapter();
                atom.increment();
                final long updateTime = masterSynch.getUpdateTime();
                CallbackMsg<String> callbackMsg = new CallbackMsg<String>() {

                    @Override
                    public void doWithBean(String bean, boolean ok, byte[] buffer, SocketAdapter adapter) {
                        try {
                            if (ok) {
                                ME.syncComplete(masterSynch, updateTime);
                            }

                        } finally {
                            atom.decrement();
                        }
                    }

                };

                if (masterSynch.isVarints()) {
                    slaveAdapter.sendDataVarints(masterSynch.getUri(),
                            masterSynch.getPostData(), syncTimeout, callbackMsg);

                } else {
                    slaveAdapter.sendData(masterSynch.getUri(),
                            masterSynch.getPostData(), syncTimeout, callbackMsg);
                }

            } catch (Exception e) {
                LOGGER.error("checkSyncs " + masterSynch.getId() + " " + masterSynch.getUri() + " => "
                        + masterSynch.getPostData(), e);
                atom.decrement();
            }
        }

        atom.await();
    }

    /**
     * 清除完成同步
     */
    @Async(notifier = true)
    @Transaction
    @Schedule(cron = "0 0 0 * * *")
    protected void clearSyncheds() {
        QueryDaoUtils.createQueryArray(BeanDao.getSession(),
                "DELETE FROM JMasterSynch o WHERE o.synched = ? AND o.updateTime < ?", true,
                ContextUtils.getContextTime() - UtilAbsir.WEEK_TIME).executeUpdate();
    }

}
