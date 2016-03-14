/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-11-14 上午10:03:48
 */
package com.absir.open.service;

import com.absir.open.bean.JPayTrade;

/**
 * @author absir
 */
public interface IPayInterface {

    /**
     * @param payTrade
     * @return
     * @throws Exception
     */
    public boolean validator(JPayTrade payTrade) throws Exception;

}
