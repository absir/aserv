/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-10 下午11:58:02
 */
package com.absir.aserv.menu.value;

import com.absir.aserv.menu.MenuContextUtils;
import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 * 
 */
public enum MeUrlType {

	@JaLang("后台链接")
	ADMIN {
		@Override
		public String getRoute() {
			return MenuContextUtils.getAdminRoute();
		}
	},

	@JaLang("前台链接")
	SITE {
		@Override
		public String getRoute() {
			return MenuContextUtils.getSiteRoute();
		}
	},

	@JaLang("外部链接")
	NONE {
		@Override
		public String getRoute() {
			return null;
		}
	},
	;

	public abstract String getRoute();
}
