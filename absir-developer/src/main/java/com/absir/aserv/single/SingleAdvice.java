package com.absir.aserv.single;

import com.absir.aserv.advice.AdviceInvoker;
import com.absir.aserv.advice.IMethodAdvice;
import com.absir.aserv.system.bean.JVerifier;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.service.VerifierService;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Value;
import com.absir.client.helper.HelperJson;
import com.absir.context.core.ContextUtils;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by absir on 16/5/30.
 */
@Base
@Bean
public class SingleAdvice implements IMethodAdvice<String> {

    public static final SingleAdvice ME = BeanFactoryUtils.get(SingleAdvice.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(SingleAdvice.class);

    @Value("single.idle.time")
    protected long idleTime = 30000;

    @Value("single.delay.time")
    protected long delayTime = 15000;

    protected ConcurrentHashMap<JVerifier, Boolean> verifierQueue;

    @Inject
    public void init() {
        ContextUtils.getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(1000);
                        try {
                            ME.checkSingle();

                        } catch (Exception e) {
                            LOGGER.error("single check error", e);
                        }
                    }

                } catch (InterruptedException e) {
                }
            }
        });
    }

    public String getMethodSingleId(Object proxy, Method method, Object[] args) {
        return method.toString() + "@" + HelperJson.encodeNull(args);
    }

    @Override
    public String matching(Class<?> beanType, Method method) {
        JaSingle single = BeanConfigImpl.getMethodAnnotation(method, JaSingle.class, true);
        return single == null ? null : single.value();
    }

    @Override
    public Object before(AdviceInvoker invoker, Object proxy, Method method, Object[] args, String advice) throws Throwable {
        String singleId = advice.length() == 0 ? getMethodSingleId(proxy, method, args) : advice;
        JVerifier verifier = ME.entrySingle(singleId);
        if (verifier != null) {
            try {
                return invoker.invoke(proxy);

            } finally {
                ME.leftSingle(verifier);
            }
        }

        return null;
    }

    @Override
    public Object after(Object proxy, Object returnValue, Method method, Object[] args, Throwable e, String advice) throws Throwable {
        return returnValue;
    }

    @Override
    public int getOrder() {
        return -128;
    }

    @Transaction
    protected void checkSingle() {
        Session session = BeanDao.getSession();
        if (verifierQueue != null) {
            long contextTime = ContextUtils.getContextTime();
            long idle = contextTime + idleTime;
            long delay = contextTime + delayTime;
            for (JVerifier verifier : verifierQueue.keySet()) {
                if (verifier.getPassTime() <= delay) {
                    try {
                        VerifierService.passOperation(session, idle, verifier);

                    } catch (Exception e) {
                        LOGGER.error("single error " + verifier.getId(), e);
                    }
                }
            }
        }
    }

    public String getSingeVerifyId(String singleId) {
        return "SINGLE@" + singleId;
    }

    @Transaction
    public JVerifier entrySingle(String singleId) {
        if (verifierQueue == null) {
            synchronized (this) {
                if (verifierQueue == null) {
                    verifierQueue = new ConcurrentHashMap<JVerifier, Boolean>();
                }
            }
        }

        singleId = getSingeVerifyId(singleId);
        Session session = BeanDao.getSession();
        JVerifier verifier = VerifierService.getOperationVerifier(session, singleId, idleTime, true);
        if (verifier != null) {
            verifierQueue.put(verifier, Boolean.TRUE);
        }

        return verifier;
    }

    @Transaction
    public void leftSingle(JVerifier verifier) {
        Session session = BeanDao.getSession();
        if (verifierQueue != null) {
            verifierQueue.remove(verifier);
        }

        VerifierService.passOperation(session, ContextUtils.getContextTime(), verifier);
    }
}
