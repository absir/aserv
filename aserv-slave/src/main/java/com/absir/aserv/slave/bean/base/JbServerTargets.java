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
import com.absir.core.kernel.KernelString;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbServerTargets extends JbBeanL {

    @JaLang("目标服务")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    @Column(length = 10240)
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonArray")
    private long[] serverIds;

    @JaLang("全部服务")
    private boolean allServerIds;

    @JaLang("组号")
    private String[] groupIds;

    @JaLang("备注")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String mark;

    public long[] getServerIds() {
        return serverIds;
    }

    public void setServerIds(long[] serverIds) {
        this.serverIds = serverIds;
    }

    public boolean isAllServerIds() {
        return allServerIds;
    }

    public void setAllServerIds(boolean allServerIds) {
        this.allServerIds = allServerIds;
    }

    public String[] getGroupIds() {
        if (groupIds != null) {
            if (groupIds.length == 0) {
                groupIds = null;

            } else if (groupIds.length == 1 && KernelString.isEmpty(groupIds[0])) {
                groupIds = null;
            }
        }

        return groupIds;
    }

    public void setGroupIds(String[] groupIds) {
        this.groupIds = groupIds;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
