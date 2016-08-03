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
import com.absir.core.base.Environment;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.Input;
import com.absir.server.in.Interceptor;
import com.absir.server.on.OnPut;
import com.absir.server.value.*;
import com.absir.servlet.InputRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

@Mapping("/api")
@Interceptors(ApiServer.Route.class)
public abstract class ApiServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServer.class);

    /**
     * 统一返回类型 权限判断
     */
    @Body
    @NoBody
    @Before
    protected SecurityContext onAuthentication(Input input) throws Throwable {
        return SecurityService.ME == null ? null : SecurityService.ME.getSecurityContext(input);
    }

    /**
     * 统一异常返回
     */
    @Body
    @OnException(Throwable.class)
    protected Object onException(Throwable e, Input input) {
        input.setStatus(ServerStatus.ON_ERROR.getCode());
        if (BeanFactoryUtils.getEnvironment() == Environment.DEVELOP) {
            e.printStackTrace();
        }

        if (BeanFactoryUtils.getEnvironment().compareTo(Environment.DEBUG) <= 0 || input.isDebug()
                || !(e instanceof ServerException)) {
            LOGGER.debug("on server " + input.getUri(), e);
        }

        if (e instanceof ServerException) {
            ServerException exception = (ServerException) e;
            Object data = exception.getExceptionData();
            if (exception.getServerStatus() == ServerStatus.ON_CODE) {
                return data == null ? "fail" : data;
            }

            if (data != null && data instanceof MessageCode) {
                return data;
            }

            MessageCode messageCode = new MessageCode();
            messageCode.setServerException(exception);
            return messageCode;
        }

        return new MessageCode(e);
    }

    /**
     * 消息对象
     */
    public static class MessageCode {

        public String message;

        public int code;

        public MessageCode() {
        }

        public MessageCode(Throwable e) {
            if (e instanceof ServerException) {
                setServerException((ServerException) e);

            } else {
                setThrowable(e);
            }
        }

        public void setThrowable(Throwable e) {
            message = e.toString();
            code = ServerStatus.ON_ERROR.getCode();
        }

        public void setServerException(ServerException e) {
            message = e.toString();
            code = e.getServerStatus().getCode();
        }
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
