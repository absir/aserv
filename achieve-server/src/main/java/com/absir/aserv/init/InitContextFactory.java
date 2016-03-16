/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月18日 下午3:11:46
 */
package com.absir.aserv.init;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.core.kernel.KernelString;

import javax.servlet.ServletContext;

@Base
@Bean
public class InitContextFactory extends InitBeanFactory {

    @Inject(type = InjectType.Selectable)
    protected void setServletContext(ServletContext servletContext) {
        // 全局链接参数
        if (appName == null) {
            appName = servletContext.getServletContextName();
        }

        if (appRoute == null) {
            appRoute = servletContext.getContextPath();
        }

        if (KernelString.isEmpty(appRoute)) {
            appRoute = "/";

        } else if (appRoute.charAt(0) != '/') {
            appRoute = '/' + appRoute + '/';
        }
    }
}
