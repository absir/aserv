/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月10日 下午4:56:46
 */
package com.absir.aserv.advice;

import com.absir.core.kernel.KernelList.Orderable;

import java.lang.reflect.Method;

public interface IMethodAdvice<O> extends Orderable {

    public O matching(Class<?> beanType, Method method);

    public Object before(AdviceInvoker invoker, Object proxy, Method method, Object[] args, O advice) throws Throwable;

    public Object after(Object proxy, Object returnValue, Method method, Object[] args, Throwable e, O advice) throws Throwable;

}
