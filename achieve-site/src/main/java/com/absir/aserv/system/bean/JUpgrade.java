/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月8日 下午1:48:02
 */
package com.absir.aserv.system.bean;


import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.*;
import com.absir.aserv.system.crud.DateCrudFactory;
import com.absir.aserv.system.crud.UploadCrudFactory;

import javax.persistence.Entity;

/**
 * @author absir
 */
@MaEntity(parent = {@MaMenu("功能管理"), @MaMenu("版本管理")}, name = "升级")
@JaModel(desc = true)
@Entity
public class JUpgrade extends JbBean {

    @JaLang("升级文件")
    @JaEdit(types = "file", groups = JaEdit.GROUP_LIST)
    @JaCrud(factory = UploadCrudFactory.class, parameters = {"-1", "zip,war"})
    private String upgradeFile;

    @JaLang("版本")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String version;

    @JaLang("描述")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String descriptor;

    @JaLang("结束")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean done;

    @JaLang("开始时间")
    @JaEdit(editable = JeEditable.LOCKED, types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {JaCrud.Crud.CREATE, JaCrud.Crud.UPDATE}, factory = DateCrudFactory.class)
    private long beginTime;

    @JaLang(value = "成功")
    @JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKED)
    private boolean success;

    @JaLang("修改时间")
    @JaEdit(editable = JeEditable.LOCKED, types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {JaCrud.Crud.CREATE, JaCrud.Crud.UPDATE}, factory = DateCrudFactory.class)
    private long updateTime;

    /**
     * @return the upgradeFile
     */
    public String getUpgradeFile() {
        return upgradeFile;
    }

    /**
     * @param upgradeFile the upgradeFile to set
     */
    public void setUpgradeFile(String upgradeFile) {
        this.upgradeFile = upgradeFile;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the descriptor
     */
    public String getDescriptor() {
        return descriptor;
    }

    /**
     * @param descriptor the descriptor to set
     */
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
