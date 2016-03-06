/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-5-22 上午10:17:30
 */
package com.absir.aserv.system.dao;

import com.absir.aserv.data.value.DataQuery;
import com.absir.aserv.system.bean.JUser;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;

/**
 * @author absir
 * 
 */
@Bean
public interface JUserDao {

	/** ME */
	public final static JUserDao ME = BeanFactoryUtils.get(JUserDao.class);

	/**
	 * @param username
	 * @return
	 */
	@DataQuery("SELECT o FROM JUser o WHERE o.username = ?")
	public JUser findByUsername(String username);
	
}
