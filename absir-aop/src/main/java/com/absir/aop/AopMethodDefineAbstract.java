/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-24 下午3:57:00
 */
package com.absir.aop;

import com.absir.bean.basis.BeanDefine;

import java.lang.reflect.Method;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AopMethodDefineAbstract<T extends AopInterceptorAbstract, K, D> implements AopMethodDefine<T, K, D> {

    @Override
    public D getVariable(T aopInterceptor, BeanDefine beanDefine, Object beanObject) {
        return null;
    }

    @Override
    public boolean isEmpty(T aopInterceptor) {
        return !aopInterceptor.setunmodifiableMethodMapInterceptor();
    }

    @Override
    public void setAopInterceptor(K interceptor, T aopInterceptor, Class<?> beanType, Method method, Method beanMethod) {
        aopInterceptor.getMethodMapInterceptor().put(beanMethod, interceptor);
    }
}
