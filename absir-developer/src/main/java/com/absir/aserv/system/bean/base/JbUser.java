/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-26 下午3:49:42
 */
package com.absir.aserv.system.bean.base;

import com.absir.aserv.system.assoc.DeveloperAssoc;
import com.absir.aserv.system.bean.proxy.JiDeveloper;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.orm.value.JaField;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbUser extends JbBean implements JiDeveloper {

    @JaLang("开发者")
    @JaEdit(editable = JeEditable.DISABLE)
    @JaField(assocClasses = JbPermission.class, referencEntityClass = DeveloperAssoc.class)
    private boolean developer;

    @JaLang("激活")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    private boolean activation;

    @JaLang("禁用")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    private boolean disabled;

    public boolean isDeveloper() {
        return developer;
    }

    public void setDeveloper(boolean developer) {
        this.developer = developer;
    }

    public boolean isActivation() {
        return activation;
    }

    public void setActivation(boolean activation) {
        this.activation = activation;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
