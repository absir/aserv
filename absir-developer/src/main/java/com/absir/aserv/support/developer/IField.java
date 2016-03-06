/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-8-8 下午6:48:03
 */
package com.absir.aserv.support.developer;

import java.util.List;
import java.util.Map;

import com.absir.aserv.system.bean.value.JeEditable;

/**
 * @author absir
 * 
 */
public interface IField {

	/**
	 * @return
	 */
	public JCrudField getCrudField();

	/**
	 * @return
	 */
	public String getName();

	/**
	 * @return
	 */
	public Class<?> getType();

	/**
	 * @return
	 */
	public IField getValueField();

	/**
	 * @return
	 */
	public String getEntityName();

	/**
	 * @return
	 */
	public String getValueEntityName();

	/**
	 * @return
	 */
	public String getCaption();

	/**
	 * @return
	 */
	public String[] getGroups();

	/**
	 * @return
	 */
	public boolean isGenerated();

	/**
	 * @return
	 */
	public boolean isCanOrder();

	/**
	 * @return
	 */
	public boolean isNullable();

	/**
	 * @return
	 */
	public boolean isCollection();

	/**
	 * @return
	 */
	public String getMappedBy();

	/**
	 * @return
	 */
	public List<String> getTypes();

	/**
	 * @return
	 */
	public JeEditable getEditable();

	/**
	 * @return
	 */
	public Map<String, Object> getMetas();

	/**
	 * @return
	 */
	public Object getDefaultEntity();
}
