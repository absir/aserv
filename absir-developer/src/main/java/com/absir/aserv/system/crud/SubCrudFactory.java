/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-16 上午11:01:29
 */
package com.absir.aserv.system.crud;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.CrudProperty;
import com.absir.aserv.crud.ICrudFactory;
import com.absir.aserv.crud.ICrudProcessor;
import com.absir.aserv.support.developer.JCrudField;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JaSubField;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAccessor;
import com.absir.core.util.UtilAccessor.Accessor;
import com.absir.orm.value.JoEntity;

/**
 * @author absir
 * 
 */
public class SubCrudFactory implements ICrudFactory, ICrudProcessor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudProcessor#crud(com.absir.aserv.crud.
	 * CrudProperty, java.lang.Object, com.absir.aserv.crud.CrudHandler,
	 * com.absir.aserv.system.bean.proxy.JiUserBase)
	 */
	@Override
	public void crud(CrudProperty crudProperty, Object entity, CrudHandler crudHandler, JiUserBase user) {
		Object property = crudProperty.get(entity);
		if (property == null) {
			crudProperty.set(entity, 0);
			return;
		}

		if (!(property instanceof Integer)) {
			return;
		}

		Object[] parameters = crudProperty.getjCrud().getParameters();
		if (parameters[0] == null) {
			return;
		}

		if (parameters[0] instanceof String) {
			Class<?> entityClass = crudProperty.getCrudEntity().getJoEntity().getEntityClass();
			parameters[0] = UtilAccessor.getAccessorObj(entity, (String) parameters[0], entityClass == null ? null : entityClass.getName());
		}

		try {
			Collection<?> subtable = (Collection<?>) ((Accessor) parameters[0]).get(entity);
			if ((Integer) property >= subtable.size()) {
				crudProperty.set(entity, 0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		crudProperty.set(entity, new Date());
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
		if (crudField.getjCrud().getParameters().length < 0) {
			return null;
		}

		if (KernelString.isEmpty((String) crudField.getjCrud().getParameters()[0])) {
			Field field = KernelReflect.declaredField(joEntity.getEntityClass(), crudField.getName());
			if (field == null) {
				return null;
			}

			JaSubField jaSubField = field.getAnnotation(JaSubField.class);
			if (jaSubField == null) {
				return null;
			}

			crudField.getjCrud().getParameters()[0] = jaSubField.value();
		}

		return this;
	}
}
