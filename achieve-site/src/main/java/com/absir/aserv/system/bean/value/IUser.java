/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年1月4日 上午9:47:43
 */
package com.absir.aserv.system.bean.value;

/**
 * @author absir
 *
 */
public interface IUser {

	/**
	 * @return the password
	 */
	public String getPassword();

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password);

	/**
	 * @return the salt
	 */
	public String getSalt();

	/**
	 * @param salt
	 *            the salt to set
	 */
	public void setSalt(String salt);

	/**
	 * @return the lastLogin
	 */
	public long getLastLogin();

	/**
	 * @param lastLogin
	 *            the lastLogin to set
	 */
	public void setLastLogin(long lastLogin);

	/**
	 * @return the errorLogin
	 */
	public int getErrorLogin();

	/**
	 * @param errorLogin
	 *            the errorLogin to set
	 */
	public void setErrorLogin(int errorLogin);

	/**
	 * @return the lastErrorLogin
	 */
	public long getLastErrorLogin();

	/**
	 * @param lastErrorLogin
	 *            the lastErrorLogin to set
	 */
	public void setLastErrorLogin(long lastErrorLogin);

}
