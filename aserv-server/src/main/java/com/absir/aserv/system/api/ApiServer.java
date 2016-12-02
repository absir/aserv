/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-3 下午5:58:37
 */
package com.absir.aserv.system.api;

import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.security.SecurityContext;
import com.absir.aserv.system.security.SecurityManager;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.impl.IdentityServiceLocal;
import com.absir.aserv.transaction.TransactionIntercepter;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.server.in.Input;
import com.absir.server.in.Interceptor;
import com.absir.server.on.OnPut;
import com.absir.server.value.*;
import com.absir.servlet.InputRequest;

import java.util.Iterator;

@Mapping("/api")
@Interceptors(ApiServer.Route.class)
public abstract class ApiServer extends ApiBase {

    /**
     * 统一返回类型 权限判断
     */
    @Body
    @NoBody
    @Before
    protected SecurityContext onAuthentication(Input input) throws Throwable {
        return SecurityService.ME == null ? null : SecurityService.ME.getSecurityContext(input);
    }

    @Base
    @Bean
    public static class Route implements Interceptor {

        public static final Route ME = BeanFactoryUtils.get(Route.class);

        @Override
        public OnPut intercept(Iterator<Interceptor> iterator, Input input) throws Throwable {
            autoLogin(input);
            return input.intercept(iterator);
        }

        public void autoLogin(Input input) {
            if (SecurityService.ME != null) {
                SecurityContext securityContext = SecurityService.ME.autoLogin("api", true, -1, input);
                if (securityContext == null && input instanceof InputRequest) {
                    InputRequest inputRequest = (InputRequest) input;
                    JiUserBase userBase = getInputUserBase(inputRequest);
                    if (userBase != null) {
                        SecurityManager securityManager = SecurityService.ME.getSecurityManager("api");
                        long remember = securityManager.getSessionExpiration();
                        if (remember < securityManager.getSessionLife()) {
                            remember = securityManager.getSessionLife();
                        }

                        SecurityService.ME.loginUser(securityManager, userBase, remember, input);
                    }
                }
            }
        }

        protected JiUserBase getInputUserBase(InputRequest inputRequest) {
            return IdentityServiceLocal.getUserBase(inputRequest.getRequest().getHeader("identity"), inputRequest.getAddress());
        }
    }

    public static class TransactionRoute extends TransactionIntercepter {

    }
}
