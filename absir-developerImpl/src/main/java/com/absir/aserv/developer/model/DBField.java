/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.developer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.absir.aserv.support.developer.IField;
import com.absir.aserv.support.developer.JCrudField;
import com.absir.aserv.system.bean.value.JeEditable;

/**
 * @author absir
 * 
 */
public class DBField implements IField {

	/** crudField */
	protected JCrudField crudField;

	/** valueField */
	protected IField valueField;

	/** entityName */
	protected String entityName;

	/** valueEntityName */
	protected String valueEntityName;

	/** caption */
	protected String caption;

	/** groups */
	protected String[] groups;

	/** generated */
	protected boolean generated;

	/** canOrder */
	protected boolean canOrder = true;

	/** nullable */
	protected boolean nullable = true;

	/** collection */
	protected boolean collection;

	/** mappedBy */
	protected String mappedBy;

	/** types */
	protected List<String> types = new ArrayList<String>();

	/** editable */
	protected JeEditable editable = JeEditable.ENABLE;

	/** metas */
	protected Map<String, Object> metas = new HashMap<String, Object>();

	/** defaultEntity */
	protected Object defaultEntity;

	/**
	 * 
	 */
	public DBField() {
		crudField = new JCrudField();
	}

	/**
	 * @param column
	 */
	public DBField(DBColumn column) {
		this();
	}

	/**
	 * @return the crudField
	 */
	public JCrudField getCrudField() {
		return crudField;
	}

	/**
	 * @return
	 */
	public String getName() {
		return crudField.getName();
	}

	/**
	 * @return
	 */
	public Class<?> getType() {
		return crudField.getType();
	}

	/**
	 * @return
	 */
	public IField getValueField() {
		return valueField;
	}

	/**
	 * @return
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @return
	 */
	public String getValueEntityName() {
		return valueEntityName;
	}

	/**
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * @return the groups
	 */
	public String[] getGroups() {
		return groups;
	}

	/**
	 * @return the generated
	 */
	public boolean isGenerated() {
		return generated;
	}

	/**
	 * @return the canOrder
	 */
	public boolean isCanOrder() {
		return canOrder;
	}

	/**
	 * @return the nullable
	 */
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * @return the collection
	 */
	public boolean isCollection() {
		return collection;
	}

	/**
	 * @return the mappedBy
	 */
	public String getMappedBy() {
		return mappedBy;
	}

	/**
	 * @return the types
	 */
	public List<String> getTypes() {
		return types;
	}

	/**
	 * @return the editable
	 */
	public JeEditable getEditable() {
		return editable;
	}

	/**
	 * @return the metas
	 */
	public Map<String, Object> getMetas() {
		return metas;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.support.developer.IField#getDefaultEntity()
	 */
	@Override
	public final Object getDefaultEntity() {
		if (defaultEntity == null) {
			defaultEntity = instanceDefaultEntity();
		}

		return defaultEntity;
	}

	/**
	 * @return
	 */
	protected Object instanceDefaultEntity() {
		return null;
	}
}
