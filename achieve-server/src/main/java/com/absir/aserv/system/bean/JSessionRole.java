/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-12 下午7:45:42
 */
package com.absir.aserv.system.bean;

import java.io.Serializable;

import com.absir.aserv.system.bean.base.JbUserRole;

/**
 * @author absir
 * 
 */
@SuppressWarnings("serial")
public class JSessionRole extends JbUserRole implements Serializable {

	/**
	 * 
	 */
	public JSessionRole() {
	}

	/**
	 * @param id
	 * @param rolename
	 */
	public JSessionRole(Long id, String rolename) {
		setId(id);
		setRolename(rolename);
	}
}
