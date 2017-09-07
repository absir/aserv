/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-6 下午1:17:33
 */
package com.absir.aserv.system.admin;

import com.absir.aserv.system.asset.Asset_verify;
import com.absir.aserv.system.bean.value.JeRoleLevel;
import com.absir.aserv.system.helper.HelperInput;
import com.absir.aserv.system.security.SecurityContext;
import com.absir.aserv.system.server.ServerResolverRedirect;
import com.absir.aserv.system.service.SecurityService;
import com.absir.bean.basis.Base;
import com.absir.core.kernel.KernelString;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InMethod;
import com.absir.server.in.Input;
import com.absir.server.value.Mapping;
import com.absir.server.value.Nullable;
import com.absir.server.value.Param;
import com.absir.server.value.Server;

import static com.absir.aserv.menu.MenuContextUtils.getAdminRoute;

@Base
@Server
public class Admin_login extends AdminServer {

    @Override
    protected SecurityContext onAuthentication(Input input) throws Exception {
        return null;
    }

    /**
     * 登录界面
     */
    public String route(Input input) {
        input.getModel().put("remember", remember);
        if (HelperInput.isAjax(input)) {
            return "admin/login.ajax";
        }

        return "admin/login";
    }

    /**
     * AJAX登录
     */
    public String ajax(Input input) {
        input.getModel().put("remember", remember);
        return "admin/login.ajax";
    }

    /**
     * 注销登录
     */
    public void out(Input input) throws Exception {
        SecurityService.ME.logout("admin", input);
        ServerResolverRedirect.redirect(getAdminRoute() + "login", false, input);
    }

    /**
     * 登录处理
     */
    @Mapping(method = InMethod.POST)
    public String route(@Param String username, @Param String password, @Param @Nullable long remember, Input input) throws Exception {
        try {
            SecurityService.ME.logout("admin", input);
            //!HelperInput.isAjax(input) &&
            if (!Asset_verify.verifyInput(input)) {
                throw new ServerException(ServerStatus.NO_VERIFY);
            }

            password = SecurityService.ME.getIEncryptValue(input, password);
            SecurityService.ME.login(username, password, remember, JeRoleLevel.ROLE_ADMIN.ordinal(), "admin", input);

        } catch (ServerException e) {
            input.getModel().put("error", e);
            if (HelperInput.isAjax(input)) {
                return "admin/login.failed";
            }

            input.getModel().put("remember", remember);
            return "admin/login";
        }

        if (!HelperInput.isAjax(input)) {
            String redirect = input.getParam("redirect");
            ServerResolverRedirect.redirect(KernelString.isEmpty(redirect) ? (getAdminRoute() + "main") : redirect, false, input);
        }

        return "admin/login.success";
    }
}
