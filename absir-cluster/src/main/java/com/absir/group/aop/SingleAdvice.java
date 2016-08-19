package com.absir.group.aop;

import com.absir.aserv.advice.AdviceInvoker;
import com.absir.aserv.advice.IMethodAdvice;
import com.absir.aserv.single.JaSingle;
import com.absir.aserv.system.bean.JVerifier;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.inject.value.Bean;
import com.absir.client.helper.HelperJson;
import com.absir.group.service.ClusterService;
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

    protected static final Logger LOGGER = LoggerFactory.getLogger(SingleAdvice.class);

    protected ConcurrentHashMap<JVerifier, Boolean> verifierQueue;

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
        JVerifier verifier = ClusterService.ME.enterSingle(singleId);
        if (verifier != null) {
            try {
                return invoker.invoke(proxy);

            } finally {
                ClusterService.ME.exitSingle(verifier);
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

}
