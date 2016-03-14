/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-2 下午8:38:01
 */
package com.absir.aserv.system.admin;

import com.absir.aserv.system.bean.JUser;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.binder.BinderData;
import com.absir.binder.BinderResult;
import com.absir.server.in.InMethod;
import com.absir.server.in.Input;
import com.absir.server.value.Mapping;
import com.absir.server.value.Param;
import com.absir.server.value.Server;

/**
 * @author absir
 */
@Server
public class admin_user extends AdminServer {

    /**
     * @param input
     */
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
    public String password(@Param String password, @Param String newPassword, Input input) {
        JUser user = BeanService.ME.get(JUser.class, SecurityService.ME.getUserBase(input).getUserId());
        if (user == null) {
            return "admin/failed.ajax";
        }

        if (!SecurityService.ME.validator(user, password, 0, 0)) {
            return "admin/failed.ajax";
        }

        user.setPassword(newPassword);
        BinderData binderData = new BinderData();
        BinderResult binderResult = binderData.getBinderResult();
        // BinderServiceUtils.getValidator().validate(user, bindingResult);
        if (binderResult.contain("password")) {
            return "admin/failed.ajax";
        }

        CrudServiceUtils.merge("JUser", null, user, false, user, null);
        return "admin/successed.ajax";
    }
}
