/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-10 上午10:49:18
 */
package com.absir.aserv.system.domain;

import com.absir.aserv.consistent.ConsistentUtils;
import com.absir.aserv.system.bean.value.JiActive;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelString;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.hibernate.boost.IEntityMerge;
import org.hibernate.Session;
import org.hibernate.event.spi.PostUpdateEvent;

import java.lang.reflect.TypeVariable;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
public class DActiver<T extends JiActive> implements IEntityMerge<T> {

    protected static final TypeVariable T_VARIABLE = DActiver.class.getTypeParameters()[0];

    //延时更新时间
    protected static final int DELAY_NEXT_TIME = 3000;

    //最大更新时间
    protected static final int MAX_NEXT_TIME = 24 * 3600000;

    protected String entityName;

    //下次更新时间
    protected long nextTime;

    //下次更新查询
    protected String nextQueryString;

    //当前活动查询
    protected String onlineQueryString;

    private boolean isAddedEntityMerges;

    public DActiver(String entityName) {
        if (entityName == null) {
            entityName = SessionFactoryUtils.getJpaEntityName(KernelClass.typeClass(getClass(), T_VARIABLE));
        }

        this.entityName = entityName;
        if (!KernelString.isEmpty(entityName)) {
            nextQueryString = "SELECT o FROM " + entityName + " o WHERE o.beginTime > ? ORDER BY o.beginTime";
            onlineQueryString = "SELECT o FROM " + entityName + " o WHERE o.beginTime <= ? AND o.passTime >= ?";
        }
    }

    public DActiver(String nextQueryString, String onlineQueryString) {
        this.nextQueryString = nextQueryString;
        this.onlineQueryString = onlineQueryString;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public long getNextTime() {
        return nextTime;
    }

    public void setNextTime(long nextTime) {
        this.nextTime = nextTime;
    }

    public String getNextQueryString() {
        return nextQueryString;
    }

    public void setNextQueryString(String nextQueryString) {
        this.nextQueryString = nextQueryString;
    }

    public String getOnlineQueryString() {
        return onlineQueryString;
    }

    public void setOnlineQueryString(String onlineQueryString) {
        this.onlineQueryString = onlineQueryString;
    }

    public void merge(T entity, MergeType mergeType, Object mergeEvent) {
        long contextTime = ContextUtils.getContextTime();
        if (mergeType == MergeType.RELOAD) {
            nextTime = contextTime + DELAY_NEXT_TIME;
            return;
        }

        if (mergeType == MergeType.DELETE) {
            if (entity.getBeginTime() < contextTime && entity.getPassTime() > contextTime) {
                contextTime += DELAY_NEXT_TIME;
                if (nextTime > contextTime) {
                    nextTime = contextTime;
                }
            }

        } else {
            if (mergeType == MergeType.INSERT) {
                if (entity.getPassTime() > contextTime) {
                    if (entity.getBeginTime() <= contextTime) {
                        contextTime += DELAY_NEXT_TIME;
                        if (nextTime > contextTime) {
                            nextTime = contextTime;
                        }

                    } else {
                        if (entity.getBeginTime() < nextTime) {
                            contextTime += DELAY_NEXT_TIME;
                            nextTime = contextTime <= entity.getBeginTime() ? contextTime : entity.getBeginTime();
                        }
                    }
                }

            } else {
                PostUpdateEvent postUpdateEvent = (PostUpdateEvent) mergeEvent;
                long beginTime = entity.getBeginTime();
                long passTime = entity.getPassTime();
                String[] propertyNames = postUpdateEvent.getPersister().getPropertyNames();
                int update = 2;
                for (int i : postUpdateEvent.getDirtyProperties()) {
                    if ("beginTime".equals(propertyNames[i])) {
                        beginTime = KernelDyna.to(postUpdateEvent.getOldState()[i], long.class);
                        if (--update == 0) {
                            break;
                        }

                    } else if ("passTime".equals(propertyNames[i])) {
                        passTime = KernelDyna.to(postUpdateEvent.getOldState()[i], long.class);
                        if (--update == 0) {
                            break;
                        }
                    }
                }

                if ((beginTime <= contextTime && passTime > contextTime) || (entity.getBeginTime() <= contextTime && entity.getPassTime() > contextTime)) {
                    contextTime += DELAY_NEXT_TIME;
                    if (nextTime > contextTime) {
                        nextTime = contextTime;
                    }

                } else {
                    if (entity.getPassTime() > contextTime) {
                        if (entity.getBeginTime() < nextTime) {
                            contextTime += DELAY_NEXT_TIME;
                            nextTime = contextTime <= entity.getBeginTime() ? contextTime : entity.getBeginTime();
                        }
                    }
                }
            }
        }
    }

    public void setNextActive(long contextTime, T activity) {
        // 最大延时1天更新
        nextTime = contextTime + MAX_NEXT_TIME;
        if (activity != null) {
            if (nextTime > activity.getBeginTime()) {
                nextTime = activity.getBeginTime();
            }
        }
    }

    public void setOnlineActives(List<T> activities) {
        for (T activity : activities) {
            if (nextTime > activity.getPassTime()) {
                nextTime = activity.getPassTime();
            }
        }
    }

    public boolean stepNext(long contextTime) {
        if (nextTime < contextTime) {
            nextTime = Long.MAX_VALUE;
            return true;
        }

        return false;
    }

    public List<T> reloadActives(long contextTime) {
        Session session = BeanDao.getSession();
        Iterator<T> iterator = QueryDaoUtils.createQueryArray(session, nextQueryString, contextTime).setMaxResults(1).iterate();
        setNextActive(contextTime, iterator.hasNext() ? iterator.next() : null);
        List<T> actives = QueryDaoUtils.createQueryArray(session, onlineQueryString, contextTime, contextTime).list();
        setOnlineActives(actives);
        return actives;
    }

    @Override
    public void merge(String entityName, T entity, MergeType mergeType, Object mergeEvent) {
        merge(entity, mergeType, mergeEvent);
    }

    public void addEntityMerges() {
        if (isAddedEntityMerges) {
            return;
        }

        isAddedEntityMerges = true;
        ConsistentUtils.addEntityMerges(entityName, null, this);
    }
}
