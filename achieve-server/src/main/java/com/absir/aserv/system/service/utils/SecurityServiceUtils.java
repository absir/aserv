/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-6 下午5:34:05
 */
package com.absir.aserv.system.service.utils;

import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.service.SecurityService;
import com.absir.server.on.OnPut;

public abstract class SecurityServiceUtils {

    public static JiUserBase getUserBase() {
        OnPut onPut = OnPut.get();
        return onPut == null ? null : SecurityService.ME.getUserBase(onPut.getInput());
    }

    public static long getUserId() {
        JiUserBase user = getUserBase();
        return user == null ? 0 : user.getUserId();
    }
}
