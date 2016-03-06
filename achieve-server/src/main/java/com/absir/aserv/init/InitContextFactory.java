/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月18日 下午3:11:46
 */
package com.absir.aserv.init;

import javax.servlet.ServletContext;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;

/**
 * @author absir
 *
 */
@Base
@Bean
public class InitContextFactory extends InitBeanFactory {

	/**
	 * @param servletContext
	 */
	@Inject(type = InjectType.Selectable)
	protected void setServletContext(ServletContext servletContext) {
		// 全局链接参数
		if (appName == null) {
			appName = servletContext.getServletContextName();
		}

		if (appRoute == null) {
			appRoute = servletContext.getContextPath();
		}
	}
}
