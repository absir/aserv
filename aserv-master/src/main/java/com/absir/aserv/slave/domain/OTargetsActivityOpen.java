/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月12日 下午2:35:22
 */
package com.absir.aserv.slave.domain;

import com.absir.aserv.master.bean.JSlaveServer;
import com.absir.aserv.master.bean.base.JbBeanTargetsO;
import com.absir.aserv.master.bean.base.JbBeanTargetsOpen;
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

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class OTargetsActivityOpen<T extends JbBeanTargetsO, O extends JbBeanTargetsOpen> {

    protected static final TypeVariable<?> TARGETS_VARIABLE = OTargetsActivityOpen.class.getTypeParameters()[0];

    protected static final TypeVariable<?> TARGETS_OPEN_VARIABLE = OTargetsActivityOpen.class.getTypeParameters()[1];

    protected Class<T> targetsClass;

    protected Class<O> targetsOpenClass;

    protected String targetsName;

    protected String targetsOpenName;

    protected OTargetsActivity<List<O>> targetsActivity = new OTargetsActivity<List<O>>();

    protected String selectAllOpenQuery;

    protected String selectServerActivityQuery;

    public OTargetsActivityOpen() {
        targetsClass = KernelClass.typeClass(getClass(), TARGETS_VARIABLE);
        targetsOpenClass = KernelClass.typeClass(getClass(), TARGETS_OPEN_VARIABLE);
        targetsName = targetsClass.getSimpleName();
        targetsOpenName = targetsOpenClass.getSimpleName();
        initQueryString();
        reloadTargetsActivity();
        L2EntityMergeService.ME.addEntityMerges(targetsOpenClass, new IEntityMerge<O>() {

            @Override
            public void merge(String entityName, O entity,
                              com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType, Object mergeEvent) {
                if (mergeType == MergeType.INSERT) {
                    addActivity(entity);

                } else {
                    reloadTargetsActivity();
                }

                MasterActivityService.ME.reTargetsActivityOpen(OTargetsActivityOpen.this);
            }
        });
    }

    public Class<T> getTargetsClass() {
        return targetsClass;
    }

    protected void initQueryString() {
        selectAllOpenQuery = "SELECT o FROM " + targetsOpenName + " o WHERE o.openLifeDay > 0";
        selectServerActivityQuery = "SELECT o FROM " + targetsName + " o WHERE o.serverId = ?";
    }

    /**
     * 重新载入活动
     */
    protected void reloadTargetsActivity() {
        TransactionContext<?> transactionContext = BeanDao.open(null, BeanService.TRANSACTION_READ_ONLY);
        try {
            targetsActivity.clearActivity();
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
        synchronized (targetsActivity) {
            if (activity.getTargets() == null || activity.getTargets().length == 0) {
                targetsActivity.singleActivity = mergeActivities(targetsActivity.singleActivity, activity);

            } else {
                for (long targetId : activity.getTargets()) {
                    targetsActivity.singleActivityMap.put(targetId,
                            mergeActivities(targetsActivity.singleActivityMap.get(targetId), activity));
                }
            }
        }
    }

    protected List<O> mergeActivities(List<O> activities, O activity) {
        if (activities == null) {
            activities = new ArrayList<O>();
            activities.add(activity);

        } else {
            int subDay = activity.getOpenSubDay();
            int endDay = subDay + activity.getOpenLifeDay();
            List<O> newActivities = new ArrayList<O>();
            for (O act : activities) {
                if (HelperNumber.isNoCross(act.getOpenSubDay(), act.getOpenSubDay() + act.getOpenLifeDay(), subDay,
                        endDay)) {
                    if (!targetsActivity.canOverwriteTargets(act, activity)) {
                        activity = null;
                        break;
                    }

                } else {
                    newActivities.add(act);
                }
            }

            if (activity != null) {
                activities = newActivities;
                activities.add(activity);
            }
        }

        return activities;
    }

    protected abstract T createActivity();

    protected void setActivity(long[] targets, Calendar calendar, T act, O activity) {
        setActivityEmbed(act, activity);
        act.setTargets(targets);
        act.setDescription(activity.getDescription());
        calendar.add(Calendar.DAY_OF_YEAR, activity.getOpenSubDay());
        act.setBeginTime(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_YEAR, activity.getOpenLifeDay());
        act.setPassTime(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_YEAR, -(activity.getOpenSubDay() + activity.getOpenLifeDay()));
    }

    protected abstract void setActivityEmbed(T act, O activity);

    /**
     * 激活服务
     *
     * @param server
     */
    public void reActivityServer(JSlaveServer server) {
        List<O> activities = targetsActivity.getSingleActivity(server.getId());
        if (activities == null || activities.isEmpty()) {
            return;
        }

        long[] targets = new long[]{server.getId()};
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
            idMapT.put(t.getOpenId(), t);
        }

        for (O activity : activities) {
            T act = idMapT.get(activity.getId());
            if (act == null) {
                act = createActivity();
                act.setServerId(server.getId());
                act.setOpenId(activity.getId());
                setActivity(targets, calendar, act, activity);
                session.persist(act);

            } else {
                setActivity(targets, calendar, act, activity);
                session.merge(act);
            }
        }

        session.flush();
    }
}
