/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年7月10日 下午3:14:11
 */
package com.absir.aserv.slave.bean.base;

import com.absir.aserv.system.bean.base.JbBeanL;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbBeanLTargets extends JbBeanL {

    @JaLang("目标")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    @Column(length = 10240)
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    private long[] targets;

    @JaLang("描述")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String description;

    public long[] getTargets() {
        return targets;
    }

    public void setTargets(long[] targets) {
        this.targets = targets;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
