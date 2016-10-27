package com.absir.aserv.master.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBeanS;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;

/**
 * Created by absir on 2016/10/26.
 */
@MaEntity(parent = {@MaMenu("节点管理")}, name = "注册", value = @MaMenu(order = -128))
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JSlaveRegister extends JbBeanS {

    @JaLang("允许")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean allow;

    @JaLang("节点编号")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String slaveId;

    @JaLang("备注")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String remark;

    public boolean isAllow() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    public String getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(String slaveId) {
        this.slaveId = slaveId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
