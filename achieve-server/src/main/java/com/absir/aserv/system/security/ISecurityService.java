/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-5-31 下午4:59:14
 */
package com.absir.aserv.system.security;

import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.server.in.Input;

/**
 * @author absir
 * 
 */
public interface ISecurityService {

	/**
	 * @param userId
	 * @return
	 */
	public JiUserBase getUserBase(Long userId);

	/**
	 * @param username
	 * @param password
	 * @return
	 */
	public JiUserBase getUserBase(String username);

	/**
	 * @param userBase
	 * @param password
	 * @param error
	 * @param errorTime
	 * @return
	 */
	public boolean validator(JiUserBase userBase, String password, int error, long errorTime);

	/**
	 * @param username
	 * @param password
	 * @param platform
	 * @return
	 */
	public JiUserBase openUserBase(String username, String password, String platform);

	/**
	 * @param name
	 * @param remeber
	 * @param roleLevel
	 * @param input
	 * @return
	 */
	public SecurityContext autoLogin(String name, boolean remeber, int roleLevel, Input input);

	/**
	 * @param userBase
	 * @param remember
	 * @param roleLevel
	 * @param input
	 * @return
	 */
	public SecurityContext login(String username, String password, long remember, int roleLevel, String name, Input input);

	/**
	 * @param name
	 * @param input
	 */
	public void logout(String name, Input input);
}
