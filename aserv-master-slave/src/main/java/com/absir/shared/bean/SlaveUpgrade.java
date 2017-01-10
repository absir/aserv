package com.absir.shared.bean;

import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.system.crud.value.UploadRule;

import javax.persistence.Embeddable;

/**
 * Created by absir on 2017/1/10.
 */
@Embeddable
public class SlaveUpgrade {

    @JaLang("动作时间")
    @JaEdit(editable = JeEditable.DISABLE)
    private long actionTime;

    @JaLang("开始时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
    private long beginTime;

    @JaLang("升级文件")
    @JaEdit(types = "file")
    @UploadRule("@slave/upgrade_:rand.:ext")
    @JaCrud(factory = UploadCrudFactory.class, parameters = {"-1", "zip,war"})
    private String upgradeFile;

    @JaLang("升级版本")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String upgradeVersion;

    @JaLang("升级描述")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String upgradeDescriptor;

    @JaLang("升级MD5")
    private String upgradeMd5;

    @JaLang("资源文件")
    @JaEdit(types = "file")
    @UploadRule("@slave/resource_:rand.:ext")
    @JaCrud(factory = UploadCrudFactory.class, parameters = {"-1", "zip"})
    private String resourceFile;

    @JaLang("资源MD5")
    private String resourceMd5;

    @JaLang("强制重启")
    private boolean forceRestart;

    @JaLang("特殊")
    private boolean special;

    @JaLang("升级消息")
    @JaEdit(types = "text")
    private String upgradeMessage;

    public long getActionTime() {
        return actionTime;
    }

    public void setActionTime(long actionTime) {
        this.actionTime = actionTime;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public String getUpgradeFile() {
        return upgradeFile;
    }

    public void setUpgradeFile(String upgradeFile) {
        this.upgradeFile = upgradeFile;
    }

    public String getUpgradeVersion() {
        return upgradeVersion;
    }

    public void setUpgradeVersion(String upgradeVersion) {
        this.upgradeVersion = upgradeVersion;
    }

    public String getUpgradeDescriptor() {
        return upgradeDescriptor;
    }

    public void setUpgradeDescriptor(String upgradeDescriptor) {
        this.upgradeDescriptor = upgradeDescriptor;
    }

    public String getUpgradeMd5() {
        return upgradeMd5;
    }

    public void setUpgradeMd5(String upgradeMd5) {
        this.upgradeMd5 = upgradeMd5;
    }

    public String getResourceFile() {
        return resourceFile;
    }

    public void setResourceFile(String resourceFile) {
        this.resourceFile = resourceFile;
    }

    public String getResourceMd5() {
        return resourceMd5;
    }

    public void setResourceMd5(String resourceMd5) {
        this.resourceMd5 = resourceMd5;
    }

    public boolean isForceRestart() {
        return forceRestart;
    }

    public void setForceRestart(boolean forceRestart) {
        this.forceRestart = forceRestart;
    }

    public boolean isSpecial() {
        return special;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

    public String getUpgradeMessage() {
        return upgradeMessage;
    }

    public void setUpgradeMessage(String upgradeMessage) {
        this.upgradeMessage = upgradeMessage;
    }
}
