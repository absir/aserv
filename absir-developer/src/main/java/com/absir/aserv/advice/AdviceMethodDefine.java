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
public class AdviceMethodDefine extends AopMethodDefineAbstract<AopMethodInterceptor, AdviceMethodDefine.MethodAdviceO[], Object> {

    @Inject(type = InjectType.Selectable)
    private IMethodAdvice[] methodAdvices;

    @Override
    public AopMethodInterceptor getAopInterceptor(BeanDefine beanDefine, Object beanObject) {
        return methodAdvices == null ? null : new AopMethodInterceptor();
    }

    @Override
    public MethodAdviceO[] getAopInterceptor(Object variable, Class<?> beanType) {
        return null;
    }

    @Override
    public MethodAdviceO[] getAopInterceptor(MethodAdviceO[] interceptor, Object variable, Class<?> beanType, Method method) {
        List<MethodAdviceO> advices = new ArrayList<MethodAdviceO>();
        for (IMethodAdvice advice : methodAdvices) {
            Object obj = advice.matching(beanType, method);
            if (obj != null) {
                MethodAdviceO adviceO = new MethodAdviceO();
                adviceO.advice = advice;
                adviceO.obj = obj;
                advices.add(adviceO);
            }
        }

        return advices.isEmpty() ? null : KernelCollection.toArray(advices, MethodAdviceO.class);
    }

    @Override
    public int getOrder() {
        return -1024;
    }

    protected static class MethodAdviceO {

        protected IMethodAdvice advice;

        protected Object obj;
    }

    public static class AopMethodInterceptor extends AopInterceptorAbstract<MethodAdviceO[]> {

        @Override
        public Object before(final Object proxy, final Iterator<AopInterceptor> iterator, final MethodAdviceO[] interceptor, final AopProxyHandler proxyHandler, final Method method,
                             final Object[] args, final MethodProxy methodProxy) throws Throwable {
            AdviceInvoker adviceInvoker = new AdviceInvoker() {

                private int index = 0;

                private int length = interceptor.length;

                @Override
                public Object invoke(Object value) throws Throwable {
                    Throwable ex = null;
                    if (index < length) {
                        try {
                            MethodAdviceO adviceO = interceptor[index++];
                            value = adviceO.advice.before(this, proxy, method, args, adviceO.obj);
                            if (value == AopProxyHandler.VOID) {
                                value = invoke(value);
                            }

                        } catch (Throwable e) {
                            ex = e;

                        } finally {
                            MethodAdviceO adviceO = interceptor[--index];
                            value = adviceO.advice.after(proxy, value, method, args, ex, adviceO.obj);
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
        public Object after(Object proxy, Object returnValue, MethodAdviceO[] interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, Throwable e) throws Throwable {
            return returnValue;
        }
    }
}
