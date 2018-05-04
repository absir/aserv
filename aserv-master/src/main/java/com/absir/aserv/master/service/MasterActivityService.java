/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月12日 下午8:16:12
 */
package com.absir.aserv.master.service;

import com.absir.aserv.master.bean.JSlaveServer;
import com.absir.aserv.slave.domain.OServersActivityCron;
import com.absir.aserv.slave.domain.OServersActivityOpen;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Started;
import com.absir.context.core.ContextUtils;
import com.absir.core.base.Environment;
import com.absir.core.util.UtilAbsir;
import com.absir.core.util.UtilContext;
import com.absir.core.util.UtilLinked;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

@SuppressWarnings("unchecked")
@Base
@Bean
public class MasterActivityService implements IEntityMerge<JSlaveServer> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(MasterActivityService.class);

    public static final MasterActivityService ME = BeanFactoryUtils.get(MasterActivityService.class);

    private UtilLinked<OServersActivityOpen<?, ?>> targetsActivityOpens;

    private UtilLinked<OServersActivityCron<?, ?>> targetsActivityCrons;

    public void addTargetActivityOpen(OServersActivityOpen<?, ?> targetsActivityOpen) {
        if (targetsActivityOpens == null) {
            synchronized (this) {
                if (targetsActivityOpens == null) {
                    targetsActivityOpens = new UtilLinked<OServersActivityOpen<?, ?>>();
                }
            }
        }

        targetsActivityOpens.add(targetsActivityOpen);
    }

    public void addTargetActivityCron(OServersActivityCron<?, ?> targetsActivityCron) {
        if (targetsActivityCrons == null) {
            synchronized (this) {
                if (targetsActivityCrons == null) {
                    targetsActivityCrons = new UtilLinked<OServersActivityCron<?, ?>>();
                    if (Environment.isStarted()) {
                        startActivityCronsRunnable();
                    }
                }
            }
        }

        targetsActivityCrons.add(targetsActivityCron);
    }

    private boolean startedActivityCrons;

    @Started
    protected synchronized void startActivityCronsRunnable() {
        if (targetsActivityCrons != null) {
            if (startedActivityCrons) {
                return;
            }

            UtilContext.getThreadPoolExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    while (Environment.isActive()) {
                        try {
                            Thread.sleep(60000);
                            targetsActivityCrons.sync();
                            long contextTime = UtilContext.getCurrentTime();
                            for (OServersActivityCron<?, ?> targetsActivityCron : targetsActivityCrons.getList()) {
                                targetsActivityCron.step(contextTime);
                            }

                        } catch (Throwable e) {
                            if (Environment.isActive()) {
                                LOGGER.error("activityCronsRunnable error", e);
                            }
                        }
                    }
                }
            });

            startedActivityCrons = true;
        }
    }

    @Override
    public void merge(String entityName, JSlaveServer entity,
                      com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType, Object mergeEvent) {
        if (mergeType != MergeType.DELETE) {
            if (entity.getBeginTime() > ContextUtils.getContextTime() - 7 * UtilAbsir.DAY_TIME) {
                if (targetsActivityOpens != null) {
                    targetsActivityOpens.sync();
                    for (OServersActivityOpen<?, ?> targetsActivityOpen : targetsActivityOpens.getList()) {
                        MasterActivityService.ME.reActivityServer(targetsActivityOpen, entity);
                    }
                }
            }
        }
    }

    @Transaction
    public void reTargetsActivityOpen(OServersActivityOpen<?, ?> targetsActivityOpen) {
        Session session = BeanDao.getSession();
        Iterator<JSlaveServer> iterator = QueryDaoUtils
                .createQueryArray(session, "SELECT o FROM JSlaveServer o WHERE o.beginTime > ?",
                        ContextUtils.getContextTime() - 7 * UtilAbsir.DAY_TIME)
                .iterate();
        while (iterator.hasNext()) {
            ME.reActivityServer(targetsActivityOpen, iterator.next());
        }
    }

    @Transaction
    public void reActivityServer(OServersActivityOpen<?, ?> targetsActivityOpen, JSlaveServer server) {
        targetsActivityOpen.reActivityServer(server);
    }
}
