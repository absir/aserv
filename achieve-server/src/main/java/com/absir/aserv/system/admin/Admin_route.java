/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-6 下午7:36:47
 */
package com.absir.aserv.system.admin;

import com.absir.aserv.menu.MenuContextUtils;
import com.absir.aserv.system.server.value.Redirect;
import com.absir.bean.basis.Base;
import com.absir.server.in.InModel;
import com.absir.server.on.OnPut;
import com.absir.server.value.Server;

/**
 * @author absir
 * 
 */
@Base
@Server
public class Admin_route extends AdminServer {

	/**
	 * 默认跳转首页
	 * 
	 * @param uiModel
	 * @return
	 */
	@Redirect
	public String route(OnPut onPut) {
		return MenuContextUtils.getAdminRoute() + "/main";
	}

	/**
	 * 管理首页
	 * 
	 * @param uiModel
	 */
	public void main(InModel model) {
	}
}
