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
import com.absir.aserv.system.bean.proxy.IPassClear;
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
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAbsir;
import com.absir.orm.hibernate.SessionFactoryBean;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.value.Transaction;
import com.absir.orm.value.JoEntity;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
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

    @Value("verifier.before")
    private long clearBeforeDelay = UtilAbsir.DAY_TIME;

    private Map<String, CrudEntity> nameMapCrudEntity;

    public String randVerifierId(Object dist) {
        return HelperRandom.randHashId(dist);
    }

    public static JVerifier createVerifier(String id, String tag, String value, int intValue, long lifeTime) {
        JVerifier verifier = new JVerifier();
        verifier.setId(id);
        verifier.setTag(tag);
        verifier.setValue(value);
        verifier.setIntValue(intValue);
        if (lifeTime > 0) {
            verifier.setPassTime(ContextUtils.getContextTime() + lifeTime);
        }

        return verifier;
    }

    /**
     * 添加验证
     */
    @Transaction
    public JVerifier persistVerifier(Object dist, String tag, String value, long lifeTime) {
        JVerifier verifier = createVerifier(randVerifierId(dist), tag, value, 0, lifeTime);
        BeanDao.getSession().persist(verifier);
        return verifier;
    }

    @Transaction
    public JVerifier mergeVerifier(String id, String tag, String value, long lifeTime) {
        JVerifier verifier = createVerifier(id, tag, value, 0, lifeTime);
        BeanDao.getSession().merge(verifier);
        return verifier;
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

    public static JVerifier getOperationVerifier(Session session, String id, long idleTime, boolean unique) {
        if (idleTime < 1000) {
            idleTime = 1000;
        }

        JVerifier verifier = BeanDao.loadReal(session, JVerifier.class, id, unique ? LockMode.NONE : LockMode.PESSIMISTIC_WRITE);
        long contextTime = ContextUtils.getContextTime();
        if (verifier != null) {
            if (verifier.getPassTime() > contextTime) {
                if (unique) {
                    return null;
                }
            }

            QueryDaoUtils.createQueryArray(session, "DELETE FROM JVerifier o WHERE o.id = ? AND o.passTime = ?", verifier.getId(), verifier.getPassTime()).executeUpdate();
            session.clear();
        }

        verifier = new JVerifier();
        verifier.setId(id);
        verifier.setPassTime(contextTime + idleTime);
        try {
            session.persist(verifier);
            session.flush();

        } catch (ConstraintViolationException e) {
            session.clear();
            if (unique) {
                return null;
            }

            verifier = BeanDao.loadReal(session, JVerifier.class, id, LockMode.PESSIMISTIC_WRITE);
        }

        return verifier;
    }

    public static long getOperationIdleTime(Session session, JVerifier verifier, String id) {
        if (verifier != null) {
            return 0;
        }

        verifier = session.get(JVerifier.class, id);
        long passTime = verifier.getPassTime() - ContextUtils.getContextTime();
        return passTime < 1 ? 1 : passTime;
    }

    public static void passOperation(Session session, long passTime, JVerifier verifier) {
        QueryDaoUtils.createQueryArray(session, "UPDATE JVerifier o SET o.passTime = ? WHERE o.id = ? AND o.passTime = ?", passTime, verifier.getId(), verifier.getPassTime()).executeUpdate();
        verifier.setPassTime(passTime);
    }

    public static void doneOperation(Session session, JVerifier verifier, String tag, String value, int intValue) {
        QueryDaoUtils.createQueryArray(session, "UPDATE JVerifier o SET o.tag = ?, o.value = ?, o.intValue = ? WHERE o.id = ? AND o.passTime = ?", tag, value, intValue, verifier.getId(), verifier.getPassTime()).executeUpdate();
    }

    public static boolean isOperationCount(String id, int maxCount) {
        if (maxCount <= -1) {
            return false;
        }

        if (maxCount == 0 || id == null) {
            return true;
        }

        JVerifier verifier = BeanService.ME.get(JVerifier.class, id);
        return verifier == null || verifier.getPassTime() <= ContextUtils.getContextTime() ? false : true;
    }

    public static boolean isOperationCount(String address, String tag, int maxCount) {
        return isOperationCount(KernelString.isEmpty(address) ? null : (address + '@' + tag), maxCount);
    }

    @Transaction
    public JVerifier doneOperationCount(String id, long idleTime) {
        Session session = BeanDao.getSession();
        JVerifier verifier = getOperationVerifier(session, id, idleTime, false);
        verifier.setIntValue(verifier.getIntValue() + 1);
        session.merge(verifier);
        return verifier;
    }

    public static boolean doneOperationCount(String id, long idleTime, int maxCount) {
        if (maxCount <= -1) {
            return false;
        }

        if (maxCount == 0 || id == null) {
            return true;
        }

        return VerifierService.ME.doneOperationCount(id, idleTime).getIntValue() > maxCount;
    }

    public static boolean doneOperationCount(String address, String tag, long idleTime, int maxCount) {
        return doneOperationCount(KernelString.isEmpty(address) ? null : (address + '@' + tag), idleTime, maxCount);
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
                if (value.getValue() == sessionFactory && IPassClear.class.isAssignableFrom(value.getClass())) {
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

        // 清理一天前的
        long passTime = ContextUtils.getContextTime() - clearBeforeDelay;
        Session session = BeanDao.getSession();
        for (Entry<String, CrudEntity> entry : nameMapCrudEntity.entrySet()) {
            try {
                if (entry.getValue() != null) {
                    Iterator<Object> iterator = QueryDaoUtils.createQueryArray(session,
                            "SELECT o FROM " + entry.getKey() + " o WHERE o.passTime > 0 AND o.passTime < ?",
                            passTime).iterate();
                    while (iterator.hasNext()) {
                        Object entity = iterator.next();
                        try {
                            CrudUtils.crud(Crud.DELETE, true, null, entry.getValue().getJoEntity(), entity, null, null);

                        } catch (Exception e) {
                            LOGGER.error("clear expired verifier " + entry.getKey() + " crud " + entity + " error", e);
                            session.clear();
                        }
                    }

                } else {
                    QueryDaoUtils.createQueryArray(session,
                            "DELETE FROM " + entry.getKey() + " o WHERE o.passTime > 0 AND o.passTime < ?", passTime)
                            .executeUpdate();
                }

            } catch (Throwable e) {
                LOGGER.error("clear expired verifier " + entry.getKey() + " error", e);
            }
        }
    }
}
