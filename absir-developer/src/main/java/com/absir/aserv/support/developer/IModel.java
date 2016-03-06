/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-8-8 下午6:45:54
 */
package com.absir.aserv.support.developer;

import java.util.List;

import com.absir.orm.value.JoEntity;

/**
 * @author absir
 * 
 */
public interface IModel {

	/**
	 * @return
	 */
	public JoEntity getJoEntity();

	/**
	 * @return
	 */
	public String getCaption();

	/**
	 * @return
	 */
	public Long lastModified();

	/**
	 * @return
	 */
	public boolean isFilter();
	
	/**
	 * @return
	 */
	public DModel getModel();

	/**
	 * @return
	 */
	public List<JCrud> getjCruds();

	/**
	 * @return
	 */
	public IField getPrimary();

	/**
	 * @return
	 */
	public List<IField> getFields();

	/**
	 * @param group
	 * @return
	 */
	public List<IField> getGroupFields(String group);

}
