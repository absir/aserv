/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-8 下午4:49:27
 */
package com.absir.servlet;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absir.bean.core.BeanDefineDiscover;
import com.absir.bean.core.BeanFactoryProvider;
import com.absir.context.config.BeanFactoryStopping;
import com.absir.context.config.BeanProviderContext;

/**
 * @author absir
 * 
 */
public class InDispathContext extends InDispathFilter {

	/** logger */
	protected Logger logger;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.servlet.InDispathFilter#init(javax.servlet.FilterConfig)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Servlet#destroy()
	 */
	@Override
	public void destroy() {
		BeanFactoryStopping.stoppingAll();
		super.destroy();
	}
}
