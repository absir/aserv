/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-8-30 上午10:21:56
 */
package com.absir.aserv.system.bean.base;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.absir.aserv.system.bean.proxy.JiUserRole;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.validator.value.Length;
import com.absir.validator.value.NotEmpty;

/**
 * @author absir
 * 
 */
@MappedSuperclass
public class JbUserRole extends JbBean implements JiUserRole {

	@JaLang("角色名称")
	@JaEdit(groups = { JaEdit.GROUP_SUGGEST })
	@Column(length = 32)
	@NotEmpty
	@Length(min = 2, max = 12)
	private String rolename;

	/**
	 * @return the rolename
	 */
	public String getRolename() {
		return rolename;
	}

	/**
	 * @param rolename
	 *            the rolename to set
	 */
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
}