/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-23 下午12:27:30
 */
package com.absir.aserv.system.service.impl;

import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JeRoleLevel;
import com.absir.aserv.system.helper.HelperString;
import com.absir.aserv.system.service.IdentityService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Value;
import com.absir.core.kernel.KernelString;

import java.util.Map;

@Base
@Bean
public class IdentityServiceLocal implements IdentityService {

    @Inject(value = "IdentityService")
    private static Map<String, IdentityService> identityServiceMap;

    @Value("security.identity.error")
    private int error = 99;

    @Value("security.identity.errorTime")
    private int errorTime = 60000;

    public static JiUserBase getUserBase(String identity, String address) {
        if (!KernelString.isEmpty(identity)) {
            String[] parameters = HelperString.split(identity, ',');
            if (parameters.length > 0) {
                IdentityService identityService = identityServiceMap.get(parameters[0]);
                if (identityService != null) {
                    JiUserBase user = identityService.getUserBase(parameters, address);
                    if (user == null || user.isDisabled()) {
                        return null;
                    }

                    return user;
                }
            }
        }

        return null;
    }

    @Override
    public JiUserBase getUserBase(String[] parameters, String address) {
        if (parameters.length == 3) {
            JiUserBase userBase = SecurityService.ME.getUserBase(parameters[1], JeRoleLevel.ROLE_USER.ordinal());
            if (userBase != null) {
                if (SecurityService.ME.validator(userBase, parameters[2], error, errorTime, address)) {
                    return userBase;
                }
            }
        }

        return null;
    }
}
