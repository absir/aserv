/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-23 上午10:57:02
 */
package com.absir.aserv.configure.xls;

import java.io.Serializable;
import java.util.Collection;

import com.absir.aserv.crud.CrudSupply;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.menu.value.MaSupply;
import com.absir.bean.basis.Basis;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.core.base.IBase;
import com.absir.core.kernel.KernelClass;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes" })
@Bean
@Basis
@MaSupply
public class XlsCrudSupply extends CrudSupply<XlsBase> {
	
	/** ME */
	public static final XlsCrudSupply ME = BeanFactoryUtils.get(XlsCrudSupply.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.crud.ICrudSupply#getIdentifierName(java.lang.String)
	 */
	@Override
	public String getIdentifierName(String entityName) {
		return "id";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.crud.ICrudSupply#getIdentifierType(java.lang.String)
	 */
	@Override
	public Class<? extends Serializable> getIdentifierType(String entityName) {
		return XlsUtils.getXlsDao(getEntityClass(entityName)).getIdType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#getIdentifier(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public Object getIdentifier(String entityName, Object entity) {
		return ((IBase) entity).getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.CrudSupply#findAll(java.lang.String)
	 */
	@Override
	public Collection findAll(String entityName) {
		return XlsUtils.getXlsDao(getEntityClass(entityName)).getAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#get(java.lang.String,
	 * java.io.Serializable, com.absir.aserv.jdbc.JdbcCondition)
	 */
	@Override
	public Object get(String entityName, Serializable id, JdbcCondition jdbcCondition) {
		return XlsUtils.getXlsDao(getEntityClass(entityName)).get(id);
	}

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
