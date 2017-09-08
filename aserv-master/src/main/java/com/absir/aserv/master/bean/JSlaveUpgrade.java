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
import com.absir.aserv.master.bean.base.JbSlaveTargets;
import com.absir.aserv.master.handler.MasterHandler;
import com.absir.aserv.master.service.MasterSyncService;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.*;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.crud.DateCrudFactory;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.upgrade.UpgradeService;
import com.absir.bean.lang.LangCodeUtils;
import com.absir.client.helper.HelperEncrypt;
import com.absir.context.core.ContextUtils;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.property.PropertyErrors;
import com.absir.server.in.Input;
import com.absir.shared.bean.EUpgradeStatus;
import com.absir.shared.bean.SlaveUpgrade;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.IOException;
import java.util.Map;

@MaEntity(parent = {@MaMenu("节点管理")}, name = "升级")
@JaModel(desc = true)
@Entity
public class JSlaveUpgrade extends JbSlaveTargets implements ICrudBean {

    @JaLang("升级")
    @JaEdit(editable = JeEditable.ENABLE)
    @Transient
    private boolean upgrade;

    @JaLang("创建时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {Crud.CREATE}, factory = DateCrudFactory.class)
    private long createTime;

    @JaLang("修改时间")
    @JaEdit(editable = JeEditable.LOCKED, types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {Crud.CREATE, Crud.UPDATE}, factory = DateCrudFactory.class)
    private long updateTime;

    @JaLang("节点升级")
    private SlaveUpgrade slaveUpgrade;

    public boolean isUpgrade() {
        return upgrade;
    }

    public void setUpgrade(boolean upgrade) {
        this.upgrade = upgrade;
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

    public SlaveUpgrade getSlaveUpgrade() {
        return slaveUpgrade;
    }

    public void setSlaveUpgrade(SlaveUpgrade slaveUpgrade) {
        this.slaveUpgrade = slaveUpgrade;
    }

    @Override
    public void processCrud(Crud crud, CrudHandler handler, Input input) {
        if (!handler.isPersist()) {
            return;
        }

        if (crud != Crud.DELETE) {
            PropertyErrors errors = handler.getErrors();
            if (errors != null && slaveUpgrade != null) {
                try {
                    if (KernelString.isEmpty(appCode)) {
                        errors.addPropertyError("appCode", LangCodeUtils.getLangMessage(UpgradeService.NO_APP_CODE, input), appCode);
                        return;
                    }

                    Map<String, Object> versionMap = KernelString.isEmpty(slaveUpgrade.getUpgradeFile()) ? null : UpgradeService.ME.getVersionMap(UploadCrudFactory.ME.getProtectedFile(slaveUpgrade.getUpgradeFile()));
                    if (versionMap != null) {
                        if (!KernelObject.equals(appCode, UpgradeService.ME.getAppCode(versionMap))) {
                            errors.addPropertyError("slaveUpgrade.upgradeFile", LangCodeUtils.getLangMessage(UpgradeService.NOT_VALIDATOR, input), slaveUpgrade.getUpgradeFile());
                            return;
                        }
                    }

                    if (upgrade) {
                        String propertyName = null;
                        try {
                            if (versionMap != null) {
                                slaveUpgrade.setUpgradeVersion(UpgradeService.ME.getVersion(versionMap));
                                if (KernelString.isEmpty(slaveUpgrade.getUpgradeDescriptor())) {
                                    slaveUpgrade.setUpgradeDescriptor(UpgradeService.ME.getVersionName(versionMap));
                                }

                                propertyName = "slaveUpgrade.upgradeFile";
                                slaveUpgrade.setUpgradeMd5(HelperEncrypt.encryptionMD5(UploadCrudFactory.ME.getUploadStream(slaveUpgrade.getUpgradeFile())));
                            }

                            if (!KernelString.isEmpty(slaveUpgrade.getResourceFile())) {
                                propertyName = "slaveUpgrade.resourceFile";
                                slaveUpgrade.setResourceMd5(HelperEncrypt.encryptionMD5(UploadCrudFactory.ME.getUploadStream(slaveUpgrade.getResourceFile())));
                            }

                            slaveUpgrade.setActionTime(ContextUtils.getContextTime());
                            workSlaves(input);

                        } catch (IOException e) {
                            if (propertyName != null) {
                                errors.addPropertyError(propertyName, LangCodeUtils.getLangMessage(UpgradeService.IO_EXCEPTION, input), e);
                            }

                            Environment.throwable(e);
                        }
                    }

                } finally {
                    if (errors.hashErrors()) {
                        UploadCrudFactory.ME.delete(slaveUpgrade.getUpgradeFile());
                        UploadCrudFactory.ME.delete(slaveUpgrade.getResourceFile());
                    }
                }
            }
        }
    }

    @Override
    protected void doSlaveId(String slaveId) {
        MasterHandler.ME.upgradeStatues(slaveId, EUpgradeStatus.ACTIONING, null, false);
        MasterSyncService.ME.addSlaveSynchRpc(slaveId, "slaveUpgrade", MasterSyncService.RpcDataSlave.upgrade(slaveUpgrade), false);
    }

    @Override
    protected void stopSlaveId(String slaveId) {
        MasterHandler.ME.upgradeStatues(slaveId, EUpgradeStatus.ACTIONING_CANCEL, null, false);
        MasterSyncService.ME.addSlaveSynchRpc(slaveId, "slaveUpgrade", MasterSyncService.RpcDataSlave.upgrade(null), false);
    }

}
