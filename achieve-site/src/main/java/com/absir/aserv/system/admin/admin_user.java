/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-2 下午8:38:01
 */
package com.absir.aserv.system.admin;

import com.absir.aserv.system.bean.JUser;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.server.in.InMethod;
import com.absir.server.in.Input;
import com.absir.server.value.Errors;
import com.absir.server.value.Mapping;
import com.absir.server.value.Server;
import com.absir.server.value.Validate;
import com.absir.validator.value.Confirm;
import com.absir.validator.value.NotEmpty;

@Server
public class admin_user extends AdminServer {

    public void password(Input input) {
        input.getModel().put("userId", SecurityService.ME.getUserBase(input).getUserId());
    }

    public static class FPassword {

        @JaLang("原密码")
        @NotEmpty
        public String oldPassword;

        @JaLang("新密码")
        @NotEmpty
        public String newPassword;

        @JaLang("确认密码")
        @NotEmpty
        @Confirm("newPassword")
        public String confirmPassword;
    }

    /**
     * 修改密码
     */
    @Errors
    @Mapping(method = InMethod.POST)
    public String password(@Validate FPassword password, Input input) {
        JUser user = BeanService.ME.get(JUser.class, SecurityService.ME.getUserBase(input).getUserId());
        if (user == null) {
            input.getModel().put("message", input.getLang(SecurityService.USER_NO_LOGIN));
            return "admin/failed";
        }

        if (!SecurityService.ME.validator(user, password.oldPassword, 0, 0, input.getAddress())) {
            input.addPropertyError("oldPassword", input.getLang(SecurityService.PASSWORD_ERROR), password, true);
            return "admin/failed";
        }

        user.setPasswordBase(password.newPassword);
        CrudServiceUtils.merge("JUser", null, user, false, user, null);
        return "admin/success";
    }
}
