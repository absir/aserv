/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月13日 下午3:16:24
 */
package com.absir.aserv.master.service;

import com.absir.aserv.data.value.DataQuery;
import com.absir.aserv.master.bean.JSlave;
import com.absir.aserv.master.bean.JSlaveServer;
import com.absir.aserv.master.bean.JSlaveSynch;
import com.absir.aserv.master.bean.base.ISlaveAutoSynch;
import com.absir.aserv.master.bean.base.JbBeanServers;
import com.absir.aserv.master.bean.base.JbBeanSlaves;
import com.absir.aserv.system.bean.JEmbedSS;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.helper.HelperArray;
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Value;
import com.absir.client.SocketAdapter;
import com.absir.client.callback.CallbackMsg;
import com.absir.client.rpc.RpcData;
import com.absir.context.core.ContextUtils;
import com.absir.context.schedule.value.Schedule;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAbsir;
import com.absir.core.util.UtilAtom;
import com.absir.data.helper.HelperDataFormat;
import com.absir.master.InputMasterContext;
import com.absir.master.MasterChannelContext;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.hibernate.boost.L2CacheCollectionService;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
@Base
@Bean
public abstract class MasterSyncService implements IEntityMerge<JSlaveServer> {

    public static final MasterSyncService ME = BeanFactoryUtils.get(MasterSyncService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(MasterSyncService.class);
    @Value("master.sync.shared")
    private static boolean syncShared;
    @Value("master.sync.timeout")
    private int syncTimeout = 60000;

    public static <T extends JbBeanServers> void addSyncEntityServers(Class<T> entityClass) {
        L2CacheCollectionService.ME.addEntityMerges(entityClass, new IEntityMerge<T>() {

            @Override
            public void merge(String entityName, T entity,
                              com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType, Object mergeEvent) {
                if (!syncShared && mergeType == MergeType.UPDATE && !entity.isAllServerIds()) {
                    // syncShared分库状态，更新目标，数据同步
                    long[] lastServerIds = entity.getLastServerIds();
                    if (entity.getLastAllServerIds() == 1 || lastServerIds == null || lastServerIds.length == 0) {
                        // 从全部选择状态更新到不全部选择
                        MasterSyncService.ME.addSlaveSynchServerIds(lastServerIds, true, entityName + "@" + entity.getId(),
                                "api/slave/option/" + entityName + "/2", entity, false);

                    } else {
                        // 比对选择状态变化
                        long[] serverIds = entity.getServerIds();
                        if (serverIds != null && serverIds.length != 0) {
                            List<Long> deleteIds = new ArrayList<Long>();
                            for (long serverId : lastServerIds) {
                                if (!HelperArray.contains(serverIds, serverId)) {
                                    deleteIds.add(serverId);
                                }
                            }

                            int size = deleteIds.size();
                            lastServerIds = new long[size];
                            for (int i = 0; i < size; i++) {
                                lastServerIds[i] = deleteIds.get(i);
                            }
                        }

                        // 从全选选择状态更新不全部选择
                        MasterSyncService.ME.addSlaveSynchServerIds(lastServerIds, false, entityName + "@" + entity.getId(),
                                "api/slave/option/" + entityName + "/2", entity, false);
                    }
                }

                MasterSyncService.ME.addSlaveSynchServerIds(entity.getServerIds(), entity.isAllServerIds(), entityName + "@" + entity.getId(),
                        "api/slave/option/" + entityName + "/" + (mergeType == MergeType.DELETE ? 2 : 1), entity, false);
            }

        });
    }

    public static <T extends JbBeanSlaves> void addSyncEntitySlaves(Class<T> entityClass) {
        L2CacheCollectionService.ME.addEntityMerges(entityClass, new IEntityMerge<T>() {

            @Override
            public void merge(String entityName, T entity,
                              com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType, Object mergeEvent) {
                if (!syncShared && mergeType == MergeType.UPDATE && !entity.isAllSlaveIds()) {
                    // syncShared分库状态，更新目标，数据同步
                    String[] lastSlaveIds = entity.getLastSlaveIds();
                    if (entity.getLastAllSlaveIds() == 1 || lastSlaveIds == null || lastSlaveIds.length == 0) {
                        // 从全部选择状态更新到不全部选择
                        MasterSyncService.ME.addSlaveSynchSlaveIds(lastSlaveIds, true, entityName + "@" + entity.getId(),
                                "api/slave/option/" + entityName + "/2", entity, false);

                    } else {
                        // 比对选择状态变化
                        String[] slaveIds = entity.getSlaveIds();
                        if (slaveIds != null && slaveIds.length != 0) {
                            List<String> deleteIds = new ArrayList<String>();
                            for (String slaveId : lastSlaveIds) {
                                if (!HelperArray.contains(slaveIds, slaveId)) {
                                    deleteIds.add(slaveId);
                                }
                            }

                            int size = deleteIds.size();
                            lastSlaveIds = new String[size];
                            for (int i = 0; i < size; i++) {
                                lastSlaveIds[i] = deleteIds.get(i);
                            }
                        }

                        // 从全选选择状态更新不全部选择
                        MasterSyncService.ME.addSlaveSynchSlaveIds(lastSlaveIds, false, entityName + "@" + entity.getId(),
                                "api/slave/option/" + entityName + "/2", entity, false);
                    }
                }

                MasterSyncService.ME.addSlaveSynchSlaveIds(entity.getSlaveIds(), entity.isAllSlaveIds(), entityName + "@" + entity.getId(),
                        "api/slave/option/" + entityName + "/" + (mergeType == MergeType.DELETE ? 2 : 1), entity, false);
            }

        });
    }

    @DataQuery("SELECT o.slave.id FROM JSlaveServer o WHERE o.id IN (:p0)")
    public abstract String[] getSlaveIds(long[] serverIds);

    @Transaction
    @Inject
    public void initService() {
        QueryDaoUtils.createQueryArray(BeanDao.getSession(), "UPDATE JSlave o SET o.connecting = FALSE")
                .executeUpdate();
    }

    /**
     * 添加同步
     */
    @Transaction
    public void addSlaveSynchServerIds(long[] serverIds, boolean all, String mid, String uri, Object postData, boolean varints) {
        if (all) {
            addSlaveSynch("*", mid, uri, postData, varints);

        } else {
            if (serverIds != null && serverIds.length > 0) {
                for (String slaveId : ME.getSlaveIds(serverIds)) {
                    addSlaveSynch(slaveId, mid, uri, postData, varints);
                }
            }
        }
    }

    @Transaction
    public void addSlaveSynchSlaveIds(String[] slaveIds, boolean all, String mid, String uri, Object postData, boolean varints) {
        if (all) {
            addSlaveSynch("*", mid, uri, postData, varints);

        } else {
            if (slaveIds != null && slaveIds.length > 0) {
                for (String slaveId : slaveIds) {
                    addSlaveSynch(slaveId, mid, uri, postData, varints);
                }
            }
        }
    }

    /**
     * 添加同步
     */
    @Transaction
    public boolean addSlaveSynch(String slaveId, String mid, String uri, Object postData, boolean varints) {
        return addSlaveSynch(slaveId, mid, uri, postData, varints,
                postData == null || postData instanceof ISlaveAutoSynch ? true : false);
    }

    /**
     * 添加同步
     */
    @Transaction
    public boolean addSlaveSynch(String slaveId, String mid, String uri, Object postData, boolean varints, boolean autoSynch) {
        try {
            return addSlaveSynchData(slaveId, mid, uri, postData == null ? null
                    : postData.getClass() == byte[].class ? (byte[]) postData : HelperDataFormat.PACK.writeAsBytes(postData), varints, autoSynch);

        } catch (Exception e) {
            LOGGER.error("addSlaveSynch failed " + uri + " => " + postData, e);
        }

        return false;
    }

    @Transaction
    public boolean addSlaveSynchData(String slaveId, String mid, String uri, byte[] postData, boolean varints, boolean autoSynch) {
        if (KernelString.isEmpty(slaveId)) {
            slaveId = "*";
        }

        JEmbedSS embedSS = new JEmbedSS();
        embedSS.setEid(slaveId);
        embedSS.setMid(mid);
        JSlaveSynch slaveSynch = new JSlaveSynch();
        slaveSynch.setId(embedSS);
        slaveSynch.setUri(uri);
        slaveSynch.setUpdateTime(System.currentTimeMillis());
        slaveSynch.setSynched("*".equals(slaveId));
        slaveSynch.setSlaveAutoSynch(autoSynch);
        slaveSynch.setPostData(postData);
        try {
            Session session = BeanDao.getSession();
            session.merge(slaveSynch);
            session.flush();
            if (slaveSynch.isSynched()) {
                ME.addSlaveSynch(slaveSynch);

            } else {
                ME.checkSyncs();
            }

            return true;

        } catch (Exception e) {
            LOGGER.error("addSlaveSynch failed " + uri + " => " + postData, e);
        }

        return false;
    }

    // 添加RPC同步
    public void addSlaveSynchRpc(String slaveId, String mid, RpcData rpcData, boolean autoSynch) {
        addSlaveSynchData(slaveId, mid, rpcData.getUri(), rpcData.getParamData(), true, autoSynch);
    }

    /**
     * 添加通用同步
     */
    @Async
    @Transaction
    protected void addSlaveSynch(JSlaveSynch slaveSynch) {
        Iterator<JSlave> iterator = QueryDaoUtils.createQueryArray(BeanDao.getSession(), "SELECT o FROM JSlave o")
                .iterate();
        while (iterator.hasNext()) {
            JSlave slave = iterator.next();
            addSlaveSynch(slave.getId(), slaveSynch.getId().getMid(), slaveSynch.getUri(), slaveSynch.getPostData(), slaveSynch.isVarints());
        }
    }

    /**
     * 检查通用同步
     */
    @Async
    @Transaction
    public void checkSlaveSynch(JSlave slave) {
        Iterator<JSlaveSynch> iterator = QueryDaoUtils.createQueryArray(BeanDao.getSession(),
                "SELECT o FROM JSlaveSynch o WHERE o.id.eid = ? AND o.slaveAutoSynch = ?", "*", true).iterate();
        String id = slave.getId();
        while (iterator.hasNext()) {
            JSlaveSynch slaveSynch = iterator.next();
            addSlaveSynch(id, slaveSynch.getId().getMid(), slaveSynch.getUri(), slaveSynch.getPostData(), slaveSynch.isVarints());
        }
    }

    /**
     * 服务区同步完成
     */
    @Transaction
    public void syncComplete(JSlaveSynch slaveSynch, long updateTime) {
        QueryDaoUtils.createQueryArray(BeanDao.getSession(),
                "UPDATE JSlaveSynch o SET o.synched = ? WHERE o.id.eid = ? AND o.id.mid = ? AND o.updateTime = ?", true,
                slaveSynch.getId().getEid(), slaveSynch.getId().getMid(), updateTime).executeUpdate();
    }

    /**
     * 检查数据同步
     */
    @Async(notifier = true)
    @Transaction(readOnly = true)
    @Schedule(fixedDelay = 300000, initialDelay = 20000)
    public void checkSyncs() {
        try {
            Thread.sleep(3000);

        } catch (Exception e) {
            return;
        }

        Iterator<JSlaveSynch> iterator = QueryDaoUtils
                .createQueryArray(BeanDao.getSession(), "SELECT o FROM JSlaveSynch o WHERE o.synched = ?", false)
                .iterate();
        final UtilAtom atom = new UtilAtom();
        while (iterator.hasNext()) {
            final JSlaveSynch slaveSynch = iterator.next();
            MasterChannelContext context = InputMasterContext.ME.getServerContext().getChannelContexts()
                    .get(slaveSynch.getId().getEid());
            if (context != null) {
                try {
                    atom.increment();
                    final long updateTime = slaveSynch.getUpdateTime();
                    CallbackMsg<String> callbackMsg = new CallbackMsg<String>() {

                        @Override
                        public void doWithBean(String bean, boolean ok, byte[] buffer, SocketAdapter adapter) {
                            try {
                                if (ok) {
                                    ME.syncComplete(slaveSynch, updateTime);
                                }

                            } finally {
                                atom.decrement();
                            }
                        }

                    };

                    if (slaveSynch.isVarints()) {
                        context.getMasterChannelAdapter().sendDataVarints(slaveSynch.getUri(),
                                slaveSynch.getPostData(), syncTimeout, callbackMsg);

                    } else {
                        context.getMasterChannelAdapter().sendData(slaveSynch.getUri(),
                                slaveSynch.getPostData(), syncTimeout, callbackMsg);
                    }

                } catch (Exception e) {
                    atom.decrement();
                    LOGGER.error("checkSyncs " + context + " " + slaveSynch.getId() + " " + slaveSynch.getUri() + " => "
                            + slaveSynch.getPostData(), e);
                }
            }
        }

        atom.await();
    }

    @Transaction
    @DataQuery("SELECT o FROM JSlaveServer o WHERE o.synched = TRUE ORDER BY o.id DESC")
    public abstract List<JSlaveServer> getServers();

    @Override
    public void merge(String entityName, JSlaveServer entity,
                      com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType, Object mergeEvent) {
        if (mergeType != MergeType.DELETE) {
            syncChange(entity);
            ME.syncServers();
        }
    }

    @Transaction
    @DataQuery("SELECT o FROM JSlaveServer o WHERE o.synched = FALSE")
    public abstract List<JSlaveServer> getNoSyncServers();

    /**
     * 服务区同步变化
     */
    @Transaction
    public void syncChange(JSlaveServer slaveServer) {
        QueryDaoUtils.createQueryArray(BeanDao.getSession(),
                "UPDATE JSlaveServer o SET o.synched = ?, o.updateTime = ? WHERE o.id = ?", false,
                System.currentTimeMillis(), slaveServer.getId()).executeUpdate();
    }

    /**
     * 服务区同步完成
     */
    @Transaction
    public void syncComplete(JSlaveServer slaveServer, long updateTime) {
        QueryDaoUtils.createQueryArray(BeanDao.getSession(),
                "UPDATE JSlaveServer o SET o.synched = ? WHERE o.id = ? AND o.updateTime = ?", true,
                slaveServer.getId(), updateTime).executeUpdate();
    }

    /**
     * 同步服务区数据
     */
    @Async(notifier = true)
    @Transaction(readOnly = true)
    @Schedule(fixedDelay = 300000, initialDelay = 20000)
    public void syncServers() {
        try {
            Thread.sleep(3000);

        } catch (Exception e) {
            return;
        }

        List<JSlaveServer> slaveServers = ME.getNoSyncServers();
        final UtilAtom atom = new UtilAtom();
        for (final JSlaveServer slaveServer : slaveServers) {
            JSlave slave = slaveServer.getSlave();
            if (slave != null) {
                MasterChannelContext context = InputMasterContext.ME.getServerContext().getChannelContexts()
                        .get(slave.getId());
                if (context != null) {
                    try {
                        atom.increment();
                        final long updateTime = slaveServer.getUpdateTime();
                        context.getMasterChannelAdapter().sendData("api/slave/sync", slaveServer, syncTimeout,
                                new CallbackMsg<String>() {

                                    @Override
                                    public void doWithBean(String bean, boolean ok, byte[] buffer,
                                                           SocketAdapter adapter) {
                                        try {
                                            if (ok) {
                                                syncComplete(slaveServer, updateTime);
                                            }

                                        } finally {
                                            atom.decrement();
                                        }
                                    }

                                });

                    } catch (Exception e) {
                        atom.decrement();
                        LOGGER.error("syncServers " + context + " " + slave.getId() + " => " + slaveServer, e);
                    }
                }
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
                "DELETE FROM JSlaveSynch o WHERE o.id.mid != ? AND o.synched = ? AND o.updateTime < ?", "*", true,
                ContextUtils.getContextTime() - UtilAbsir.WEEK_TIME).executeUpdate();
    }

}
