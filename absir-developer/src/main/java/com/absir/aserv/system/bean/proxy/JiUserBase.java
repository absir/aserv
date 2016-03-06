/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-4 下午2:06:23
 */
package com.absir.aserv.system.bean.proxy;

import java.util.Collection;

import com.absir.aserv.system.bean.base.JbUserRole;

/**
 * @author absir
 * 
 */
public interface JiUserBase {

	/**
	 * @return
	 */
	public Long getUserId();

	/**
	 * @return
	 */
	public boolean isDeveloper();

	/**
	 * @return
	 */
	public boolean isActivation();

	/**
	 * @return
	 */
	public boolean isDisabled();

	/**
	 * @return
	 */
	public String getUsername();

	/**
	 * @return
	 */
	public int getUserRoleLevel();

	/**
	 * @return
	 */
	public Collection<? extends JiUserRole> userRoles();

	/**
	 * @return
	 */
	public Collection<? extends JbUserRole> getUserRoles();

	/**
	 * @param key
	 * @return
	 */
	public Object getMetaMap(String key);

	/**
	 * @param key
	 * @param value
	 */
	public void setMetaMap(String key, String value);
}
