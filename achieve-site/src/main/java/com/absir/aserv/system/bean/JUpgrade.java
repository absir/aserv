/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月8日 下午1:48:02
 */
package com.absir.aserv.system.bean;


import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.*;
import com.absir.aserv.system.crud.DateCrudFactory;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.task.JaTask;
import com.absir.aserv.upgrade.UpgradeService;
import com.absir.bean.basis.Configure;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelString;
import com.absir.server.in.Input;
import com.absir.server.route.RouteAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.File;
import java.util.Map;

/**
 * @author absir
 */
@Configure
@MaEntity(parent = {@MaMenu("功能管理"), @MaMenu("版本管理")}, name = "升级")
@JaModel(desc = true)
@Entity
public class JUpgrade extends JbBean implements ICrudBean {

    @JaLang("升级文件")
    @JaEdit(types = "file", groups = JaEdit.GROUP_LIST)
    @JaCrud(factory = UploadCrudFactory.class, parameters = {"-1", "zip,war"})
    private String upgradeFile;

    @JaLang("版本")
    @JaEdit(groups = JaEdit.GROUP_LIST, listColType = 1)
    private String version;

    @JaLang("描述")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String descriptor;

    @JaLang(value = "验证")
    @JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKED, listColType = 1)
    private boolean validation;

    @JaLang("修改时间")
    @JaEdit(editable = JeEditable.LOCKED, types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {JaCrud.Crud.CREATE}, factory = DateCrudFactory.class)
    private long createTime;

    @JaLang("修改时间")
    @JaEdit(editable = JeEditable.LOCKED, types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {JaCrud.Crud.CREATE, JaCrud.Crud.UPDATE}, factory = DateCrudFactory.class)
    private long updateTime;

    @JaLang("升级")
    @JaEdit(editable = JeEditable.ENABLE)
    @Transient
    private boolean upgrade;

    @JaLang("开始时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {JaCrud.Crud.CREATE, JaCrud.Crud.UPDATE}, factory = DateCrudFactory.class)
    private long beginTime;

    public String getUpgradeFile() {
        return upgradeFile;
    }

    public void setUpgradeFile(String upgradeFile) {
        this.upgradeFile = upgradeFile;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public boolean isValidation() {
        return validation;
    }

    public void setValidation(boolean validation) {
        this.validation = validation;
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

    public boolean isUpgrade() {
        return upgrade;
    }

    public void setUpgrade(boolean upgrade) {
        this.upgrade = upgrade;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    @Override
    public void processCrud(JaCrud.Crud crud, CrudHandler handler, Input input) {
        if (handler.isPersist() && crud != JaCrud.Crud.DELETE) {
            Map<String, Object> versionMap = null;
            String filePath = null;
            if (!KernelString.isEmpty(upgradeFile)) {
                filePath = UploadCrudFactory.getUploadPath() + upgradeFile;
                File file = new File(filePath);
                versionMap = UpgradeService.ME.getVersionMap(file);
            }

            if (versionMap == null) {
                validation = false;

            } else {
                validation = true;
                String version = UpgradeService.ME.getVersion(versionMap);
                if (!KernelString.isEmpty(version)) {
                    this.version = version;
                }

                if (KernelString.isEmpty(descriptor)) {
                    descriptor = BeanConfigImpl.getMapValue(versionMap, "version.name", null, String.class);
                }

                if (upgrade) {
                    if (beginTime <= ContextUtils.getContextTime()) {
                        upgradeFile(RouteAdapter.ADAPTER_TIME, filePath);

                    } else {
                        upgradeFile(RouteAdapter.ADAPTER_TIME, filePath);
                    }

                    upgrade = false;
                }
            }
        }
    }

    protected static final Logger LOGGER = LoggerFactory.getLogger(JUpgrade.class);

    @JaTask("upgradeFile")
    public static void upgradeFile(long adapterTime, String filePath) {
        if (RouteAdapter.ADAPTER_TIME == adapterTime) {
            try {
                UpgradeService.ME.restartUpgrade(filePath, true);

            } catch (Exception e) {
                LOGGER.error("upgradeFile error " + filePath, e);
            }
        }
    }

}
