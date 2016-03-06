/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-7-9 下午12:41:10
 */
package com.absir.aserv.system.crud;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.CrudProperty;
import com.absir.aserv.crud.ICrudFactory;
import com.absir.aserv.crud.ICrudProcessor;
import com.absir.aserv.support.developer.JCrudField;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelObject;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.value.JoEntity;

/**
 * @author absir
 * 
 */
public class RecycleCrudFactory implements ICrudFactory, ICrudProcessor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudProcessor#crud(com.absir.aserv.crud.
	 * CrudProperty, java.lang.Object, com.absir.aserv.crud.CrudHandler,
	 * com.absir.aserv.system.bean.proxy.JiUserBase)
	 */
	@Override
	public void crud(CrudProperty crudProperty, Object entity, CrudHandler handler, JiUserBase user) {
		String recycleName = crudProperty.getCrudEntity().getJoEntity().getEntityName() + "Recycle";
		Class<?> recycleClass = SessionFactoryUtils.getEntityClass(recycleName);
		if (recycleClass != null) {
			Object recycle = KernelClass.newInstance(recycleClass);
			if (recycle != null) {
				KernelObject.copy(entity, recycle);
			}

			CrudServiceUtils.merge(recycleName, handler.getCrudRecord(), recycle, true, user, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.crud.ICrudFactory#getProcessor(com.absir.aserv.support
	 * .entity.value.JoEntity, com.absir.aserv.support.developer.JCrudField)
	 */
	@Override
	public ICrudProcessor getProcessor(JoEntity joEntity, JCrudField crudField) {
		return this;
	}
}
