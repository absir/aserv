/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-4-10 上午10:49:18
 */
package com.absir.aserv.system.domain;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.event.spi.PostUpdateEvent;

import com.absir.aserv.system.bean.value.JiActive;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelString;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.hibernate.boost.IEntityMerge.MergeType;

/**
 * @author absir
 * 
 */
@SuppressWarnings("unchecked")
public class DActiver<T extends JiActive> {

	/** 下次更新时间 */
	protected long nextTime;

	/** 下次更新查询 */
	protected String nextQueryString;

	/** 当前活动查询 */
	protected String onlineQueryString;

	/** 延时更新时间 */
	protected static final int DELAY_NEXT_TIME = 3000;

	/** 最大更新时间 */
	protected static final int MAX_NEXT_TIME = 24 * 3600000;

	/**
	 * @param entityName
	 */
	public DActiver(String entityName) {
		if (entityName == null) {
			entityName = SessionFactoryUtils.getJpaEntityName(KernelClass.argumentClass(getClass()));
		}

		if (!KernelString.isEmpty(entityName)) {
			nextQueryString = "SELECT o FROM " + entityName + " o WHERE o.beginTime > ? ORDER BY o.beginTime";
			onlineQueryString = "SELECT o FROM " + entityName + " o WHERE o.beginTime <= ? AND o.passTime >= ?";
		}
	}

	/**
	 * @param nextQueryString
	 * @param onlineQueryString
	 */
	public DActiver(String nextQueryString, String onlineQueryString) {
		this.nextQueryString = nextQueryString;
		this.onlineQueryString = onlineQueryString;
	}

	/**
	 * @return the nextTime
	 */
	public long getNextTime() {
		return nextTime;
	}

	/**
	 * @param nextTime
	 *            the nextTime to set
	 */
	public void setNextTime(long nextTime) {
		this.nextTime = nextTime;
	}

	/**
	 * @return the nextQueryString
	 */
	public String getNextQueryString() {
		return nextQueryString;
	}

	/**
	 * @param nextQueryString
	 *            the nextQueryString to set
	 */
	public void setNextQueryString(String nextQueryString) {
		this.nextQueryString = nextQueryString;
	}

	/**
	 * @return the onlineQueryString
	 */
	public String getOnlineQueryString() {
		return onlineQueryString;
	}

	/**
	 * @param onlineQueryString
	 *            the onlineQueryString to set
	 */
	public void setOnlineQueryString(String onlineQueryString) {
		this.onlineQueryString = onlineQueryString;
	}

	/**
	 * @param entity
	 * @param mergeType
	 */
	public void merge(T entity, MergeType mergeType, Object mergeEvent) {
		long contextTime = ContextUtils.getContextTime();
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

	/**
	 * @param contextTime
	 * @param activity
	 */
	public void setNextActive(long contextTime, T activity) {
		// 最大延时1天更新
		nextTime = contextTime + MAX_NEXT_TIME;
		if (activity != null) {
			if (nextTime > activity.getBeginTime()) {
				nextTime = activity.getBeginTime();
			}
		}
	}

	/**
	 * @param activities
	 */
	public void setOnlineActives(List<T> activities) {
		for (T activity : activities) {
			if (nextTime > activity.getPassTime()) {
				nextTime = activity.getPassTime();
			}
		}
	}

	/**
	 * @param contextTime
	 * @return
	 */
	public boolean stepNext(long contextTime) {
		if (nextTime < contextTime) {
			nextTime = Long.MAX_VALUE;
			return true;
		}

		return false;
	}

	/**
	 * @param contextTime
	 * @return
	 */
	public List<T> reloadActives(long contextTime) {
		Session session = BeanDao.getSession();
		Iterator<T> iterator = QueryDaoUtils.createQueryArray(session, nextQueryString, contextTime).setMaxResults(1).iterate();
		setNextActive(contextTime, iterator.hasNext() ? iterator.next() : null);
		List<T> actives = QueryDaoUtils.createQueryArray(session, onlineQueryString, contextTime, contextTime).list();
		setOnlineActives(actives);
		return actives;
	}
}
