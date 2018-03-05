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
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Started;
import com.absir.bean.inject.value.Value;
import com.absir.context.core.ContextService;
import com.absir.context.core.ContextUtils;
import com.absir.context.schedule.cron.CronFixDelayRunnable;
import com.absir.context.schedule.value.Schedule;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAbsir;
import com.absir.core.util.UtilLinked;
import com.absir.orm.hibernate.SessionFactoryBean;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.value.Transaction;
import com.absir.orm.value.JoEntity;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unchecked")
@Base
@Bean
public class VerifierService extends ContextService {

    public static final VerifierService ME = BeanFactoryUtils.get(VerifierService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(VerifierService.class);

    @Value("verifier.clear")
    private long clearFixDelay = 8 * 3600000;

    @Value("verifier.before")
    private long clearBeforeDelay = UtilAbsir.DAY_TIME;

    private Map<String, CrudEntity> nameMapCrudEntity;

    private UtilLinked<PassVerifier> passVerifierUtilLinked;

    protected static class PassVerifier {

        protected boolean db;

        protected long uPassTime;

        protected String id;

        protected long passTime;

    }

    protected void setNameMapCrudEntity(Map<String, CrudEntity> nameMapCrudEntity) {
        this.nameMapCrudEntity = nameMapCrudEntity;
    }

    @Override
    public void step(long contextTime) {
    }

    @Schedule(fixedDelay = 10000)
    @Async(notifier = true)
    @Transaction
    protected void clearPassVerifier() {
        if (passVerifierUtilLinked == null) {
            return;
        }

        passVerifierUtilLinked.syncAdds();
        if (!passVerifierUtilLinked.getList().isEmpty()) {
            return;
        }

        Session session = BeanDao.getSession();
        Iterator<PassVerifier> iterator = passVerifierUtilLinked.iterator();
        while (iterator.hasNext()) {
            PassVerifier passVerifier = iterator.next();
            try {
                doPassVerifier(session, passVerifier.db, passVerifier.uPassTime, passVerifier.id, passVerifier.passTime);
                iterator.remove();

            } catch (Exception e) {
                Environment.throwable(e);
                return;
            }
        }
    }

    protected void doPassVerifier(Session session, boolean db, long uPassTime, String id, long passTime) {
        if (uPassTime > 0) {
            if (passTime <= 0) {
                QueryDaoUtils.createQueryArray(session, "UPDATE JVerifier o SET o.passTime = ? WHERE o.id = ?", uPassTime, id).executeUpdate();

            } else {
                QueryDaoUtils.createQueryArray(session, "UPDATE JVerifier o SET o.passTime = ? WHERE o.id = ? AND o.passTime = ?", uPassTime, id, passTime).executeUpdate();
            }

            session.clear();

        } else {
            if (passTime <= 0) {
                QueryDaoUtils.createQueryArray(session, "DELETE FROM JVerifier o WHERE o.id = ?", id).executeUpdate();
                session.clear();

            } else {
                dbDeleteVerifier(session, id, passTime);
            }
        }
    }

    /**
     * 开启服务
     */
    @Started
    protected void startService() {
        SessionFactoryBean sessionFactoryBean = SessionFactoryUtils.get();
        SessionFactory sessionFactory = sessionFactoryBean.getSessionFactory();
        if (sessionFactory != null) {
            final Map<String, CrudEntity> nameMapCrudEntity = new HashMap<String, CrudEntity>();
            for (Map.Entry<String, Map.Entry<Class<?>, SessionFactory>> entry : sessionFactoryBean
                    .getJpaEntityNameMapEntityClassFactory().entrySet()) {
                Map.Entry<Class<?>, SessionFactory> value = entry.getValue();
                if (value.getValue() == sessionFactory && IPassClear.class.isAssignableFrom(value.getClass())) {
                    JoEntity joEntity = new JoEntity(entry.getKey(), entry.getValue().getKey());
                    nameMapCrudEntity.put(entry.getKey(), CrudUtils.getCrudEntity(joEntity));
                }
            }

            if (!nameMapCrudEntity.isEmpty()) {
                setNameMapCrudEntity(nameMapCrudEntity);
                ContextUtils.getScheduleFactory().addRunnable(new CronFixDelayRunnable(new Runnable() {

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
        for (Map.Entry<String, CrudEntity> entry : nameMapCrudEntity.entrySet()) {
            try {
                if (entry.getValue() != null) {
                    Iterator<Object> iterator = QueryDaoUtils.createQueryArray(session,
                            "SELECT o FROM " + entry.getKey() + " o WHERE o.passTime > 0 AND o.passTime < ?",
                            passTime).iterate();
                    while (iterator.hasNext()) {
                        Object entity = iterator.next();
                        try {
                            CrudUtils.crud(JaCrud.Crud.DELETE, true, null, entry.getValue().getJoEntity(), entity, null, null);

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

    protected void dbDeleteVerifier(Session session, String id, Long passTime) {
        QueryDaoUtils.createQueryArray(session, "DELETE FROM JVerifier o WHERE o.id = ? AND o.passTime = ?", id, passTime).executeUpdate();
        session.clear();
    }

    //* < 0 idleTime; > 0 passTime; 0 incr >= incrMax
    public long setVerifier(String id, boolean unique, long lifeTime, String tag, String value, int intValue, int incrMax) {
        return ME.dbSetVerifier(id, unique, lifeTime, tag, value, intValue, incrMax);
    }

    @Transaction
    public long dbSetVerifier(String id, boolean unique, long lifeTime, String tag, String value, int intValue, int incrMax) {
        Session session = BeanDao.getSession();
        if (lifeTime < 1000) {
            lifeTime = 1000;
        }

        boolean incr = incrMax > 0;
        JVerifier verifier;
        long contextTime = ContextUtils.getContextTime();
        if (unique) {
            verifier = BeanDao.loadReal(session, JVerifier.class, id, LockMode.PESSIMISTIC_WRITE);
            if (verifier != null) {
                if (verifier.getPassTime() > contextTime) {
                    if (incr) {
                        if (verifier.getIntValue() >= incrMax) {
                            return 0;
                        }

                    } else {
                        return contextTime - verifier.getPassTime();
                    }

                } else {
                    verifier.setIntValue(0);
                }

                verifier.setPassTime(contextTime + lifeTime);
                verifier.setTag(tag);
                verifier.setValue(value);
                if (incr) {
                    verifier.setIntValue(verifier.getIntValue() + 1);

                } else {
                    verifier.setIntValue(intValue);
                }

                session.merge(verifier);
                return verifier.getPassTime();
            }

            verifier = new JVerifier();
            verifier.setId(id);
            verifier.setPassTime(contextTime + lifeTime);
            verifier.setTag(tag);
            verifier.setValue(value);
            if (incr) {
                verifier.setIntValue(1);

            } else {
                verifier.setIntValue(intValue);
            }

            try {
                session.persist(verifier);
                session.flush();

            } catch (RuntimeException e) {
                SessionFactoryUtils.throwNoConstraintViolationException(e);
                session.clear();
                if (unique) {
                    return -lifeTime;
                }
            }

        } else {
            verifier = incr ? BeanDao.loadReal(session, JVerifier.class, id, LockMode.NONE) : null;
            if (verifier == null) {
                verifier = new JVerifier();
                verifier.setId(id);

            } else if (incr && verifier.getIntValue() >= incrMax) {
                return 0;
            }

            verifier.setPassTime(contextTime + lifeTime);
            verifier.setTag(tag);
            verifier.setValue(value);
            if (incr) {
                verifier.setIntValue(verifier.getIntValue() + 1);

            } else {
                verifier.setIntValue(intValue);
            }

            session.merge(verifier);
            session.flush();
        }

        return verifier.getPassTime();
    }

    public long setVerifierWith(String id, boolean unique, long lifeTime, boolean errDelete, KernelLang.GetTemplate<Boolean, VerifierMerge> withDel) {
        return ME.dbSetVerifierWith(id, unique, lifeTime, errDelete, withDel);
    }

    @Transaction
    public long dbSetVerifierWith(String id, boolean unique, long lifeTime, boolean errDelete, KernelLang.GetTemplate<Boolean, VerifierMerge> withDel) {
        long passTime = dbSetVerifier(id, unique, lifeTime, null, null, 0, 0);
        if (passTime > 0) {
            if (unique && errDelete) {
                try {
                    if (KernelLang.isBoolean(withDel.getWith(MERGE))) {
                        passTime = 1;

                    } else {
                        errDelete = false;
                        passTime = 0;
                    }

                } finally {
                    if (errDelete) {
                        dbDeleteVerifier(BeanDao.getSession(), id, passTime);
                    }
                }

            } else {
                if (KernelLang.isBoolean(withDel.getWith(MERGE))) {
                    passTime = 1;

                } else {
                    passTime = 0;
                }
            }
        }

        return passTime;
    }

    public interface VerifierMerge {

        public boolean set(String id, String tag, String value, int intValue);
    }

    protected static final VerifierMerge MERGE = new VerifierMerge() {
        @Override
        public boolean set(String id, String tag, String value, int intValue) {
            return ME.setVerifierMerge(id, tag, value, intValue);
        }
    };

    protected boolean setVerifierMerge(String id, String tag, String value, int intValue) {
        return ME.dbSetVerifierMerge(id, tag, value, intValue);
    }

    protected boolean dbSetVerifierMerge(String id, String tag, String value, int intValue) {
        Session session = BeanDao.getSession();
        JVerifier verifier = session.get(JVerifier.class, id);
        if (verifier == null) {
            verifier = new JVerifier();
            verifier.setId(id);
        }

        verifier.setTag(tag);
        verifier.setValue(value);
        verifier.setIntValue(intValue);
        session.merge(verifier);
        session.flush();
        return true;
    }

    public boolean passVerifier(long uPassTime, String id, long passTime, boolean guarantee) {
        return ME.dbPassVerifier(uPassTime, id, passTime, guarantee);
    }

    @Transaction
    protected boolean dbPassVerifier(long uPassTime, String id, long passTime, boolean guarantee) {
        Session session = BeanDao.getSession();
        try {
            doPassVerifier(session, true, uPassTime, id, passTime);
            return true;

        } catch (Throwable e) {
            if (guarantee) {
                if (passVerifierUtilLinked == null) {
                    synchronized (this) {
                        if (passVerifierUtilLinked == null) {
                            passVerifierUtilLinked = new UtilLinked<PassVerifier>();
                        }
                    }
                }

                PassVerifier passVerifier = new PassVerifier();
                passVerifier.db = true;
                passVerifier.uPassTime = uPassTime;
                passVerifier.id = id;
                passVerifier.passTime = passTime;
                passVerifierUtilLinked.add(passVerifier);
            }

            Environment.throwable(e);
        }

        return false;
    }

    //* 0正常 -1验证码不存在 -2验证码过期 -3验证码错误 -4 等级太低
    public int validateVerifier(String id, String tag, String value, int intValue, int level, boolean delete) {
        return ME.dbValidateVerifier(id, tag, value, intValue, level, delete);
    }

    @Transaction(readOnly = true)
    public int dbValidateVerifier(String id, String tag, String value, int intValue, int level, boolean delete) {
        Session session = BeanDao.getSession();
        JVerifier verifier = BeanDao.get(session, JVerifier.class, id);
        if (verifier == null) {
            return -1;
        }

        if (verifier.getPassTime() <= ContextUtils.getContextTime()) {
            return -2;
        }

        if ((tag != null && !KernelObject.equals(tag, verifier.getTag()))
                || (value != null && !KernelObject.equals(value, verifier.getValue()))
                || (intValue != 0 && intValue != verifier.getIntValue())) {
            return -3;
        }

        if (level != 0 && verifier.getIntValue() < level) {
            return -4;
        }

        if (delete) {
            session.delete(verifier);
        }

        return 0;
    }

    public static String getVerifierId(String address, String tag) {
        return KernelString.isEmpty(address) ? tag : (address + '@' + tag);
    }

    public static boolean couldOperation(String id, int maxTimes) {
        if (maxTimes < 0) {
            return false;
        }

        if (maxTimes == 0 || id == null) {
            return true;
        }

        return ME.validateVerifier(id, null, null, 0, maxTimes, false) == -4;
    }

    public static boolean doOperationIncr(String id, boolean unique, long idleTime, int maxTimes) {
        return ME.setVerifier(id, unique, idleTime, null, null, 0, maxTimes) > 0;
    }

}
