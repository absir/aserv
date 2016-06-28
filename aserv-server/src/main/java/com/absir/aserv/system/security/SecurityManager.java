/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-12 下午1:23:59
 */
package com.absir.aserv.system.security;

import com.absir.bean.basis.Base;
import com.absir.bean.basis.BeanConfig;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.core.kernel.KernelString;

@Base
@Bean
public class SecurityManager {

    private int error;

    private long errorTime;

    private String sessionKey;

    private long sessionLife;

    private long sessionExpiration;

    private String cookiePath;

    @Inject
    private void initialize() {
        BeanConfig beanConfig = BeanFactoryUtils.getBeanConfig();
        if (error <= 0) {
            error = beanConfig.getExpressionValue("security.session.error", null, int.class);
            if (error == 0) {
                error = 5;
            }
        }

        if (errorTime <= 0) {
            errorTime = beanConfig.getExpressionValue("security.session.errorTime", null, long.class);
            if (errorTime == 0) {
                errorTime = 180000;
            }
        }

        if (sessionKey == null) {
            sessionKey = beanConfig.getExpressionObject("security.session.key", null, String.class);
            if (KernelString.isEmpty(sessionKey)) {
                sessionKey = "ACHIEVE-SECURITY";
            }
        }

        if (sessionLife <= 0) {
            sessionLife = beanConfig.getExpressionValue("security.session.life", null, long.class);
            if (sessionLife <= 60000) {
                sessionLife = 180000;
            }
        }

        if (sessionExpiration <= 0) {
            sessionExpiration = beanConfig.getExpressionValue("security.session.expiration", null, long.class);
            if (sessionExpiration >= 0 && sessionExpiration < sessionLife) {
                sessionExpiration = 3600000;
            }
        }

        if (cookiePath == null) {
            cookiePath = beanConfig.getExpressionObject("security.cookie.Path", null, String.class);
            if (KernelString.isEmpty(cookiePath)) {
                cookiePath = "/";
            }
        }
    }

    public int getError() {
        return error;
    }

    public long getErrorTime() {
        return errorTime;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public long getSessionLife() {
        return sessionLife;
    }

    public long getSessionExpiration() {
        return sessionExpiration;
    }

    public String getCookiePath() {
        return cookiePath;
    }
}
