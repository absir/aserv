/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月12日 下午2:35:22
 */
package com.absir.aserv.slave.domain;

import com.absir.aserv.master.bean.JSlaveServer;
import com.absir.aserv.master.bean.base.JbServerTargetsO;
import com.absir.aserv.master.bean.base.JbServerTargetsOpen;
import com.absir.aserv.master.service.MasterActivityService;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.helper.HelperNumber;
import com.absir.aserv.system.service.BeanService;
import com.absir.core.kernel.KernelClass;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.hibernate.boost.L2EntityMergeService;
import com.absir.orm.transaction.TransactionContext;
import org.hibernate.Session;

import java.lang.reflect.TypeVariable;
import java.util.*;

// 服务开启定时周期类
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class OServersActivityOpen<T extends JbServerTargetsO, O extends JbServerTargetsOpen> {

    protected static final TypeVariable<?> TARGETS_VARIABLE = OServersActivityOpen.class.getTypeParameters()[0];

    protected static final TypeVariable<?> TARGETS_OPEN_VARIABLE = OServersActivityOpen.class.getTypeParameters()[1];

    protected Class<T> serversClass;

    protected Class<O> serversOpenClass;

    protected String serversName;

    protected String serversOpenName;

    protected String selectAllOpenQuery;

    protected String selectServerActivityQuery;

    protected OServersActivity<List<O>> serversActivity = new OServersActivity<List<O>>();

    public OServersActivityOpen() {
        serversClass = KernelClass.typeClass(getClass(), TARGETS_VARIABLE);
        serversOpenClass = KernelClass.typeClass(getClass(), TARGETS_OPEN_VARIABLE);
        serversName = serversClass.getSimpleName();
        serversOpenName = serversOpenClass.getSimpleName();
        initQueryString();
        reloadTargetsActivity();
        L2EntityMergeService.ME.addEntityMerges(serversOpenClass, new IEntityMerge<O>() {

            @Override
            public void merge(String entityName, O entity,
                              com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType, Object mergeEvent) {
                if (mergeType == MergeType.INSERT) {
                    addActivity(entity);

                } else {
                    reloadTargetsActivity();
                }

                MasterActivityService.ME.reTargetsActivityOpen(OServersActivityOpen.this);
            }
        });
    }

    public Class<T> getServersClass() {
        return serversClass;
    }

    protected void initQueryString() {
        selectAllOpenQuery = "SELECT o FROM " + serversOpenName + " o WHERE o.openLifeDay > 0";
        selectServerActivityQuery = "SELECT o FROM " + serversName + " o WHERE o.serverId = ?";
    }

    /**
     * 重新载入活动
     */
    protected void reloadTargetsActivity() {
        TransactionContext<?> transactionContext = BeanDao.open(null, BeanService.TRANSACTION_READ_ONLY);
        try {
            serversActivity.clearActivity();
            Iterator<O> iterator = QueryDaoUtils.createQueryArray(BeanDao.getSession(), selectAllOpenQuery).iterate();
            while (iterator.hasNext()) {
                O activity = iterator.next();
                addActivity(activity);
            }

        } finally {
            BeanDao.commit(transactionContext, null);
        }
    }

    protected void addActivity(O activity) {
        synchronized (serversActivity) {
            if (activity.getServerIds() == null || activity.getServerIds().length == 0) {
                serversActivity.singleActivity = mergeActivities(serversActivity.singleActivity, activity);

            } else {
                for (long targetId : activity.getServerIds()) {
                    serversActivity.singleActivityMap.put(targetId,
                            mergeActivities(serversActivity.singleActivityMap.get(targetId), activity));
                }
            }
        }
    }

    protected List<O> mergeActivities(List<O> activities, O newActivity) {
        if (activities == null) {
            activities = new ArrayList<O>();
            activities.add(newActivity);

        } else {
            int subDay = newActivity.getOpenSubDay();
            int endDay = subDay + newActivity.getOpenLifeDay();
            List<O> newActivities = new ArrayList<O>();
            for (O activity : activities) {
                if (HelperNumber.isNoCross(activity.getOpenSubDay(), activity.getOpenSubDay() + activity.getOpenLifeDay(), subDay,
                        endDay)) {
                    if (!serversActivity.canOverwriteTargets(activity, newActivity)) {
                        newActivity = null;
                        break;
                    }

                } else {
                    newActivities.add(activity);
                }
            }

            if (newActivity != null) {
                activities = newActivities;
                activities.add(newActivity);
            }
        }

        return activities;
    }

    protected abstract T createActivity();

    protected abstract void setActivityEmbed(T act, O activity);

    protected void setActivity(long[] serverIds, Calendar calendar, T act, O activity) {
        setActivityEmbed(act, activity);
        act.setServerIds(serverIds);
        act.setMark(activity.getMark());
        int calendarDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.add(Calendar.DAY_OF_YEAR, activity.getOpenSubDay());
        act.setBeginTime(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_YEAR, activity.getOpenLifeDay());
        act.setPassTime(calendar.getTimeInMillis());
        calendar.set(Calendar.DAY_OF_YEAR, calendarDay);
    }

    /**
     * 激活服务
     */
    public void reActivityServer(JSlaveServer server) {
        List<O> activities = serversActivity.getSingleActivity(server.getId());
        if (activities == null || activities.isEmpty()) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(server.getBeginTime());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Session session = BeanDao.getSession();
        Iterator<T> iterator = QueryDaoUtils.createQueryArray(session, selectServerActivityQuery, server.getId())
                .iterate();
        Map<Long, T> idMapT = new HashMap<Long, T>();
        while (iterator.hasNext()) {
            T t = iterator.next();
            idMapT.put(t.getServersOpenId(), t);
        }

        long[] serverIds = new long[]{server.getId()};
        for (O activity : activities) {
            T act = idMapT.get(activity.getId());
            if (act == null) {
                act = createActivity();
                act.setServerId(server.getId());
                act.setServersOpenId(activity.getId());
                setActivity(serverIds, calendar, act, activity);
                session.persist(act);

            } else {
                setActivity(serverIds, calendar, act, activity);
                session.merge(act);
            }
        }

        session.flush();
    }
}
