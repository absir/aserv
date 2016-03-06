/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-13 下午5:11:54
 */
package com.absir.aserv.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.absir.aop.AopImplDefine;
import com.absir.aop.AopMethodDefineAbstract;
import com.absir.aserv.data.value.DataQuery;
import com.absir.aserv.data.value.DataSession;
import com.absir.aserv.data.value.FirstResults;
import com.absir.aserv.data.value.MaxResults;
import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.core.BeanDefineDiscover;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelClass;

/**
 * @author absir
 * 
 */
@Basis
@Bean
public class DataQueryMethodDefine extends AopMethodDefineAbstract<DataQueryInterceptor, DataQueryDetached, String> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aop.AopMethodDefine#getAopInterceptor(com.absir.bean.basis.
	 * BeanDefine, java.lang.Object)
	 */
	@Override
	public DataQueryInterceptor getAopInterceptor(BeanDefine beanDefine, Object beanObject) {
		return BeanFactoryImpl.getBeanDefine(beanDefine, AopImplDefine.class) == null ? null : new DataQueryInterceptor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aop.AopMethodDefineAbstract#getVariable(com.absir.aop.
	 * AopInterceptorAbstract, com.absir.bean.basis.BeanDefine,
	 * java.lang.Object)
	 */
	@Override
	public String getVariable(DataQueryInterceptor aopInterceptor, BeanDefine beanDefine, Object beanObject) {
		DataSession session = beanDefine.getBeanType().getAnnotation(DataSession.class);
		return session == null ? null : session.value();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aop.AopMethodDefine#getAopInterceptor(java.lang.Object,
	 * java.lang.Class)
	 */
	@Override
	public DataQueryDetached getAopInterceptor(String variable, Class<?> beanType) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aop.AopMethodDefine#getAopInterceptor(java.lang.Object,
	 * java.lang.Object, java.lang.Class, java.lang.reflect.Method)
	 */
	@Override
	public DataQueryDetached getAopInterceptor(DataQueryDetached interceptor, String variable, Class<?> beanType, Method method) {
		DataQuery query = method.getAnnotation(DataQuery.class);
		if (query == null) {
			return null;
		}
		Class<?>[] parameterTypes = method.getParameterTypes();
		int length = parameterTypes.length;
		Annotation[][] annotations = method.getParameterAnnotations();
		int firstResultsPos = -1;
		int maxResultsPos = -1;
		for (int i = 0; i < length; i++) {
			if (KernelClass.isMatchableFrom(parameterTypes[i], int.class)) {
				if (KernelArray.getAssignable(annotations[i], FirstResults.class) != null) {
					firstResultsPos = i;

				} else if (KernelArray.getAssignable(annotations[i], MaxResults.class) != null) {
					maxResultsPos = i;
				}
			}
		}

		DataQueryDetached queryDetached = new DataQueryDetached(query.value(), query.nativeQuery(), variable, method.getReturnType(), query.cacheable(), query.excuteType(), query.aliasType(),
				parameterTypes, BeanDefineDiscover.paramterNames(method), firstResultsPos, maxResultsPos);
		return queryDetached;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
	 */
	@Override
	public int getOrder() {
		return 2048;
	}
}
