/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月19日 上午10:32:29
 */
package com.absir.aserv.system.bean;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JiOpenValue;

/**
 * @author absir
 *
 */
@MaEntity(parent = { @MaMenu("系统配置"), @MaMenu("常用配置") }, name = "字典")
@Entity
public class JDict extends JbBase implements JiOpenValue<String> {

	@JaLang(value = "键", tag = "key")
	@Id
	private String id;

	@JaLang("开启")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private boolean open;

	@JaLang("值")
	@JaEdit(groups = { JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST })
	private String value;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the open
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * @param open
	 *            the open to set
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
