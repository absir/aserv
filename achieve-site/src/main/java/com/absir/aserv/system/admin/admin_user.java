/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-2 下午8:38:01
 */
package com.absir.aserv.system.admin;

import com.absir.aserv.system.bean.JUser;
import com.absir.aserv.system.crud.PasswordCrudFactory;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.binder.BinderResult;
import com.absir.core.kernel.KernelString;
import com.absir.server.in.InMethod;
import com.absir.server.in.Input;
import com.absir.server.value.Mapping;
import com.absir.server.value.Param;
import com.absir.server.value.Server;

@Server
public class admin_user extends AdminServer {

    public void password(Input input) {
        input.getModel().put("userId", SecurityService.ME.getUserBase(input).getUserId());
    }

    /**
     * 修改密码
     *
     * @param password
     * @param newPassword
     * @param input
     * @return
     */
    @Mapping(method = InMethod.POST)
    public String password(@Param String password, @Param String newPassword, @Param String confirmPassword, Input input) {
        JUser user = BeanService.ME.get(JUser.class, SecurityService.ME.getUserBase(input).getUserId());
        if (user == null) {
            return "admin/failed";
        }

        if (KernelString.isEmpty(newPassword)) {
            BinderResult result = new BinderResult();
            result.addPropertyError("newPassword", PasswordCrudFactory.PASSWORD_EMPTY, password);
            input.getModel().put("errors", result.getPropertyErrors());
            return "admin/failed";
        }

        if (!confirmPassword.equals(newPassword)) {
            BinderResult result = new BinderResult();
            result.addPropertyError("confirmPassword", PasswordCrudFactory.PASSWORD_NOT_CONFIRM, password);
            input.getModel().put("errors", result.getPropertyErrors());
            return "admin/failed";
        }

        if (!SecurityService.ME.validator(user, password, 0, 0, input.getAddress())) {
            BinderResult result = new BinderResult();
            result.addPropertyError("password", PasswordCrudFactory.PASSWORD_ERROR, password);
            input.getModel().put("errors", result.getPropertyErrors());
            return "admin/failed";
        }

        user.setPasswordBase(newPassword);
        CrudServiceUtils.merge("JUser", null, user, false, user, null);
        return "admin/success";
    }

    //todo 修改密码用DTO 试试
    protected static class ddd {

    }
}
