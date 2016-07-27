/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月29日 下午2:59:39
 */
package com.absir.aserv.system.portal;

import com.absir.aserv.developer.Pag;
import com.absir.aserv.system.bean.form.FEmailCode;
import com.absir.aserv.system.bean.form.FMobileCode;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeRoleLevel;
import com.absir.aserv.system.security.SecurityContext;
import com.absir.aserv.system.service.SecurityService;
import com.absir.binder.BinderData;
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
    public void registerCode(@Param int type, Input input) {
        type = getRegisterType(type);
        if (!(type == 2 || type == 3)) {
            throw new ServerException(ServerStatus.ON_DENIED);
        }

        //String value = input.getParam(type == 2 ? "email" : "mobile");
        long idleTime = type == 2 ? Pag.CONFIGURE.getEmailIdleTime() : Pag.CONFIGURE.getMessageIdleTime();
        BinderData binderData = input.getBinderData();
        binderData.getBinderResult().setValidation(true);
        if (type == 2) {
            FEmailCode emailCode = binderData.bind(input.getParamMap(), null, FEmailCode.class);
            InvokerResolverErrors.checkError(binderData.getBinderResult(), null);

        } else {
            FMobileCode mobileCode = binderData.bind(input.getParamMap(), null, FMobileCode.class);
        }
    }

    /**
     * 用户注册
     */
    public String register(@Param int type, Input input) {
        type = getRegisterType(type);
        InModel model = input.getModel();
        model.put("type", type);


//        if (input.getMethod() == InMethod.POST) {
//            JUser user = ParameterResolverBinder.getBinderObject(null, JUser.class, 1, input);
//            if (Pag.CONFIGURE.getVerifyTime() > 0) {
//                user.setActivation(false);
//
//            } else {
//                user.setActivation(true);
//            }
//
//            user.setDisabled(Pag.CONFIGURE.isRegisterUserDisable());
//            CrudServiceUtils.merge("JUser", null, user, true, null,
//                    input.getBinderData().getBinderResult().getPropertyFilter());
//            SecurityService.ME.setUserBase(user, input);
//            // 通知激活
//            if (!user.isActivation()) {
//                VerifierService.ME.persistVerifier(user, TAG, user.getId().toString(), Pag.CONFIGURE.getVerifyTime());
//            }
//        }

        return "portal/user/register";
    }

    /**
     * 修改密码
     */
    public String password(Input input) {
        return "portal/user/password";
    }


}
