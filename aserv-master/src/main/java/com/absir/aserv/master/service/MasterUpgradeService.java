package com.absir.aserv.master.service;

import com.absir.aserv.master.bean.JSlaveUpgrade;
import com.absir.aserv.task.JaTask;
import com.absir.aserv.task.TaskService;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Value;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelString;

/**
 * Created by absir on 2016/11/18.
 */
@Base
@Bean
public class MasterUpgradeService {

    public static final MasterUpgradeService ME = BeanFactoryUtils.get(MasterUpgradeService.class);

    @Value("master.upgrade.slave.app")
    private String slaveAppCode = "slave";

    public String crudSlaveUpgrade(JSlaveUpgrade slaveUpgrade) {
        String upgradeFile = slaveUpgrade.getUpgradeFile();
        if (!KernelString.isEmpty(upgradeFile)) {
//            String filePath = UploadCrudFactory.getUploadPath() + upgradeFile;
//            File file = new File(filePath);
//            if (!file.exists()) {
//                return null;
//            }

//            Map<String, Object> versionMap = UpgradeService.ME.getVersionMapAppCode(file, slaveAppCode);
//            if (versionMap == null) {
//                return null;
//
//            } else {
//                String version = UpgradeService.ME.getVersion(versionMap);
//                if (!KernelString.isEmpty(version)) {
//                    slaveUpgrade.setUpgradeVersion(version);
//                }
//
//                if (KernelString.isEmpty(slaveUpgrade.getMark())) {
//                    slaveUpgrade.setMark(BeanConfigImpl.getMapValue(versionMap, "version.name", null, String.class));
//                }
//
//                try {
//                    slaveUpgrade.setUpgradeMd5(HelperEncrypt.encryptionMD5(new FileInputStream(file)));
//
//                } catch (Exception e) {
//                    Environment.throwable(e);
//                    return null;
//                }
//            }
        }

        return slaveAppCode;
    }

    public void planSlaveUpgrade(String app, JSlaveUpgrade slaveUpgrade) {
        String[] slaveIds = slaveUpgrade.getSlaveIds();
        if (slaveIds == null || slaveIds.length == 0) {
            if (!slaveUpgrade.isAllSlaveIds()) {
                return;
            }

            
        }

        long contextTime = ContextUtils.getContextTime();
        long beginTime = slaveUpgrade.getBeginTime();
        String id = app + "@" + slaveUpgrade.getGroup();

        TaskService.ME.addPanel(id, "slaveUpgrade", beginTime, (beginTime < contextTime ? contextTime : beginTime) + 3600000, 3, slaveUpgrade);
        if (slaveUpgrade.isAllSlaveIds()) {


        } else {

        }
    }

    @JaTask("slaveUpgrade")
    public void doSlaveUpgrade(JSlaveUpgrade slaveUpgrade) {

    }

}
