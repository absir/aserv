/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-24 下午3:08:30
 */
package com.absir.aserv.system.api;

import com.absir.aserv.system.api.ApiServer.Route;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.service.IdentityService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.impl.IdentityServiceLocal;
import com.absir.bean.inject.value.Bean;
import com.absir.servlet.InputRequest;

import javax.servlet.http.HttpServletRequest;

@Bean
public class ApiRoute extends Route implements IdentityService {

    protected JiUserBase getInputUserBase(InputRequest inputRequest) {
        HttpServletRequest request = inputRequest.getRequest();
        String identity = request.getHeader("identity");
        if (identity == null) {
            identity = request.getParameter("identity");
            return identity == null ? null : SecurityService.ME.openUserBase(identity, null, "Local", inputRequest.getAddress());
        }

        return IdentityServiceLocal.getUserBase(identity, inputRequest.getAddress());
    }

    @Override
    public JiUserBase getUserBase(String[] parameters, String address) {
        if (parameters.length == 2) {
            return SecurityService.ME.openUserBase(parameters[1], null, "Local", address);
        }

        return null;
    }
}
