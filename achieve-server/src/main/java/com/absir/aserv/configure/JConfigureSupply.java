/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-24 上午10:42:15
 */
package com.absir.aserv.configure;

import org.hibernate.Session;

import com.absir.aserv.crud.CrudSupply;
import com.absir.aserv.menu.value.MaSupply;
import com.absir.aserv.system.bean.JConfigure;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.bean.basis.Basis;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.orm.transaction.value.Transaction;

/**
 * @author absir
 * 
 */
@SuppressWarnings("unchecked")
@Bean
@Basis
@MaSupply(folder = "系统配置", name = "配置", method = "edit", icon = "cogs")
public class JConfigureSupply extends CrudSupply<JConfigureBase> {

	/** ME */
	public static final JConfigureSupply ME = BeanFactoryUtils.get(JConfigureSupply.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.CrudSupply#put(java.lang.Class,
	 * java.lang.Class)
	 */
	@Override
	protected void put(Class<?> type, Class<?> beanType) {
		JConfigureUtils.put((Class<? extends JConfigureBase>) type, (Class<? extends JConfigureBase>) beanType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#create(java.lang.String)
	 */
	@Override
	public Object create(String entityName) {
		return JConfigureUtils.getConfigure(getEntityClass(entityName));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#mergeEntity(java.lang.String,
	 * java.lang.Object, boolean)
	 */
	@Override
	public void mergeEntity(String entityName, Object entity, boolean create) {
		((JConfigureBase) entity).merge();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#deleteEntity(java.lang.String,
	 * java.lang.Object)
	 */
	@Transaction
	@Override
	public void deleteEntity(String entityName, Object entity) {
		Session session = BeanDao.getSession();
		for (JConfigure configure : ((JConfigureBase) entity).fieldMapConfigure.values()) {
			session.delete(session.merge(configure));
		}
	}
}
