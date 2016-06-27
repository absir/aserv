/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-23 下午12:26:18
 */
package com.absir.aserv.system.service;

import com.absir.aserv.system.bean.proxy.JiUserBase;

public interface IdentityService {

    public JiUserBase getUserBase(String[] parameters, String address);

}
