/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-8-30 上午10:21:56
 */
package com.absir.aserv.system.bean.base;

import com.absir.aserv.system.bean.proxy.JiUserRole;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.validator.value.Length;
import com.absir.validator.value.NotEmpty;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbUserRole extends JbBean implements JiUserRole {

    @JaLang("角色名称")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @Column(length = 32)
    @NotEmpty
    @Length(min = 2, max = 12)
    private String rolename;

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }
}