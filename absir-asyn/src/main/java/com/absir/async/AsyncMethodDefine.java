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

import java.lang.reflect.Method;

/**
 * @author absir
 */
@Basis
public class AsyncMethodDefine extends AopMethodDefineAbstract<AysncInterceptor, Async, Object> {

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aop.AopMethodDefine#getAopInterceptor(com.absir.bean.basis.
     * BeanDefine, java.lang.Object)
     */
    @Override
    public AysncInterceptor getAopInterceptor(BeanDefine beanDefine, Object beanObject) {
        return new AysncInterceptor();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aop.AopMethodDefine#getAopInterceptor(java.lang.Object,
     * java.lang.Class)
     */
    @Override
    public Async getAopInterceptor(Object variable, Class<?> beanType) {
        return beanType.getAnnotation(Async.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aop.AopMethodDefine#getAopInterceptor(java.lang.Object,
     * java.lang.Object, java.lang.Class, java.lang.reflect.Method)
     */
    @Override
    public Async getAopInterceptor(Async interceptor, Object variable, Class<?> beanType, Method method) {
        Async async = method.getAnnotation(Async.class);
        return async == null ? interceptor : async;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aop.AopMethodDefine#setAopInterceptor(java.lang.Object,
     * com.absir.aop.AopInterceptor, java.lang.Class, java.lang.reflect.Method,
     * java.lang.reflect.Method)
     */
    @Override
    public void setAopInterceptor(Async interceptor, AysncInterceptor aopInterceptor, Class<?> beanType, Method method, Method beanMethod) {
        aopInterceptor.getMethodMapInterceptor().put(beanMethod,
                interceptor.notifier() ? new AysncRunableNotifier(interceptor.timeout(), interceptor.thread()) : new AysncRunable(interceptor.timeout(), interceptor.thread()));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
     */
    @Override
    public int getOrder() {
        return -255;
    }
}
