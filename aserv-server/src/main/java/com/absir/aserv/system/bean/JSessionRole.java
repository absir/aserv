/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-12 下午7:45:42
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.base.JbUserRole;

import java.io.Serializable;

@SuppressWarnings("serial")
public class JSessionRole extends JbUserRole implements Serializable {

    public JSessionRole() {
    }

    public JSessionRole(Long id, String rolename) {
        setId(id);
        setRolename(rolename);
    }
}
