/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-6 下午6:11:57
 */
package com.absir.aserv.system.service.impl;

import com.absir.bean.basis.Basis;
import com.absir.bean.inject.value.Bean;
import com.absir.orm.hibernate.SessionFactoryUtils;
import org.hibernate.SessionFactory;

/**
 * @author absir
 *
 */
@Basis
@Bean
public class BeanServiceImpl extends BeanServiceBase {

    /**
     * @return
     */
    public BeanServiceImpl() {
        super(SessionFactoryUtils.get().getSessionFactory());
    }

    /**
     * @param sessionFactory
     */
    public BeanServiceImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
