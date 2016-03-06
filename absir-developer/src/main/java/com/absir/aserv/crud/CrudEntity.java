/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-8 下午2:44:47
 */
package com.absir.aserv.crud;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.orm.value.JoEntity;

/**
 * @author absir
 * 
 */
public class CrudEntity {

	/** ALL */
	public static final JaCrud.Crud[] ALL = new JaCrud.Crud[] { JaCrud.Crud.CREATE, JaCrud.Crud.UPDATE, JaCrud.Crud.DELETE };

	/** joEntity */
	protected JoEntity joEntity;

	/** crudPropertyReferences */
	protected List<CrudPropertyReference> crudPropertyReferences;

	/** curCrudProperties */
	protected List<CrudProperty> crudProperties;

	/** crudEntityNone */
	protected boolean crudEntityNone;

	/**
	 * @param crudProperty
	 */
	protected void addCrudPropertyReference(CrudPropertyReference crudPropertyReference) {
		if (crudPropertyReferences == null) {
			crudPropertyReferences = new ArrayList<CrudPropertyReference>();
		}

		crudPropertyReferences.add(crudPropertyReference);
	}

	/**
	 * @param crudProperty
	 */
	protected void addCrudProperty(CrudProperty crudProperty) {
		if (crudProperties == null) {
			crudProperties = new ArrayList<CrudProperty>();
		}

		crudProperties.add(crudProperty);
	}

	/**
	 * @return
	 */
	public JoEntity getJoEntity() {
		return joEntity;
	}

	/**
	 * @return
	 */
	public Iterator<CrudPropertyReference> getCrudPropertyReferencesIterator() {
		return crudPropertyReferences == null ? null : crudPropertyReferences.iterator();
	}

	/**
	 * @return
	 */
	public Iterator<CrudProperty> getCrudPropertiesIterator() {
		return crudProperties == null ? null : crudProperties.iterator();
	}

	/**
	 * 
	 */
	protected void initCrudEntity() {
		crudEntityNone = crudProperties == null && crudPropertyReferences == null && !ICrudBean.class.isAssignableFrom(joEntity.getEntityClass());
	}

	/**
	 * @return the crudEntityNone
	 */
	public boolean isCrudEntityNone() {
		return crudEntityNone;
	}
}
