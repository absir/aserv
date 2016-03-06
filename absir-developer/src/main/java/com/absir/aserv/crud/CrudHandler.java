/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-25 下午4:01:35
 */
package com.absir.aserv.crud;

import java.util.HashMap;
import java.util.Map;

import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.core.kernel.KernelObject;

/**
 * @author absir
 * 
 */
public abstract class CrudHandler {

	/** crud */
	protected JaCrud.Crud crud;

	/** crudRecord */
	protected Map<String, Object> crudRecord;

	/** filter */
	protected PropertyFilter filter;

	/** crudEntity */
	protected CrudEntity crudEntity;

	/** root */
	protected Object root;

	/** rootEntity */
	protected Object rootEntity;

	/** entityMap */
	protected Map<String, Object> entityMap;

	/** propertyPath */
	protected String propertyPath;

	/** entity */
	protected Object entity;

	/** created */
	protected boolean created;

	/**
	 * @param crud
	 * @param filter
	 * @param crudEntity
	 * @param root
	 */
	public CrudHandler(JaCrud.Crud crud, Map<String, Object> crudRecord, PropertyFilter filter, CrudEntity crudEntity, Object root) {
		this.crud = crud;
		this.filter = filter;
		this.crudEntity = crudEntity;
		this.root = root;
	}

	/**
	 * @return the crud
	 */
	public JaCrud.Crud getCrud() {
		return crud;
	}

	/**
	 * @return the crudRecord
	 */
	public Map<String, Object> getCrudRecord() {
		return crudRecord;
	}

	/**
	 * @return the filter
	 */
	public PropertyFilter getFilter() {
		return filter;
	}

	/**
	 * @return the crudEntity
	 */
	public CrudEntity getCrudEntity() {
		return crudEntity;
	}

	/**
	 * @return the root
	 */
	public Object getRoot() {
		return root;
	}

	/**
	 * @return the rootEntity
	 */
	public Object getRootEntity() {
		if (rootEntity == null) {
			if (crudEntity.getJoEntity().getEntityName() == null) {
				if (crudEntity.getJoEntity().getEntityClass() == null) {
					return (rootEntity = root);
				}
			}

			rootEntity = BeanDao.getLoadedEntity(null, crudEntity.getJoEntity().getEntityName(), root);
			if (rootEntity == null) {
				rootEntity = root;
			}
		}

		return rootEntity;
	}

	/**
	 * @return the Entity
	 */
	public Object getEntity() {
		if (propertyPath == filter.getPropertyPath()) {
			if (entity != null) {
				return entity;
			}

		} else {
			propertyPath = filter.getPropertyPath();
		}

		if (entityMap == null) {
			entityMap = new HashMap<String, Object>();

		} else {
			entity = entityMap.get(propertyPath);
			if (entity != null) {
				return entity;
			}
		}

		entity = KernelObject.expressGetter(getRootEntity(), propertyPath);
		entityMap.put(propertyPath, entity);
		return entity;
	}

	/**
	 * @return
	 */
	public boolean isCreate() {
		if (created) {
			return false;
		}

		created = true;
		return crud == Crud.CREATE;
	}

	/**
	 * @author absir
	 * 
	 */
	protected static abstract class CrudInvoker extends CrudHandler {

		/**
		 * @param crud
		 * @param filter
		 * @param crudEntity
		 * @param root
		 */
		public CrudInvoker(Crud crud, Map<String, Object> crudRecord, PropertyFilter filter, CrudEntity crudEntity, Object root) {
			super(crud, crudRecord, filter, crudEntity, root);
		}

		/**
		 * @param crud
		 * @param crudProperty
		 * @return
		 */
		public boolean isSupport(Crud crud, CrudProperty crudProperty) {
			if (filter.allow(crudProperty.getInclude(), crudProperty.getExclude()) && isSupport(crudProperty)) {
				Crud[] cruds = crudProperty.getjCrud().getCruds();
				if (cruds == CrudEntity.ALL || KernelArray.contain(cruds, crud)) {
					return true;
				}
			}

			return false;
		}

		/**
		 * @param crudProperty
		 * @return
		 */
		protected abstract boolean isSupport(CrudProperty crudProperty);

		/**
		 * @param crudProperty
		 * @param entity
		 */
		public abstract void crudInvoke(CrudProperty crudProperty, Object entity);
	}
}
