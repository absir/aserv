/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月12日 下午2:35:22
 */
package com.absir.aserv.slave.domain;

import com.absir.aserv.master.bean.base.JbServerTargetsCron;
import com.absir.aserv.master.bean.base.JbServerTargetsO;
import com.absir.aserv.master.service.MasterSyncService;
import com.absir.aserv.system.bean.value.JiOrdinal;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.service.BeanService;
import com.absir.context.schedule.cron.CronSequenceGenerator;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelList;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAbsir;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.hibernate.boost.L2EntityMergeService;
import com.absir.orm.transaction.TransactionContext;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.TypeVariable;
import java.util.*;

// 服务开启定时周期类
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class OServersActivityCron<T extends JbServerTargetsO, O extends JbServerTargetsCron> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(OServersActivityCron.class);

    protected static final TypeVariable<?> TARGETS_VARIABLE = OServersActivityCron.class.getTypeParameters()[0];

    protected static final TypeVariable<?> TARGETS_OPEN_VARIABLE = OServersActivityCron.class.getTypeParameters()[1];

    protected Class<T> serversClass;

    protected Class<O> serversOpenClass;

    protected String serversName;

    protected String serversOpenName;

    protected String selectAllOpenQuery;

    protected String updateOpenQuery;

    protected String selectServerActivityQuery;

    protected List<CronOpenActivity<O>> serverCronOpenActivities;

    protected static class CronOpenActivity<O extends JbServerTargetsCron> implements JiOrdinal {

        public CronSequenceGenerator cronSequenceGenerator;

        public long openNextTime;

        public O activity;

        public Long getId() {
            return activity == null ? null : activity.getId();
        }

        @Override
        public int getOrdinal() {
            return activity.getOrdinal();
        }
    }

    public OServersActivityCron() {
        serversClass = KernelClass.typeClass(getClass(), TARGETS_VARIABLE);
        serversOpenClass = KernelClass.typeClass(getClass(), TARGETS_OPEN_VARIABLE);
        serversName = serversClass.getSimpleName();
        serversOpenName = serversOpenClass.getSimpleName();
        initQueryString();
        L2EntityMergeService.ME.addEntityMerges(serversOpenClass, new IEntityMerge<O>() {

            @Override
            public void merge(String entityName, O entity, com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType, Object mergeEvent) {
                if (mergeType == MergeType.INSERT) {
                    addActivity(entity);

                } else {
                    reloadTargetsActivity();
                }
            }
        });

        reloadTargetsActivity();
    }

    protected void initQueryString() {
        selectAllOpenQuery = "SELECT o FROM " + serversOpenName + " o WHERE o.open = true AND o.openLifeDay > 0";
        updateOpenQuery = "UPDATE " + serversOpenName + " o SET o.openLastTime = ? WHERE o.id = ?";
        selectServerActivityQuery = "SELECT o FROM " + serversName + " o WHERE o.serversOpenId = ? AND o.passTime < ?";
    }

    /**
     * 重新载入活动
     */
    protected void reloadTargetsActivity() {
        List<O> activities = (List<O>) BeanService.ME.selectQueryMaxResults(256, selectAllOpenQuery);
        List<CronOpenActivity<O>> cronActivities = new ArrayList<CronOpenActivity<O>>(activities.size());
        for (O activity : activities) {
            addCronOpenActivity(cronActivities, activity);
        }

        serverCronOpenActivities = cronActivities;
    }

    protected void addActivity(O activity) {
        synchronized (serversOpenName) {
            if (serverCronOpenActivities == null) {
                serverCronOpenActivities = new ArrayList<CronOpenActivity<O>>();
            }

            addCronOpenActivity(serverCronOpenActivities, activity);
        }
    }

    protected final void addCronOpenActivity(List<CronOpenActivity<O>> cronOpenActivities, O activity) {
        if (activity == null || !activity.isOpen() || activity.getOpenLifeDay() <= 0 || KernelString.isEmpty(activity.getOpenCron())) {
            return;
        }

        try {
            CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(activity.getOpenCron());
            CronOpenActivity<O> cronOpenActivity = new CronOpenActivity<O>();
            cronOpenActivity.cronSequenceGenerator = cronSequenceGenerator;
            cronOpenActivity.activity = activity;
            KernelList.addObjectComparator((List<JiOrdinal>) (Object) cronOpenActivities, cronOpenActivity, BeanService.COMPARATOR);

        } catch (Throwable e) {
            LOGGER.error("addCronActivity error at " + activity.getId(), e);
        }
    }

    public void step(long contextTime) {
        if (serverCronOpenActivities == null) {
            return;
        }

        Date date = new Date();
        synchronized (serversOpenName) {
            for (CronOpenActivity<O> cronOpenActivity : serverCronOpenActivities) {
                try {
                    checkOpenActivity(contextTime, date, cronOpenActivity);

                } catch (Throwable e) {
                    LOGGER.error("checkOpenActivity error at " + cronOpenActivity.getId(), e);
                }
            }
        }
    }

    protected void checkOpenActivity(long contextTime, Date date, CronOpenActivity<O> cronOpenActivity) {
        if (cronOpenActivity.openNextTime > contextTime) {
            return;
        }

        O openActivity = cronOpenActivity.activity;
        long beginTime = contextTime - openActivity.getOpenLifeDay() * UtilAbsir.DAY_TIME;
        if (beginTime < openActivity.getOpenLastTime()) {
            beginTime = openActivity.getOpenLastTime();
        }

        date.setTime(beginTime);
        beginTime = cronOpenActivity.cronSequenceGenerator.next(date).getTime();
        long openNextTime = beginTime - UtilAbsir.DAY_TIME;
        if (openNextTime > contextTime) {
            cronOpenActivity.openNextTime = openNextTime;

        } else {
            long passTime = beginTime + openActivity.getOpenLifeDay() * UtilAbsir.DAY_TIME;
            doOpenActivity(openActivity, contextTime, beginTime, passTime);
            passTime += (openActivity.getOpenSubDay() - 1) * UtilAbsir.DAY_TIME;
            openActivity.setOpenLastTime(passTime);
            BeanService.ME.executeUpdate(updateOpenQuery, passTime, openActivity.getId());
        }
    }

    protected abstract T createActivity();

    protected abstract void setActivityEmbed(T act, O activity);

    protected void doOpenActivity(O activity, long contextTime, long beginTime, long passTime) {
        TransactionContext<?> transactionContext = BeanDao.open(null, BeanService.TRANSACTION_READ_WRITE);
        try {
            Session session = BeanDao.getSession();
            if (activity.isOpenSingly()) {
                // 生成单独活动
                Set<Long> serverIdsSet = null;
                {
                    Iterator<Long> serverIdsIterator = null;
                    long[] serverIds = null;
                    if (activity.isAllServerIds()) {
                        if (activity.getGroupIds() == null || activity.getGroupIds().length == 0) {
                            serverIdsIterator = QueryDaoUtils
                                    .createQueryArray(session, "SELECT o.id FROM JSlaveServer o WHERE o.beginTime < ? AND o.passTime > ?", contextTime, contextTime)
                                    .iterate();

                        } else {
                            serverIds = MasterSyncService.ME.getServerIdsFromGroupIds(activity.getGroupIds());
                        }

                    } else {
                        serverIds = activity.getServerIds();
                    }


                    if (serverIdsIterator != null) {
                        while (serverIdsIterator.hasNext()) {
                            if (serverIdsSet == null) {
                                serverIdsSet = new HashSet<Long>();
                            }

                            serverIdsSet.add(serverIdsIterator.next());
                        }

                    } else if (serverIds != null && serverIds.length > 0) {
                        for (long serverId : serverIds) {
                            if (serverIdsSet == null) {
                                serverIdsSet = new HashSet<Long>();
                            }

                            serverIdsSet.add(serverId);
                        }
                    }
                }

                if (serverIdsSet == null) {
                    return;
                }

                List<T> actCaches = null;
                Iterator<T> iterator = QueryDaoUtils.createQueryArray(session, selectServerActivityQuery, activity.getId(), contextTime).iterate();
                while (iterator.hasNext()) {
                    T act = iterator.next();
                    long[] serverIds = act.getServerIds();
                    if (serverIds != null && serverIds.length == 1 && serverIdsSet.remove(serverIds[0])) {
                        setActivity(session, act, activity, serverIds, beginTime, passTime);
                        session.merge(act);

                    } else {
                        if (actCaches == null) {
                            actCaches = new ArrayList<T>();
                        }

                        actCaches.add(act);
                    }
                }

                if (!serverIdsSet.isEmpty()) {
                    T act;
                    for (Long serverId : serverIdsSet) {
                        if (actCaches == null) {
                            act = createActivity();
                            act.setServersOpenId(activity.getId());

                        } else {
                            act = actCaches.remove(actCaches.size() - 1);
                            if (actCaches.size() == 0) {
                                actCaches = null;
                            }
                        }

                        setActivity(session, act, activity, new long[]{serverId}, beginTime, passTime);
                    }
                }

            } else {
                // 生成通用活动
                Iterator<T> iterator = QueryDaoUtils.createQueryArray(session, selectServerActivityQuery, activity.getId(), contextTime).setMaxResults(1).iterate();
                T act;
                if (iterator.hasNext()) {
                    act = iterator.next();

                } else {
                    act = createActivity();
                    act.setServersOpenId(activity.getId());
                }

                setActivity(session, act, activity, null, beginTime, passTime);
            }

        } finally {
            BeanDao.commit(transactionContext, null);
        }
    }

    protected String findGroupIdsHql;

    protected String findServerIdsHql;

    protected String getFindGroupIdsHql() {
        if (findGroupIdsHql == null) {
            findGroupIdsHql = "SELECT o.id FROM " + serversName
                    + " o WHERE o.groupIds = ? AND ((o.beginTime <= ? AND o.passTime > ?) OR (o.passTime <= ? AND o.beginTime > ?))";
        }

        return findGroupIdsHql;
    }


    protected String getFindServerIdsHql() {
        if (findServerIdsHql == null) {
            findServerIdsHql = "SELECT o.id FROM " + serversName
                    + " o WHERE o.serverIds = ? AND ((o.beginTime <= ? AND o.passTime > ?) OR (o.passTime <= ? AND o.beginTime > ?))";
        }

        return findServerIdsHql;
    }

    protected void setActivity(Session session, T act, O activity, long[] serverIds, long beginTime, long passTime) {
        if (serverIds == null) {
            if (QueryDaoUtils.createQueryArray(session, getFindGroupIdsHql()
                    , activity.getGroupIds(), beginTime, beginTime, passTime, passTime).setMaxResults(1).iterate().hasNext()) {
                return;
            }

        } else {
            if (QueryDaoUtils.createQueryArray(session, getFindServerIdsHql()
                    , serverIds, beginTime, beginTime, passTime, passTime).setMaxResults(1).iterate().hasNext()) {
                return;
            }
        }

        setActivityEmbed(act, activity);
        if (serverIds == null) {
            act.setServerIds(activity.getServerIds());
            act.setAllServerIds(activity.isAllServerIds());
            act.setGroupIds(activity.getGroupIds());

        } else {
            act.setServerIds(serverIds);
            act.setAllServerIds(false);
            act.setGroupIds(null);
        }

        act.setMark(activity.getMark());
        act.setBeginTime(beginTime);
        act.setPassTime(passTime);
        session.merge(act);
    }

}
