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

/**
 * @author absir
 */
@SuppressWarnings("rawtypes")
@Basis
@Bean
public class DataCacheMethodDefine extends AopMethodDefineAbstract<DataCacheInterceptor, ObjectTemplate<Object>, String> {

    /**
     * DATA_CACHE_EMPTY
     */
    private static final Object DATA_CACHE_EMPTY = new Object();

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aop.AopMethodDefine#getAopInterceptor(com.absir.bean.basis.
     * BeanDefine, java.lang.Object)
     */
    @Override
    public DataCacheInterceptor getAopInterceptor(BeanDefine beanDefine, Object beanObject) {
        return BeanFactoryImpl.getBeanDefine(beanDefine, AopImplDefine.class) == null ? null : new DataCacheInterceptor();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aop.AopMethodDefine#getAopInterceptor(java.lang.Object,
     * java.lang.Class)
     */
    @Override
    public ObjectTemplate<Object> getAopInterceptor(String variable, Class<?> beanType) {
        DataCache dataCache = beanType.getAnnotation(DataCache.class);
        return dataCache == null || !dataCache.cacheable() ? null : new ObjectTemplate<Object>(DATA_CACHE_EMPTY);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aop.AopMethodDefine#getAopInterceptor(java.lang.Object,
     * java.lang.Object, java.lang.Class, java.lang.reflect.Method)
     */
    @Override
    public ObjectTemplate<Object> getAopInterceptor(ObjectTemplate<Object> interceptor, String variable, Class<?> beanType, Method method) {
        DataCache dataCache = beanType.getAnnotation(DataCache.class);
        return dataCache == null ? interceptor : dataCache.cacheable() ? interceptor == null ? new ObjectTemplate<Object>(DATA_CACHE_EMPTY) : interceptor : null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
     */
    @Override
    public int getOrder() {
        return 1024;
    }

    /**
     * @author absir
     */
    public static class DataCacheInterceptor extends AopInterceptorAbstract<ObjectTemplate<Object>> {

        /*
         * (non-Javadoc)
         *
         * @see com.absir.aop.AopInterceptor#before(java.lang.Object,
         * java.util.Iterator, java.lang.Object, com.absir.aop.AopProxyHandler,
         * java.lang.reflect.Method, java.lang.Object[],
         * net.sf.cglib.proxy.MethodProxy)
         */
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
