/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-5-23 下午4:00:57
 */
package com.absir.aserv.crud.bean;

import com.absir.aserv.crud.CrudSupply;
import com.absir.aserv.menu.value.MaSupply;
import com.absir.bean.basis.Basis;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelClass;

/**
 * @author absir
 * 
 */
@SuppressWarnings("unchecked")
@Bean
@Basis
@MaSupply(folder = "功能管理", name = "添加", method = "edit")
public class CrudBeanSupply extends CrudSupply<CrudBean> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#create(java.lang.String)
	 */
	@Override
	public Object create(String entityName) {
		return KernelClass.newInstance(getEntityClass(entityName));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#mergeEntity(java.lang.String,
	 * java.lang.Object, boolean)
	 */
	@Override
	public void mergeEntity(String entityName, Object entity, boolean create) {
		((CrudBean) entity).merge();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#deleteEntity(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void deleteEntity(String entityName, Object entity) {
	}
}
