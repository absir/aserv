/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月10日 下午5:28:07
 */
package com.absir.aserv.advice;

import com.absir.aop.AopInterceptor;
import com.absir.aop.AopInterceptorAbstract;
import com.absir.aop.AopMethodDefineAbstract;
import com.absir.aop.AopProxyHandler;
import com.absir.aserv.advice.AdviceMethodDefine.AopMethodInterceptor;
import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.core.kernel.KernelCollection;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("rawtypes")
@Basis
@Bean
public class AdviceMethodDefine extends AopMethodDefineAbstract<AopMethodInterceptor, IMethodAdvice[], Object> {

    @Inject(type = InjectType.Selectable)
    private IMethodAdvice[] methodAdvices;

    @Override
    public AopMethodInterceptor getAopInterceptor(BeanDefine beanDefine, Object beanObject) {
        return methodAdvices == null ? null : new AopMethodInterceptor();
    }

    @Override
    public IMethodAdvice[] getAopInterceptor(Object variable, Class<?> beanType) {
        return null;
    }

    @Override
    public IMethodAdvice[] getAopInterceptor(IMethodAdvice[] interceptor, Object variable, Class<?> beanType, Method method) {
        List<IMethodAdvice> advices = new ArrayList<IMethodAdvice>();
        for (IMethodAdvice advice : methodAdvices) {
            if (advice.matching(beanType, method)) {
                advices.add(advice);
            }
        }

        return advices.isEmpty() ? null : KernelCollection.toArray(advices, IMethodAdvice.class);
    }

    @Override
    public int getOrder() {
        return -1024;
    }

    public static class AopMethodInterceptor extends AopInterceptorAbstract<IMethodAdvice[]> {

        @Override
        public Object before(final Object proxy, final Iterator<AopInterceptor> iterator, final IMethodAdvice[] interceptor, final AopProxyHandler proxyHandler, final Method method,
                             final Object[] args, final MethodProxy methodProxy) throws Throwable {
            AdviceInvoker adviceInvoker = new AdviceInvoker() {

                private int index = 0;

                private int length = interceptor.length;

                @Override
                public Object invoke(Object value) throws Throwable {
                    Throwable ex = null;
                    if (index < length) {
                        try {
                            value = interceptor[index++].before(this, proxy, method, args);
                            if (value == AopProxyHandler.VOID) {
                                value = invoke(value);
                            }

                        } catch (Throwable e) {
                            ex = e;

                        } finally {
                            value = interceptor[--index].after(proxy, value, method, args, ex);
                        }

                    } else {
                        value = proxyHandler.invoke(proxyHandler, iterator, method, args, methodProxy);
                    }

                    return value;
                }
            };

            return adviceInvoker.invoke(null);
        }

        @Override
        public Object after(Object proxy, Object returnValue, IMethodAdvice[] interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, Throwable e) throws Throwable {
            return returnValue;
        }
    }
}
