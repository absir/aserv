/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月8日 下午1:48:02
 */
package com.absir.aserv.master.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.*;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.crud.DateCrudFactory;
import com.absir.aserv.system.crud.UploadCrudFactory;

import javax.persistence.Entity;

@MaEntity(parent = {@MaMenu("平台管理")}, name = "升级")
@JaModel(desc = true)
@Entity
public class JUpgrade extends JbBean {

    @JaLang("升级文件")
    @JaEdit(types = "file", groups = JaEdit.GROUP_LIST)
    @JaCrud(factory = UploadCrudFactory.class, parameters = "zip,war")
    private String upgradeFile;

    @JaLang("描述")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String description;

    @JaLang("开始时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
    private long beginTime;

    @JaLang(value = "成功")
    @JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKED)
    private boolean success;

    @JaLang("创建时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {Crud.CREATE}, factory = DateCrudFactory.class)
    private long createTime;

    @JaLang("修改时间")
    @JaEdit(editable = JeEditable.LOCKED, types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {Crud.CREATE, Crud.UPDATE}, factory = DateCrudFactory.class)
    private long updateTime;

    public String getUpgradeFile() {
        return upgradeFile;
    }

    public void setUpgradeFile(String upgradeFile) {
        this.upgradeFile = upgradeFile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

}
