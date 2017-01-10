package com.absir.aserv.system.bean;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.init.InitBeanFactory;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.*;
import com.absir.aserv.system.crud.DateCrudFactory;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.system.crud.value.UploadRule;
import com.absir.aserv.system.service.impl.UpgradeServiceImpl;
import com.absir.aserv.task.TaskService;
import com.absir.aserv.upgrade.UpgradeService;
import com.absir.bean.lang.LangCodeUtils;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelString;
import com.absir.property.PropertyErrors;
import com.absir.server.in.Input;
import com.absir.server.route.RouteAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.File;
import java.util.Map;

/**
 * Created by absir on 2016/12/1.
 */
//@Configure
@MaEntity(parent = {@MaMenu("功能管理"), @MaMenu("版本管理")}, name = "升级")
@JaModel(desc = true)
@Entity
public class JUpgrade extends JbBean implements ICrudBean {

    protected static final Logger LOGGER = LoggerFactory.getLogger(JUpgrade.class);

    @JaLang("升级文件")
    @JaEdit(types = "file", groups = JaEdit.GROUP_LIST)
    @UploadRule("@upgrade/:rand.:ext")
    @JaCrud(factory = UploadCrudFactory.class, parameters = {"-1", "zip,war"})
    private String upgradeFile;

    @JaLang("更新")
    @JaEdit(editable = JeEditable.ENABLE)
    @Transient
    private boolean upgrade;

    @JaLang("版本")
    @JaEdit(groups = JaEdit.GROUP_LIST, listColType = 1)
    private String version;

    @JaLang("描述")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String descriptor;

    @JaLang("修改时间")
    @JaEdit(editable = JeEditable.LOCKED, types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {JaCrud.Crud.CREATE}, factory = DateCrudFactory.class)
    private long createTime;

    @JaLang("修改时间")
    @JaEdit(editable = JeEditable.LOCKED, types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {JaCrud.Crud.CREATE, JaCrud.Crud.UPDATE}, factory = DateCrudFactory.class)
    private long updateTime;

    @JaLang("开始时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long beginTime;

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

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    @Override
    public void processCrud(JaCrud.Crud crud, CrudHandler handler, Input input) {
        if (handler.isPersist() && crud != JaCrud.Crud.DELETE) {
            PropertyErrors errors = handler.getErrors();
            if (errors != null) {
                Map<String, Object> versionMap = null;
                if (!KernelString.isEmpty(upgradeFile)) {
                    File file = UploadCrudFactory.ME.getUploadFile(upgradeFile);
                    versionMap = UpgradeService.ME.getVersionMap(file);
                }

                if (versionMap == null || !UpgradeService.ME.validateAppCode(versionMap, InitBeanFactory.ME.getAppCode())) {
                    UploadCrudFactory.ME.delete(upgradeFile);
                    errors.addPropertyError("upgradeFile", LangCodeUtils.getLangMessage(UpgradeService.NOT_VALIDATOR, input), upgradeFile);
                    return;

                } else {
                    String version = UpgradeService.ME.getVersion(versionMap);
                    if (!KernelString.isEmpty(version)) {
                        this.version = version;
                    }

                    if (KernelString.isEmpty(descriptor)) {
                        descriptor = UpgradeService.ME.getVersionName(versionMap);
                    }

                    if (upgrade) {
                        if (beginTime <= ContextUtils.getContextTime()) {
                            UpgradeServiceImpl.ME.upgradeFile(RouteAdapter.ADAPTER_TIME, upgradeFile);

                        } else {
                            TaskService.ME.addPanel(null, "upgradeFile", beginTime, beginTime + 600000, 0, RouteAdapter.ADAPTER_TIME, upgradeFile);
                        }

                        upgrade = false;
                    }
                }
            }
        }
    }

}
