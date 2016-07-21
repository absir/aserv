/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-22 上午10:17:30
 */
package com.absir.aserv.system.dao;

import com.absir.aserv.data.value.DataQuery;
import com.absir.aserv.system.bean.JUser;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;

@Bean
public interface JUserDao {

    public final static JUserDao ME = BeanFactoryUtils.get(JUserDao.class);

    @DataQuery("SELECT o FROM JUser o WHERE o.username = ?")
    public JUser findByUsername(String username);

    @DataQuery("SELECT o FROM JUser o WHERE o.username = :p0 OR o.email = :p0 OR o.mobile = :p0")
    public JUser findByRefUsername(String loginUsername);

}
