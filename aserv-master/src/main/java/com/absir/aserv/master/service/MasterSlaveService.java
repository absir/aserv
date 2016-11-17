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
import com.absir.aserv.master.bean.base.JbBeanTargets;
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
public abstract class MasterSlaveService implements IEntityMerge<JSlaveServer> {

    public static final MasterSlaveService ME = BeanFactoryUtils.get(MasterSlaveService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(MasterSlaveService.class);
    @Value("master.sync.shared")
    private static boolean syncShared;
    @Value("master.sync.timeout")
    private int syncTimeout = 60000;

    public static <T extends JbBeanTargets> void addEntityMerge(Class<T> entityClass) {
        L2CacheCollectionService.ME.addEntityMerges(entityClass, new IEntityMerge<T>() {

            @Override
            public void merge(String entityName, T entity,
                              com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType, Object mergeEvent) {
                if (!syncShared && mergeType == MergeType.UPDATE && !entity.isAllTarget()) {
                    // slave分库状态，更新目标，数据同步
                    long[] lastEids = entity.getLastTargets();
                    if (lastEids == null || lastEids.length == 0) {
                        // 从全选选择状态更新不全部选择
                        MasterSlaveService.ME.addSlaveSynch(lastEids, true, entityName + "@" + entity.getId(),
                                "api/slave/option/" + entityName + "/2", entity, false);

                    } else {
                        // 比对选择状态变化
                        long[] eids = entity.getTargets();
                        if (eids != null && eids.length != 0) {
                            List<Long> deleteIds = new ArrayList<Long>();
                            for (long eid : lastEids) {
                                if (!HelperArray.contains(eids, eid)) {
                                    deleteIds.add(eid);
                                }
                            }

                            int size = deleteIds.size();
                            lastEids = new long[size];
                            for (int i = 0; i < size; i++) {
                                lastEids[i] = deleteIds.get(i);
                            }
                        }

                        // 从全选选择状态更新不全部选择
                        MasterSlaveService.ME.addSlaveSynch(lastEids, false, entityName + "@" + entity.getId(),
                                "api/slave/option/" + entityName + "/2", entity, false);
                    }
                }

                MasterSlaveService.ME.addSlaveSynch(entity.getTargets(), entity.isAllTarget(), entityName + "@" + entity.getId(),
                        "api/slave/option/" + entityName + "/" + (mergeType == MergeType.DELETE ? 2 : 0), entity, false);
            }

        });
    }

    @DataQuery("SELECT o.host.id FROM JSlaveServer o WHERE o.id IN (:p0)")
    public abstract String[] getTargetIds(long[] eids);

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
    public void addSlaveSynch(long[] eids, boolean all, String mid, String uri, Object postData, boolean varints) {
        if (all || eids == null || eids.length == 0) {
            if (all) {
                addSlaveSynch("*", mid, uri, postData, varints);
            }

        } else {
            for (String eid : ME.getTargetIds(eids)) {
                addSlaveSynch(eid, mid, uri, postData, varints);
            }
        }
    }

    /**
     * 添加同步
     */
    @Transaction
    public boolean addSlaveSynch(String eid, String mid, String uri, Object postData, boolean varints) {
        return addSlaveSynch(eid, mid, uri, postData, varints,
                postData == null || postData instanceof ISlaveAutoSynch ? true : false);
    }

    /**
     * 添加同步
     */
    @Transaction
    public boolean addSlaveSynch(String eid, String mid, String uri, Object postData, boolean varints, boolean autoSynch) {
        try {
            return addSlaveSynchData(eid, mid, uri, postData == null ? null
                    : postData.getClass() == byte[].class ? (byte[]) postData : HelperDataFormat.PACK.writeAsBytes(postData), varints, autoSynch);

        } catch (Exception e) {
            LOGGER.error("addSlaveSynch failed " + uri + " => " + postData, e);
        }

        return false;
    }

    @Transaction
    public boolean addSlaveSynchData(String eid, String mid, String uri, byte[] postData, boolean varints, boolean autoSynch) {
        if (KernelString.isEmpty(eid)) {
            eid = "*";
        }

        JEmbedSS embedSS = new JEmbedSS();
        embedSS.setEid(eid);
        embedSS.setMid(mid);
        JSlaveSynch slaveSynch = new JSlaveSynch();
        slaveSynch.setId(embedSS);
        slaveSynch.setUri(uri);
        slaveSynch.setUpdateTime(System.currentTimeMillis());
        slaveSynch.setSynched("*".equals(eid));
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
    public void addSlaveSynchRpc(String channelId, String mid, RpcData rpcData, boolean autoSynch) {
        addSlaveSynchData(channelId, mid, rpcData.getUri(), rpcData.getParamData(), true, autoSynch);
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
        }

        List<JSlaveServer> slaveServers = ME.getNoSyncServers();
        final UtilAtom atom = new UtilAtom();
        for (final JSlaveServer slaveServer : slaveServers) {
            JSlave host = slaveServer.getHost();
            if (host != null) {
                MasterChannelContext context = InputMasterContext.ME.getServerContext().getChannelContexts()
                        .get(host.getId());
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
                        LOGGER.error("syncServers " + context + " " + host.getId() + " => " + slaveServer, e);
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
