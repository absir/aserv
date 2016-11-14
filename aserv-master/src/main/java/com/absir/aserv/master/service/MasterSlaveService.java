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
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Value;
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
import com.absir.server.socket.SocketServerContext.ChannelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
@Base
@Bean
public abstract class MasterSlaveService implements IEntityMerge<JSlaveServer> {

    public static final MasterSlaveService ME = BeanFactoryUtils.get(MasterSlaveService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(MasterSlaveService.class);
    @Value("master.sync.timeout")
    private int syncTimeout = 60000;

    public static <T extends JbBeanTargets> void addEntityMerge(Class<T> entityClass) {
        L2CacheCollectionService.ME.addEntityMerges(entityClass, new IEntityMerge<T>() {

            @Override
            public void merge(String entityName, T entity,
                              com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType, Object mergeEvent) {
                MasterSlaveService.ME.addSlaveSynch(entity.getTargets(), entityName + "@" + entity.getId(),
                        "api/slave/option/" + entityName + "/" + (mergeType == MergeType.DELETE ? 2 : 0), entity);
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
    public void addSlaveSynch(long[] eids, String mid, String uri, Object postData) {
        if (eids == null || eids.length == 0) {
            addSlaveSynch("*", mid, uri, postData);

        } else {
            for (String eid : ME.getTargetIds(eids)) {
                addSlaveSynch(eid, mid, uri, postData);
            }
        }
    }

    /**
     * 添加同步
     */
    @Transaction
    public boolean addSlaveSynch(String eid, String mid, String uri, Object postData) {
        return addSlaveSynch(eid, mid, uri, postData,
                postData == null || postData instanceof ISlaveAutoSynch ? true : false);
    }

    /**
     * 添加同步
     */
    @Transaction
    public boolean addSlaveSynch(String eid, String mid, String uri, Object postData, boolean autoSynch) {
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
        try {
            slaveSynch.setPostData(postData == null ? null
                    : postData.getClass() == byte[].class ? (byte[]) postData : HelperDataFormat.PACK.writeAsBytes(postData));
            BeanDao.getSession().merge(slaveSynch);
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
            addSlaveSynch(slave.getId(), slaveSynch.getId().getMid(), slaveSynch.getUri(), slaveSynch.getPostData());
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
            addSlaveSynch(id, slaveSynch.getId().getMid(), slaveSynch.getUri(), slaveSynch.getPostData());
        }
    }

    /**
     * 服务区同步完成
     */
    @Transaction
    public void syncComplete(JSlaveSynch slaveSynch, long updateTime) {
        QueryDaoUtils.createQueryArray(BeanDao.getSession(),
                "UPDATE JSlaveSynch o SET o.synched = ? WHERE o.id.eid = ? AND o.id.mid= ? AND o.updateTime = ?", true,
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
//                    MasterServerResolver.ME.sendDataBytesVarints(context, slaveSynch.getUri(),
//                            slaveSynch.getPostData(), syncTimeout, new CallbackMsg<String>() {
//
//                                @Override
//                                public void doWithBean(String bean, boolean ok, byte[] buffer, SocketAdapter adapter) {
//                                    try {
//                                        if (ok) {
//                                            ME.syncComplete(slaveSynch, updateTime);
//                                        }
//
//                                    } finally {
//                                        atom.decrement();
//                                    }
//                                }
//
//                            });

                } catch (Exception e) {
                    atom.decrement();
                    LOGGER.error("checkSyncs " + context + " " + slaveSynch.getId() + " " + slaveSynch.getUri() + " => "
                            + slaveSynch.getPostData(), e);
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
                ChannelContext context = InputMasterContext.ME.getServerContext().getChannelContexts()
                        .get(host.getId());
                if (context != null) {
                    try {
                        atom.increment();
                        final long updateTime = slaveServer.getUpdateTime();
//                        MasterServerResolver.ME.sendData(context.getChannel(), "api/slave/sync", slaveServer,
//                                new CallbackMsg<String>() {
//
//                                    @Override
//                                    public void doWithBean(String bean, boolean ok, byte[] buffer,
//                                                           SocketAdapter adapter) {
//                                        try {
//                                            if (ok) {
//                                                syncComplete(slaveServer, updateTime);
//                                            }
//
//                                        } finally {
//                                            atom.decrement();
//                                        }
//                                    }
//
//                                });

                    } catch (Exception e) {
                        atom.decrement();
                        LOGGER.error("syncServers " + context + " " + host.getId() + " => " + slaveServer, e);
                    }
                }
            }
        }

        atom.await();
    }

}
