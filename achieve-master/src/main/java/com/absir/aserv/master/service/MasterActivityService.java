/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月12日 下午8:16:12
 */
package com.absir.aserv.master.service;

import com.absir.aserv.master.bean.JSlaveServer;
import com.absir.aserv.slave.domain.OTargetsActivityOpen;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.context.core.ContextUtils;
import com.absir.core.util.UtilAbsir;
import com.absir.core.util.UtilLinked;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;

import java.util.Iterator;

@SuppressWarnings("unchecked")
@Base
@Bean
public class MasterActivityService implements IEntityMerge<JSlaveServer> {

    public static final MasterActivityService ME = BeanFactoryUtils.get(MasterActivityService.class);

    private UtilLinked<OTargetsActivityOpen<?, ?>> targetsActivityOpens;

    public void addTargetActivityOpen(OTargetsActivityOpen<?, ?> targetsActivityOpen) {
        if (targetsActivityOpens == null) {
            targetsActivityOpens = new UtilLinked<OTargetsActivityOpen<?, ?>>();
        }

        targetsActivityOpens.add(targetsActivityOpen);
    }

    @Override
    public void merge(String entityName, JSlaveServer entity,
                      com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType, Object mergeEvent) {
        if (mergeType != MergeType.DELETE) {
            if (entity.getBeginTime() > ContextUtils.getContextTime() - 7 * UtilAbsir.DAY_TIME) {
                if (targetsActivityOpens != null) {
                    targetsActivityOpens.sync();
                    for (OTargetsActivityOpen<?, ?> targetsActivityOpen : targetsActivityOpens.getList()) {
                        MasterActivityService.ME.reActivityServer(targetsActivityOpen, entity);
                    }
                }
            }
        }
    }

    @Transaction
    public void reTargetsActivityOpen(OTargetsActivityOpen<?, ?> targetsActivityOpen) {
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
    public void reActivityServer(OTargetsActivityOpen<?, ?> targetsActivityOpen, JSlaveServer server) {
        targetsActivityOpen.reActivityServer(server);
    }
}
