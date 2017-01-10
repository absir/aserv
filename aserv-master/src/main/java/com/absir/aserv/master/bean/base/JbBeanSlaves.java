/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月14日 上午10:28:04
 */
package com.absir.aserv.master.bean.base;

import com.absir.aserv.master.bean.JSlave;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaName;
import com.absir.aserv.system.service.statics.EntityStatics;
import com.absir.server.in.Input;
import com.absir.validator.value.NotEmpty;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@MappedSuperclass
public class JbBeanSlaves extends JbBean {

    @JaLang("应用")
    @NotEmpty
    @JaEdit(groups = JaEdit.GROUP_SUG, listColType = 1, metas = "{\"input_ext\": \"ab_toggles='linkage' linkage='slaveIds' select='${SITE_ROUTE}admin/open/suggest/JSlave?appCode%20%3D=$val'\"}")
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

    protected void workSlaves(Input input) {
        String[] slaveIds = this.slaveIds;
        Set<String> stopSlaveIds = null;
        if (isAllSlaveIds()) {
            List<JSlave> slaves = (List<JSlave>) EntityStatics.suggestParam("JSlave", "appCode=" + appCode, input);
            int size = slaves.size();
            slaveIds = new String[size];
            for (int i = 0; i < size; i++) {
                slaveIds[i] = slaves.get(i).getId();
            }

            setSlaveIds(slaveIds);

        } else {
            stopSlaveIds = new HashSet<String>();
            if (lastAllSlaveIds == 1) {
                for (JSlave slave : (List<JSlave>) EntityStatics.suggestParam("JSlave", "appCode=" + appCode, input)) {
                    stopSlaveIds.add(slave.getId());
                }

            } else {
                if (lastSlaveIds != null) {
                    for (String slaveId : lastSlaveIds) {
                        stopSlaveIds.add(slaveId);
                    }
                }
            }
        }

        if (slaveIds != null) {
            for (String slaveId : slaveIds) {
                doSlaveId(slaveId);
                if (stopSlaveIds != null) {
                    stopSlaveIds.remove(slaveId);
                }
            }
        }

        if (stopSlaveIds != null) {
            for (String slaveId : stopSlaveIds) {
                stopSlaveId(slaveId);
            }
        }
    }

    protected void doSlaveId(String slaveId) {
    }

    protected void stopSlaveId(String slaveId) {
    }
}
