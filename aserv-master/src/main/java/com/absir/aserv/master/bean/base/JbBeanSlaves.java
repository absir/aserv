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
import com.absir.validator.value.NotEmpty;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbBeanSlaves extends JbBean {

    @JaLang("应用")
    @NotEmpty
    @JaEdit(groups = JaEdit.GROUP_SUG, listColType = 1, metas = "{\"input_ext\": \"ab_toggle='linkage' linkage='slaveIds' select='${SITE_ROUTE}admin/open/suggest/JSlave?appCode%20%3D=$val'\"}")
    @JaName("JSlaveAppCode")
    public String appCode;

    @JaLang(value = "目标节点", tag = "targetSlave")
    @JaName("JSlave")
    @JaEdit(groups = JaEdit.GROUP_LIST, suggest = true, metas = "{\"suggest\":\"appCode=NONE\"}")
    @Column(length = 10240)
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    private String[] slaveIds;

    private transient String[] lastSlaveIds;

    @JaLang("全部节点")
    private boolean allSlaveIds;

    private transient int lastAllSlaveIds;

    @JaLang("备注")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String mark;

    public String[] getSlaveIds() {
        return slaveIds;
    }

    public final void setSlaveIds(String[] slaveIds) {
        if (lastSlaveIds == null) {
            lastSlaveIds = this.slaveIds;
        }

        this.slaveIds = slaveIds;
    }

    public String[] getLastSlaveIds() {
        return lastSlaveIds;
    }

    public boolean isAllSlaveIds() {
        return allSlaveIds;
    }

    public final void setAllSlaveIds(boolean allSlaveIds) {
        if (lastAllSlaveIds == 0) {
            lastAllSlaveIds = this.allSlaveIds ? 1 : -1;
        }

        this.allSlaveIds = allSlaveIds;
    }

    public int getLastAllSlaveIds() {
        return lastAllSlaveIds;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
