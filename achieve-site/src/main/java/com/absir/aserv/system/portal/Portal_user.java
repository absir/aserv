/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月29日 下午2:59:39
 */
package com.absir.aserv.system.portal;

import com.absir.aserv.developer.Pag;
import com.absir.aserv.developer.Site;
import com.absir.aserv.menu.MenuContextUtils;
import com.absir.aserv.system.bean.JUser;
import com.absir.aserv.system.bean.form.FEmailCode;
import com.absir.aserv.system.bean.form.FMobileCode;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.IUser;
import com.absir.aserv.system.bean.value.JeRoleLevel;
import com.absir.aserv.system.security.SecurityContext;
import com.absir.aserv.system.server.ServerResolverRedirect;
import com.absir.aserv.system.service.PortalService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelString;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.route.invoker.InvokerResolverErrors;
import com.absir.server.value.*;

import java.text.MessageFormat;

@Server
public class portal_user extends PortalServer {

    /**
     * 用户登录
     */
    public String login(Input input) throws Exception {
        JiUserBase userBase = SecurityService.ME.getUserBase(input);
        if (userBase != null && userBase instanceof JUser) {
            String redirect = input.getParam("redirect");
            ServerResolverRedirect.redirect(KernelString.isEmpty(redirect) ? (MenuContextUtils.getSiteRoute() + "user/center") : redirect, false, input);
        }

        PortalService.ME.setOperationVerify(input.getAddress(), PortalService.LOGIN_TAG, input);
        return "user/login";
    }

    @Mapping(method = InMethod.POST)
    public String login(@Param String username, @Param String password, @Param long remember, Input input) throws Exception {
        PortalService.ME.doneOperationVerify(input.getAddress(), PortalService.LOGIN_TAG, input);
        InModel model = input.getModel();
        try {
            SecurityContext securityContext = SecurityService.ME.login(username, password, remember, JeRoleLevel.ROLE_USER.ordinal(), SECURITY_NAME, input);

        } catch (ServerException e) {
            JiUserBase userBase = (JiUserBase) e.getExceptionData();
            if (userBase == null) {
                InvokerResolverErrors.onError("username", Site.USER_NOT_EXIST, null, null);

            } else {
                if (userBase instanceof IUser) {
                    IUser user = (IUser) userBase;
                    if (user.getLastErrorTimes() >= 0) {
                        if (user.getLastErrorTimes() == 0) {
                            model.put("tip", MessageFormat.format(input.getLang(Site.LOGIN_LAST_ERROR_TIME), Site.getHumanTime((int) (user.getLastErrorLogin() - ContextUtils.getContextTime()) / 1000, 1, input)));

                        } else {
                            model.put("tip", MessageFormat.format(input.getLang(Site.LOGIN_LAST_ERROR_TIMES), user.getLastErrorTimes()));
                        }
                    }
                }

                InvokerResolverErrors.onError("password", Site.PASSWORD_ERROR, null, null);
            }
        }

        String redirect = input.getParam("redirect");
        if (!KernelString.isEmpty(redirect)) {
            ServerResolverRedirect.redirect(redirect, false, input);
        }

        model.put("message", input.getLangValue(Site.LOGIN_SUCCESS));
        model.put("url", MenuContextUtils.getSiteRoute() + "user/center");
        return "success";
    }

    /**
     * 注销登录
     */
    public void logout(Input input) throws Exception {
        SecurityService.ME.logout(SECURITY_NAME, input);
        ServerResolverRedirect.redirect(MenuContextUtils.getSiteRoute() + "user/login", false, input);
    }

    /**
     * 发送激活
     */
    public String registerCode(@Param int type, Input input) {
        PortalService.ME.sendRegisterCode(type, input);
        return "success";
    }

    /**
     * 用户注册
     */
    public String register(@Param int type, Input input) {
        type = PortalService.getRegisterType(type);
        InModel model = input.getModel();
        if (input.getMethod() == InMethod.POST) {
            PortalService.ME.register(type, SECURITY_NAME, input);
            model.put("url", MenuContextUtils.getSiteRoute() + "user/center");
            return "success";
        }

        model.put("type", type);
        if (type != 1) {
            PortalService.ME.setOperationVerify(input.getAddress(), type == 2 ? PortalService.EMAIL_TAG : PortalService.MESSAGE_TAG, input);
        }

        return "user/register";
    }

    /*
     * 用户中心
     */
    public void center(Input input) throws Exception {
        SecurityContext securityContext = SecurityService.ME.getSecurityContext(input);
        if (securityContext == null) {
            ServerResolverRedirect.redirect(MenuContextUtils.getSiteRoute() + "user/login", false, input);
        }
    }

    /**
     * 修改密码
     */
    public String password(Input input) throws Exception {
        JiUserBase userBase = SecurityService.ME.getUserBase(input);
        if (userBase == null || !(userBase instanceof JUser)) {
            ServerResolverRedirect.redirect(MenuContextUtils.getSiteRoute() + "user/login", false, input);
        }

        if (input.getMethod() == InMethod.POST) {
            PortalService.ME.password((JUser) userBase, input);
            return "success";
        }

        PortalService.ME.setOperationVerify(input.getAddress(), PortalService.PASSWORD_TAG, input);
        input.getModel().put("message", input.getLangMessage(Site.MODIFY_SUCCESS));
        return "user/password";
    }

    /*
     * 绑定用户名
     */
    @Errors
    public String username(@Validate PortalService.FUsername username, Input input) {
        JUser user = PortalService.ME.verifyUser(input);
        PortalService.ME.username(user, username.username, input);
        return "success";
    }

    /*
     * 发送操作验证码
     */
    @Mapping(method = InMethod.POST)
    public String verifyCode(int level, @Param String tag, @Param int type, Input input) throws Exception {
        JUser user = PortalService.ME.verifyUser(input);
        PortalService.ME.sendVerifyCode(user, level, tag, type, input);
        return "success";
    }

    /*
     * 验证用户可否操作
     */
    public String verify(int level, @Param String tag, Input input) throws Exception {
        JUser user = PortalService.ME.verifyUser(input);
        if (input.getMethod() == InMethod.POST) {
            PortalService.ME.verifyLevel(user, level, tag, KernelDyna.to(input.getParam("type"), int.class), input.getParam("value"), input);
            return "success";
        }

        level = PortalService.getVerifyLevel(level, user);
        InModel model = input.getModel();
        model.put("level", level);
        model.put("tag", tag);
        model.put("operation", PortalService.getOperation(tag, input));
        model.put("email", Pag.CONFIGURE.hasEmail() && !KernelString.isEmpty(user.getEmail()));
        model.put("message", Pag.CONFIGURE.hasMessage() && !KernelString.isEmpty(user.getMobile()));
        return "user/verify";
    }

    /**
     * 发送绑定验证码
     */
    public String bindCode(@Param int type, Input input) {
        if (type < 2 || type > 3) {
            throw new ServerException(ServerStatus.ON_DENIED);
        }

        PortalService.ME.sendOperationCode(type, type, type == 2 ? PortalService.BIND_EMAIL_TAG : PortalService.BIND_MOBILE_TAG, input);
        return "success";
    }

    /*
     * 绑定邮箱
     */
    @Errors
    public void email(@Validate FEmailCode emailCode, Input input) {
        JUser user = PortalService.ME.verifyUser(input);
        PortalService.ME.verifyEmailOrMobile(emailCode.email, PortalService.BIND_EMAIL_TAG, emailCode.code, 2, true);
        user.setEmail(emailCode.email);
        CrudServiceUtils.merge("JUser", null, user, true, null, null);
    }

    /*
     * 绑定手机号
     */
    @Errors
    public void mobile(@Validate FMobileCode mobileCode, Input input) {
        JUser user = PortalService.ME.verifyUser(input);
        PortalService.ME.verifyEmailOrMobile(mobileCode.mobile, PortalService.BIND_EMAIL_TAG, mobileCode.code, 2, true);
        user.setEmail(mobileCode.mobile);
        CrudServiceUtils.merge("JUser", null, user, true, null, null);
    }

}
