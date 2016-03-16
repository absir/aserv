/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-5-7 下午7:37:01
 */
package com.absir.aserv.data;

import com.absir.aop.*;
import com.absir.aserv.data.DataCacheMethodDefine.DataCacheInterceptor;
import com.absir.aserv.data.value.DataCache;
import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelLang.ObjectTemplate;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
@Basis
@Bean
public class DataCacheMethodDefine extends AopMethodDefineAbstract<DataCacheInterceptor, ObjectTemplate<Object>, String> {

    private static final Object DATA_CACHE_EMPTY = new Object();

    @Override
    public DataCacheInterceptor getAopInterceptor(BeanDefine beanDefine, Object beanObject) {
        return BeanFactoryImpl.getBeanDefine(beanDefine, AopImplDefine.class) == null ? null : new DataCacheInterceptor();
    }

    @Override
    public ObjectTemplate<Object> getAopInterceptor(String variable, Class<?> beanType) {
        DataCache dataCache = beanType.getAnnotation(DataCache.class);
        return dataCache == null || !dataCache.cacheable() ? null : new ObjectTemplate<Object>(DATA_CACHE_EMPTY);
    }

    @Override
    public ObjectTemplate<Object> getAopInterceptor(ObjectTemplate<Object> interceptor, String variable, Class<?> beanType, Method method) {
        DataCache dataCache = beanType.getAnnotation(DataCache.class);
        return dataCache == null ? interceptor : dataCache.cacheable() ? interceptor == null ? new ObjectTemplate<Object>(DATA_CACHE_EMPTY) : interceptor : null;
    }

    @Override
    public int getOrder() {
        return 1024;
    }

    public static class DataCacheInterceptor extends AopInterceptorAbstract<ObjectTemplate<Object>> {

        @Override
        public Object before(Object proxy, Iterator<AopInterceptor> iterator, ObjectTemplate<Object> interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy)
                throws Throwable {
            if (interceptor.object == DATA_CACHE_EMPTY) {
                interceptor.object = proxyHandler.invoke(proxy, iterator, method, args, methodProxy);
            }

            return interceptor.object;
        }
    }
}
