/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-5 下午2:36:52
 */
package com.absir.aserv.menu;

import com.absir.aserv.menu.value.MaFactory;
import com.absir.server.route.RouteMatcher;

/**
 * @author absir
 * 
 */
public interface IMenuFactory {

	/**
	 * @param route
	 * @param menuBeanRoot
	 * @param routeMatcher
	 * @param maFactory
	 */
	public void proccess(String route, MenuBeanRoot menuBeanRoot, RouteMatcher routeMatcher, MaFactory maFactory);

}
