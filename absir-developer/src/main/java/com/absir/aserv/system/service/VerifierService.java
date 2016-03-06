/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年9月29日 下午4:46:32
 */
package com.absir.aserv.system.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absir.aserv.crud.CrudEntity;
import com.absir.aserv.crud.CrudUtils;
import com.absir.aserv.system.bean.JVerifier;
import com.absir.aserv.system.bean.proxy.JiPass;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.helper.HelperRandom;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Started;
import com.absir.bean.inject.value.Value;
import com.absir.context.core.ContextUtils;
import com.absir.context.schedule.cron.CronFixDelayRunable;
import com.absir.orm.hibernate.SessionFactoryBean;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.value.Transaction;
import com.absir.orm.value.JoEntity;

/**
 * @author absir
 *
 */
@SuppressWarnings("unchecked")
@Base
@Bean
public class VerifierService {

	/** LOGGER */
	protected static final Logger LOGGER = LoggerFactory.getLogger(VerifierService.class);

	/** clearFixDelay */
	@Value("verifier.clear")
	private long clearFixDelay = 8 * 3600000;

	/** ME */
	public static final VerifierService ME = BeanFactoryUtils.get(VerifierService.class);

	/**
	 * @param dist
	 * @return
	 */
	public String randVerifierId(Object dist) {
		return HelperRandom.randHashId(dist);
	}

	/**
	 * 添加验证
	 * 
	 * @param dist
	 * @param tag
	 * @param value
	 * @param lifeTime
	 */
	public void persistVerifier(Object dist, String tag, String value, long lifeTime) {
		String id = randVerifierId(dist);
		JVerifier verifier = new JVerifier();
		verifier.setId(id);
		verifier.setTag(tag);
		verifier.setValue(value);
		verifier.setPassTime(ContextUtils.getContextTime() + lifeTime);
		BeanService.ME.persist(verifier);
	}

	/**
	 * 查找验证
	 * 
	 * @param id
	 * @return
	 */
	@Transaction(readOnly = true)
	public JVerifier findVerifier(String id, String tag) {
		Query query = BeanDao.getSession()
				.createQuery("SELECT o FROM JVerifier o WHERE o.id = ? AND o.passTime > ? AND o.tag = ?");
		query.setMaxResults(1);
		query.setParameter(0, id);
		query.setParameter(1, ContextUtils.getContextTime());
		query.setParameter(2, tag);
		Iterator<JVerifier> iterator = query.iterate();
		return iterator.hasNext() ? iterator.next() : null;
	}

	/**
	 * 处理过期对象
	 */
	@Started
	protected void initVerifierNames() {
		SessionFactoryBean sessionFactoryBean = SessionFactoryUtils.get();
		SessionFactory sessionFactory = sessionFactoryBean.getSessionFactory();
		if (sessionFactory != null) {
			final Map<String, CrudEntity> nameMapCrudEntity = new HashMap<String, CrudEntity>();
			for (Entry<String, Entry<Class<?>, SessionFactory>> entry : sessionFactoryBean
					.getJpaEntityNameMapEntityClassFactory().entrySet()) {
				Entry<Class<?>, SessionFactory> value = entry.getValue();
				if (value.getValue() == sessionFactory && JiPass.class.isAssignableFrom(value.getClass())) {
					JoEntity joEntity = new JoEntity(entry.getKey(), entry.getValue().getKey());
					nameMapCrudEntity.put(entry.getKey(), CrudUtils.getCrudEntity(joEntity));
				}
			}

			if (!nameMapCrudEntity.isEmpty()) {
				ContextUtils.getScheduleFactory().addRunables(new CronFixDelayRunable(new Runnable() {

					@Override
					public void run() {
						ME.clearExpiredVerifier(nameMapCrudEntity);
					}

				}, clearFixDelay));
			}

		}
	}

	/**
	 * 清除过期验证
	 * 
	 * @param nameMapCrudEntity
	 */
	@Transaction
	protected void clearExpiredVerifier(Map<String, CrudEntity> nameMapCrudEntity) {
		long contextTime = ContextUtils.getContextTime();
		for (Entry<String, CrudEntity> entry : nameMapCrudEntity.entrySet()) {
			try {
				if (entry.getValue() != null) {
					Iterator<Object> iterator = QueryDaoUtils.createQueryArray(BeanDao.getSession(),
							"SELECT o FROM " + entry.getKey() + " o WHERE o.passTime > 0 AND o.passTime < ?",
							contextTime).iterate();
					while (iterator.hasNext()) {
						CrudUtils.crud(Crud.DELETE, null, entry.getValue().getJoEntity(), iterator.next(), null, null);
					}
				}

				QueryDaoUtils.createQueryArray(BeanDao.getSession(),
						"DELETE o FROM " + entry.getKey() + " o WHERE o.passTime > 0 AND o.passTime < ?", contextTime)
						.executeUpdate();

			} catch (Throwable e) {
				LOGGER.error("clear expired verifier " + entry.getKey() + " error!", e);
			}
		}
	}
}
