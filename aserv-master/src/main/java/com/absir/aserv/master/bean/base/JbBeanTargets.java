/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月14日 上午10:28:04
 */
package com.absir.aserv.master.bean.base;

import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaName;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbBeanTargets extends JbBean {

    @JaLang("目标")
    @JaName("JSlaveServer")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    @Column(length = 10240)
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    private long[] targets;

    private transient long[] lastTargets;

    @JaLang("全部目标")
    private boolean allTarget;

    @JaLang("描述")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String description;

    public long[] getTargets() {
        return targets;
    }

    public void setTargets(long[] targets) {
        if (lastTargets == null) {
            lastTargets = targets;
        }

        this.targets = targets;
    }

    public long[] getLastTargets() {
        return lastTargets;
    }

    public boolean isAllTarget() {
        return allTarget;
    }

    public void setAllTarget(boolean allTarget) {
        this.allTarget = allTarget;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
