/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.aserv.system.bean.proxy;

import com.absir.aserv.system.bean.base.JbStragety;

import javax.persistence.Entity;

/**
 * @author absir
 *
 */
public interface JpStrategy {

    /**
     * @author absir
     */
    @Entity
    class Strategy extends JbStragety {

    }
}
