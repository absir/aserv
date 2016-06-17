/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-24 下午5:21:11
 */
package com.absir.async;

import com.absir.aop.AopMethodDefineAbstract;
import com.absir.async.value.Async;
import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.core.BeanConfigImpl;

import java.lang.reflect.Method;

@Basis
public class AsyncMethodDefine extends AopMethodDefineAbstract<AysncInterceptor, Async, Object> {

    @Override
    public AysncInterceptor getAopInterceptor(BeanDefine beanDefine, Object beanObject) {
        return new AysncInterceptor();
    }

    @Override
    public Async getAopInterceptor(Object variable, Class<?> beanType) {
        return BeanConfigImpl.getTypeAnnotation(beanType, Async.class);
    }

    @Override
    public Async getAopInterceptor(Async interceptor, Object variable, Class<?> beanType, Method method) {
        Async async = BeanConfigImpl.getMethodAnnotation(method, Async.class, true);
        return async == null ? interceptor : async;
    }

    @Override
    public void setAopInterceptor(Async interceptor, AysncInterceptor aopInterceptor, Class<?> beanType, Method method, Method beanMethod) {
        aopInterceptor.getMethodMapInterceptor().put(beanMethod,
                interceptor.notifier() ? new AysncRunnableNotifier(interceptor.timeout(), interceptor.thread()) : new AysncRunnable(interceptor.timeout(), interceptor.thread()));
    }

    @Override
    public int getOrder() {
        return -255;
    }
}
