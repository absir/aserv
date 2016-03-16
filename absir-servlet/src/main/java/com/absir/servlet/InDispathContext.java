/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-8 下午4:49:27
 */
package com.absir.servlet;

import com.absir.bean.core.BeanDefineDiscover;
import com.absir.bean.core.BeanFactoryProvider;
import com.absir.context.config.BeanFactoryStopping;
import com.absir.context.config.BeanProviderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

public class InDispathContext extends InDispathFilter {

    protected Logger logger;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        BeanDefineDiscover.open();
        BeanProviderContext beanProviderContext = new BeanProviderContext(
                BeanFactoryProvider.getParameterList(filterConfig.getInitParameter("include")),
                BeanFactoryProvider.getParameterList(filterConfig.getInitParameter("exclude")),
                BeanFactoryProvider.getParameterList(filterConfig.getInitParameter("filter")));
        beanProviderContext.scan(null, null, filterConfig.getServletContext());
        logger = LoggerFactory.getLogger(InDispathContext.class);
        logger.info("start beanFactory from " + this);
        beanProviderContext.started();
    }

    @Override
    public void destroy() {
        BeanFactoryStopping.stoppingAll();
        super.destroy();
    }
}
