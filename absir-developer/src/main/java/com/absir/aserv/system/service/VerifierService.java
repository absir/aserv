/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月29日 下午4:46:32
 */
package com.absir.aserv.system.service;

import com.absir.aserv.crud.CrudEntity;
import com.absir.aserv.crud.CrudUtils;
import com.absir.aserv.system.bean.JVerifier;
import com.absir.aserv.system.bean.proxy.JiPass;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.helper.HelperRandom;
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Started;
import com.absir.bean.inject.value.Value;
import com.absir.context.core.ContextUtils;
import com.absir.context.schedule.cron.CronFixDelayRunnable;
import com.absir.orm.hibernate.SessionFactoryBean;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.value.Transaction;
import com.absir.orm.value.JoEntity;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("unchecked")
@Base
@Bean
public class VerifierService {

    public static final VerifierService ME = BeanFactoryUtils.get(VerifierService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(VerifierService.class);

    @Value("verifier.clear")
    private long clearFixDelay = 8 * 3600000;

    private Map<String, CrudEntity> nameMapCrudEntity;

    public String randVerifierId(Object dist) {
        return HelperRandom.randHashId(dist);
    }

    /**
     * 添加验证
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

    public void mergeVerifier(String id, String tag, String value, long lifeTime) {
        JVerifier verifier = new JVerifier();
        verifier.setId(id);
        verifier.setTag(tag);
        verifier.setValue(value);
        verifier.setPassTime(ContextUtils.getContextTime() + lifeTime);
        BeanService.ME.merge(verifier);
    }

    /**
     * 查找验证
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

    protected void setNameMapCrudEntity(Map<String, CrudEntity> nameMapCrudEntity) {
        this.nameMapCrudEntity = nameMapCrudEntity;
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
                setNameMapCrudEntity(nameMapCrudEntity);
                ContextUtils.getScheduleFactory().addRunnables(new CronFixDelayRunnable(new Runnable() {

                    @Override
                    public void run() {
                        ME.clearExpiredVerifier();
                    }

                }, clearFixDelay));
            }

        }
    }

    /**
     * 清除过期验证
     */
    @Async(notifier = true)
    @Transaction
    public void clearExpiredVerifier() {
        if (nameMapCrudEntity == null) {
            return;
        }

        long contextTime = ContextUtils.getContextTime();
        for (Entry<String, CrudEntity> entry : nameMapCrudEntity.entrySet()) {
            try {
                if (entry.getValue() != null) {
                    Iterator<Object> iterator = QueryDaoUtils.createQueryArray(BeanDao.getSession(),
                            "SELECT o FROM " + entry.getKey() + " o WHERE o.passTime > 0 AND o.passTime < ?",
                            contextTime).iterate();
                    while (iterator.hasNext()) {
                        CrudUtils.crud(Crud.DELETE, true, null, entry.getValue().getJoEntity(), iterator.next(), null, null);
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
