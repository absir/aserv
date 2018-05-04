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
import com.absir.aserv.system.bean.value.JaModel;
import com.absir.aserv.system.bean.value.JaName;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelString;
import com.absir.validator.value.NotEmpty;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@JaModel(desc = true)
@MappedSuperclass
public class JbServerTargets extends JbBean {

    //@Access(AccessType.PROPERTY)
    @JaLang(value = "目标服务", tag = "targetServer")
    @JaName("JSlaveServer")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    @Column(length = 10240)
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonArray")
    private long[] serverIds;

    private transient long[] lastServerIds;

    //@Access(AccessType.PROPERTY)
    @JaLang("全部服务")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean allServerIds;

    private transient int lastAllServerIds;

    @JaLang("组号")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonArray")
    private String[] groupIds;

    private transient String[] lastGroupIds;

    @NotEmpty
    @JaLang("备注")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String mark;

    public boolean notRequireSync() {
        return false;
    }

    public final boolean noLastServerSelected() {
        return (lastServerIds == null || lastServerIds.length == 0) && lastAllServerIds != 1 && (lastGroupIds == null || lastGroupIds.length == 0);
    }

    public long[] getServerIds() {
        return serverIds;
    }

    public final void setServerIds(long[] serverIds) {
        if (lastServerIds == null) {
            lastServerIds = serverIds == null ? KernelLang.NULL_LONGS : this.serverIds;
        }

        this.serverIds = serverIds;
    }

    public long[] getLastServerIds() {
        return lastServerIds;
    }

    public boolean isAllServerIds() {
        return allServerIds;
    }

    public final void setAllServerIds(boolean allServerIds) {
        if (lastAllServerIds == 0) {
            lastAllServerIds = this.allServerIds ? 1 : -1;
        }

        this.allServerIds = allServerIds;
    }

    public int getLastAllServerIds() {
        return lastAllServerIds;
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
        if (lastGroupIds == null) {
            lastGroupIds = groupIds == null ? KernelLang.NULL_STRINGS : getGroupIds();
        }

        this.groupIds = groupIds;
    }

    public String[] getLastGroupIds() {
        return lastGroupIds;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
