/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月8日 下午1:48:02
 */
package com.absir.aserv.master.bean;

import com.absir.aserv.master.bean.base.JbBeanTargets;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.*;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.crud.DateCrudFactory;
import com.absir.aserv.system.crud.UploadCrudFactory;

import javax.persistence.Entity;

/**
 * @author absir
 *
 */
@MaEntity(parent = {@MaMenu("节点管理")}, name = "升级")
@JaModel(desc = true)
@Entity
public class JUpgradeSlave extends JbBeanTargets {

    @JaLang("升级文件")
    @JaEdit(types = "file")
    @JaCrud(factory = UploadCrudFactory.class, parameters = "zip,war")
    private String upgradeFile;

    @JaLang("资源文件")
    @JaEdit(types = "file")
    @JaCrud(factory = UploadCrudFactory.class, parameters = "zip")
    private String resourceFile;

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

    @JaLang("静默")
    private boolean silent;

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

    /**
     * @return the resourceFile
     */
    public String getResourceFile() {
        return resourceFile;
    }

    /**
     * @param resourceFile
     *            the resourceFile to set
     */
    public void setResourceFile(String resourceFile) {
        this.resourceFile = resourceFile;
    }

    /**
     * @return the beginTime
     */
    public long getBeginTime() {
        return beginTime;
    }

    /**
     * @param beginTime
     *            the beginTime to set
     */
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
     * @param success
     *            the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the createTime
     */
    public long getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     *            the createTime to set
     */
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the updateTime
     */
    public long getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     *            the updateTime to set
     */
    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return the silent
     */
    public boolean isSilent() {
        return silent;
    }

    /**
     * @param silent
     *            the silent to set
     */
    public void setSilent(boolean silent) {
        this.silent = silent;
    }

}
