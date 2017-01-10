package com.absir.shared.bean;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.MappedSuperclass;

/**
 * Created by absir on 2016/11/18.
 */
@MappedSuperclass
public class SlaveUpgradeStatus {

    @JaLang("升级状态")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private EUpgradeStatus upgradeStatus;

    @JaLang("变更时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
    @JsonIgnore
    private long updateTime;

    @JaLang("参数")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String param;

    @JaLang("失败")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean failed;

    public EUpgradeStatus getUpgradeStatus() {
        return upgradeStatus;
    }

    public void setUpgradeStatus(EUpgradeStatus upgradeStatus) {
        this.upgradeStatus = upgradeStatus;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

}
