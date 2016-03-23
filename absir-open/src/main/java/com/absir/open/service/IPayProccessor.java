/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-23 下午3:21:33
 */
package com.absir.open.service;

import com.absir.open.bean.JPayTrade;

public interface IPayProccessor {

    public Object process(JPayTrade payTrade) throws Exception;

}
