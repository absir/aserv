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

import javax.persistence.MappedSuperclass;

/**
 * @author absir
 *
 */
@MappedSuperclass
public class JbBeanLTargets extends JbBeanL {

    @JaLang("目标")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private long[] targets;

    @JaLang("描述")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String description;

    /**
     * @return the targets
     */
    public long[] getTargets() {
        return targets;
    }

    /**
     * @param targets the targets to set
     */
    public void setTargets(long[] targets) {
        this.targets = targets;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
