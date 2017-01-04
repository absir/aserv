package com.absir.aserv.master.service;

import com.absir.aserv.master.bean.JSlaveUpgrade;
import com.absir.aserv.task.JaTask;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by absir on 2016/11/18.
 */
@Base
@Bean
public class MasterUpgradeService {

    public static final Logger LOGGER = LoggerFactory.getLogger(MasterUpgradeService.class);

    public static final MasterUpgradeService ME = BeanFactoryUtils.get(MasterUpgradeService.class);

//    @Started
//    protected void postConstuctor() {
//        if (InitBeanFactory.ME.isDevelopOrVersionChange()) {
//            String deployPath = HelperFileName.normalizeNoEndSeparator(BeanFactoryUtils.getBeanConfig().getResourcePath() + "/shell/");
//            LOGGER.info("deploy : " + deployPath);
//            File file = new File(BeanFactoryUtils.getBeanConfig().getClassPath() + "deploy");
//            if (file.exists() && file.isDirectory()) {
//                for (File depFile : file.listFiles()) {
//                    if (depFile.isFile() && depFile.getName().endsWith(".zip")) {
//                        try {
//                            HelperFile.copyDirectoryOverWrite(new ZipInputStream(new FileInputStream(depFile)), new File(deployPath), false, null, true);
//
//                        } catch (Exception e) {
//                            LOGGER.error("deployDir " + depFile, e);
//                        }
//                    }
//                }
//            }
//
//
//        }
//    }

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

        return "";
    }

    public void planSlaveUpgrade(String app, JSlaveUpgrade slaveUpgrade) {
//        String[] slaveIds = slaveUpgrade.getSlaveIds();
//        if (slaveIds == null || slaveIds.length == 0) {
//            if (!slaveUpgrade.isAllSlaveIds()) {
//                return;
//            }
//
//
//        }
//
//        long contextTime = ContextUtils.getContextTime();
//        long beginTime = slaveUpgrade.getBeginTime();
//        String id = app + "@" + slaveUpgrade.getGroup();
//
//        TaskService.ME.addPanel(id, "slaveUpgrade", beginTime, (beginTime < contextTime ? contextTime : beginTime) + 3600000, 3, slaveUpgrade);
//        if (slaveUpgrade.isAllSlaveIds()) {
//
//
//        } else {
//
//        }
    }

    @JaTask("slaveUpgrade")
    public void doSlaveUpgrade(JSlaveUpgrade slaveUpgrade) {

    }

}
