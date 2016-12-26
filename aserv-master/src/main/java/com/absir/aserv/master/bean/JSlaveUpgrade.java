/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月8日 下午1:48:02
 */
package com.absir.aserv.master.bean;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.master.bean.base.JbBeanSlaves;
import com.absir.aserv.master.service.MasterUpgradeService;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.*;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.crud.DateCrudFactory;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.upgrade.UpgradeService;
import com.absir.bean.lang.LangCodeUtils;
import com.absir.property.PropertyErrors;
import com.absir.server.in.Input;

import javax.persistence.Entity;
import javax.persistence.Transient;

@MaEntity(parent = {@MaMenu("节点管理")}, name = "升级")
@JaModel(desc = true)
@Entity
public class JSlaveUpgrade extends JbBeanSlaves implements ICrudBean {

    @JaLang("升级文件")
    @JaEdit(types = "file")
    @JaCrud(factory = UploadCrudFactory.class, parameters = {"0", "zip,war"})
    private String upgradeFile;

    @JaLang("升级")
    @JaEdit(editable = JeEditable.ENABLE)
    @Transient
    private boolean upgrade;

    @JaLang("升级版本")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String upgradeVersion;

    @JaLang("升级MD5")
    private String upgradeMd5;

    @JaLang("资源文件")
    @JaEdit(types = "file")
    @JaCrud(factory = UploadCrudFactory.class, parameters = {"0", "zip"})
    private String resourceFile;

    @JaLang("开始时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
    private long beginTime;

    @JaLang("创建时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {Crud.CREATE}, factory = DateCrudFactory.class)
    private long createTime;

    @JaLang("修改时间")
    @JaEdit(editable = JeEditable.LOCKED, types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {Crud.CREATE, Crud.UPDATE}, factory = DateCrudFactory.class)
    private long updateTime;

    @JaLang("特殊")
    private boolean special;

    @JaLang("升级消息")
    @JaEdit(types = "text")
    private String upgradeMessage;

    public String getUpgradeFile() {
        return upgradeFile;
    }

    public void setUpgradeFile(String upgradeFile) {
        this.upgradeFile = upgradeFile;
    }

    public boolean isUpgrade() {
        return upgrade;
    }

    public void setUpgrade(boolean upgrade) {
        this.upgrade = upgrade;
    }

    public String getUpgradeVersion() {
        return upgradeVersion;
    }

    public void setUpgradeVersion(String upgradeVersion) {
        this.upgradeVersion = upgradeVersion;
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

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
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

    @Override
    public void processCrud(Crud crud, CrudHandler handler, Input input) {
        if (crud != Crud.DELETE && handler.isPersist()) {
            PropertyErrors errors = handler.getErrors();
            if (errors != null) {
                String app = MasterUpgradeService.ME.crudSlaveUpgrade(this);
                if (app == null) {
                    UploadCrudFactory.ME.delete(upgradeFile);
                    UploadCrudFactory.ME.delete(resourceFile);
                    errors.addPropertyError("upgradeFile", LangCodeUtils.getLangMessage(UpgradeService.NOT_VALIDATOR, input), upgradeFile);
                    return;
                }


            }
        }
    }
}
