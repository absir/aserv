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
import com.absir.aserv.system.asset.Asset_verify;
import com.absir.aserv.system.bean.JUser;
import com.absir.aserv.system.bean.form.FEmailCode;
import com.absir.aserv.system.bean.form.FMobileCode;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeRoleLevel;
import com.absir.aserv.system.bean.value.JeUserType;
import com.absir.aserv.system.security.SecurityContext;
import com.absir.aserv.system.service.PortalService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.binder.BinderData;
import com.absir.context.core.ContextUtils;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.route.invoker.InvokerResolverErrors;
import com.absir.server.value.Mapping;
import com.absir.server.value.Param;
import com.absir.server.value.Server;
import com.absir.validator.value.Confirm;
import com.absir.validator.value.Length;
import com.absir.validator.value.NotEmpty;
import com.absir.validator.value.Regex;
import org.hibernate.exception.ConstraintViolationException;

@Server
public class portal_user extends PortalServer {

    protected static final String TAG = "LA";

    /**
     * 用户登录
     */
    public String login(Input input) {
        return "portal/user/login";
    }

    @Mapping(method = InMethod.POST)
    public String login(@Param String username, @Param String password, @Param long remember, Input input) {
        SecurityContext securityContext = SecurityService.ME.login(username, password, remember, JeRoleLevel.ROLE_USER.ordinal(), "api", input);
        return "";
    }

    public static class FUsername {

        @JaLang("用户名")
        @NotEmpty
        @Regex(value = "^[^\\d@][^@]{4,16}$", lang = "请输入首位不是数字,不含有@的4-16位字符")
        public String username;
    }

    public static class FRegister {

        @JaLang("密码")
        @JaEdit(types = "passwordType")
        @NotEmpty
        @Length(min = 6, max = 16)
        public String password;

        @JaLang("确认密码")
        @JaEdit(types = "passwordType")
        @NotEmpty
        @Confirm("newPassword")
        public String confirmPassword;

    }

    protected int getRegisterType(int type) {
        if (!Pag.CONFIGURE.hasAllowUserRegister()) {
            throw new ServerException(ServerStatus.ON_DENIED);
        }

        //默认注册类型
        if (type == 0) {
            type = Pag.CONFIGURE.getDefaultRegisterType();
        }

        if (type == 1) {
            if (!Pag.CONFIGURE.isAllowUsernameRegister()) {
                type = 2;
            }

        } else if (type == 3) {
            if (!Pag.CONFIGURE.isAllowMessageRegister()) {
                type = 2;
            }

        } else {
            type = 2;
        }

        if (type == 2 && !Pag.CONFIGURE.isAllowEmailRegister()) {
            if (Pag.CONFIGURE.isAllowMessageRegister()) {
                type = 3;

            } else if (Pag.CONFIGURE.isAllowUsernameRegister()) {
                type = 1;

            } else {
                throw new ServerException(ServerStatus.ON_DENIED);
            }
        }

        return type;
    }

    /**
     * 发送激活
     */
    public String registerCode(@Param int type, Input input) {
        type = getRegisterType(type);
        if (!(type == 2 || type == 3)) {
            throw new ServerException(ServerStatus.ON_DENIED);
        }

        long idleTime;
        long sendTime;
        BinderData binderData = input.getBinderData();
        binderData.getBinderResult().setValidation(true);
        binderData.getBinderResult().getPropertyFilter().exclude("code");
        if (type == 2) {
            FEmailCode emailCode = binderData.bind(input.getParamMap(), null, FEmailCode.class);
            InvokerResolverErrors.checkError(binderData.getBinderResult(), null);
            if (PortalService.ME.findUser(emailCode.email, type) != null) {
                InvokerResolverErrors.onError("email", Site.EMAIL_REGISTERED, null, null);
            }

            idleTime = Pag.CONFIGURE.getEmailIdleTime();
            sendTime = PortalService.ME.sendEmailCode(emailCode.email, PortalService.REGISTER_TAG, Site.TPL.getCodeEmailSubject(), Site.TPL.getCodeEmail(), idleTime, Site.REGISTER_OPERATION, input);

        } else {
            FMobileCode mobileCode = binderData.bind(input.getParamMap(), null, FMobileCode.class);
            InvokerResolverErrors.checkError(binderData.getBinderResult(), null);
            if (PortalService.ME.findUser(mobileCode.mobile, type) != null) {
                InvokerResolverErrors.onError("mobile", Site.MOBILE_REGISTERED, null, null);
            }

            idleTime = Pag.CONFIGURE.getMessageIdleTime();
            sendTime = PortalService.ME.sendMessageCode(mobileCode.mobile, PortalService.REGISTER_TAG, Site.TPL.getCodeMessage(), idleTime, Site.REGISTER_OPERATION, input);
        }

        return "success";
    }

    /**
     * 用户注册
     */
    public String register(@Param int type, Input input) {
        type = getRegisterType(type);
        InModel model = input.getModel();
        if (input.getMethod() == InMethod.POST) {
            if (type == 1) {
                if (!Asset_verify.verifyInput(input)) {
                    model.put("click", "#verifyCode");
                    InvokerResolverErrors.onError("verifyCode", Site.VERIFY_ERROR, null, null);
                }
            }

            BinderData binderData = input.getBinderData();
            binderData.getBinderResult().setValidation(true);
            FRegister register = binderData.bind(input.getParamMap(), null, FRegister.class);
            JUser user = new JUser();
            if (type == 1) {
                FUsername username = binderData.bind(input.getParamMap(), null, FUsername.class);
                InvokerResolverErrors.checkError(binderData.getBinderResult(), null);
                user.setUsername(username.username);


            } else if (type == 2) {
                FEmailCode emailCode = binderData.bind(input.getParamMap(), null, FEmailCode.class);
                InvokerResolverErrors.checkError(binderData.getBinderResult(), null);
                if (PortalService.ME.verifyCode(emailCode.email, PortalService.REGISTER_TAG) != 0) {
                    InvokerResolverErrors.onError("code", Site.VERIFY_ERROR, null, null);
                }

                user.setEmail(emailCode.email);

            } else {
                FMobileCode mobileCode = binderData.bind(input.getParamMap(), null, FMobileCode.class);
                InvokerResolverErrors.checkError(binderData.getBinderResult(), null);
                if (PortalService.ME.verifyCode(mobileCode.mobile, PortalService.REGISTER_TAG) != 0) {
                    InvokerResolverErrors.onError("code", Site.VERIFY_ERROR, null, null);
                }

                user.setMobile(mobileCode.mobile);
            }

            user.setCreateTime(ContextUtils.getContextTime());
            user.setPasswordBase(register.password);
            user.setUserType(type == 1 ? JeUserType.USER_VALIDATING : JeUserType.USER_NORMAL);
            user.setActivation(!Pag.CONFIGURE.isRegisterUserNoActive());
            try {
                CrudServiceUtils.merge("JUser", null, user, true, null, null);

            } catch (ConstraintViolationException e) {
                if (type == 1) {
                    InvokerResolverErrors.onError("username", Site.USERNAME_REGISTERED, null, null);

                } else if (type == 2) {
                    InvokerResolverErrors.onError("email", Site.EMAIL_REGISTERED, null, null);

                } else {
                    InvokerResolverErrors.onError("mobile", Site.MOBILE_REGISTERED, null, null);
                }
            }

            return "success";
        }

        model.put("type", type);
        return "portal/user/register";
    }

    /**
     * 修改密码
     */
    public String password(Input input) {
        return "portal/user/password";
    }


}
