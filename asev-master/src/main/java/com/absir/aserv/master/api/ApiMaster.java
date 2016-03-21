/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月13日 下午4:10:30
 */
package com.absir.aserv.master.api;

import com.absir.aserv.system.api.ApiServer;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.security.SecurityContext;
import com.absir.master.InputMaster;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.Input;

public class ApiMaster extends ApiServer {

    @Override
    protected SecurityContext onAuthentication(Input input) throws Throwable {
        SecurityContext context = super.onAuthentication(input);
        JiUserBase userBase = context == null ? null : context.getUser();
        if (userBase == null || !userBase.isDeveloper()) {
            if (!InputMaster.onAuthentication(input)) {
                throw new ServerException(ServerStatus.ON_DENIED);
            }
        }

        return context;
    }

}
