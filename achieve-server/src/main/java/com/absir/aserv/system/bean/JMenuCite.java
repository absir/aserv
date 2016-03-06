/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-29 下午3:52:17
 */
package com.absir.aserv.system.bean;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 * 
 */
@MaEntity(parent = { @MaMenu("菜单管理") }, name = "引用")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JMenuCite extends JbBase {

	@JaLang(value = "标识", tag = "identifie")
	@Id
	private String id;

	@JaLang("菜单")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private JMenu menu;

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
	 * @return the menu
	 */
	public JMenu getMenu() {
		return menu;
	}

	/**
	 * @param menu
	 *            the menu to set
	 */
	public void setMenu(JMenu menu) {
		this.menu = menu;
	}
}
