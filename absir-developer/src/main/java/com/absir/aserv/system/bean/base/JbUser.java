/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-26 下午3:49:42
 */
package com.absir.aserv.system.bean.base;

import javax.persistence.MappedSuperclass;

import com.absir.aserv.system.assoc.DeveloperAssoc;
import com.absir.aserv.system.bean.proxy.JiDeveloper;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.orm.value.JaField;

/**
 * @author absir
 * 
 */
@MappedSuperclass
public class JbUser extends JbBean implements JiDeveloper {

	@JaLang("开发者")
	@JaEdit(editable = JeEditable.DISABLE)
	@JaField(assocClasses = JbPermission.class, referencEntityClass = DeveloperAssoc.class)
	private boolean developer;

	@JaLang("激活")
	@JaEdit(groups = { JaEdit.GROUP_SUGGEST })
	private boolean activation;

	@JaLang("禁用")
	@JaEdit(groups = { JaEdit.GROUP_SUGGEST })
	private boolean disabled;

	/**
	 * @return the developer
	 */
	public boolean isDeveloper() {
		return developer;
	}

	/**
	 * @param developer
	 *            the developer to set
	 */
	public void setDeveloper(boolean developer) {
		this.developer = developer;
	}

	/**
	 * @return the activation
	 */
	public boolean isActivation() {
		return activation;
	}

	/**
	 * @param activation
	 *            the activation to set
	 */
	public void setActivation(boolean activation) {
		this.activation = activation;
	}

	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @param disabled
	 *            the disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
