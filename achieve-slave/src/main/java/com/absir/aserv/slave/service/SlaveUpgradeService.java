/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月16日 下午3:19:39
 */
package com.absir.aserv.slave.service;

import com.absir.aserv.slave.bean.JSlaveUpgrading;
import com.absir.aserv.slave.bean.JSlaveUpgrading.EUpgradeStatus;
import com.absir.aserv.slave.bean.dto.DUpgradeSlave;
import com.absir.aserv.upgrade.UpgradeService;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Started;
import com.absir.bean.inject.value.Value;
import com.absir.client.helper.HelperClient;
import com.absir.client.helper.HelperEncrypt;
import com.absir.context.core.ContextUtils;
import com.absir.core.base.Environment;
import com.absir.core.helper.HelperFile;
import com.absir.core.kernel.KernelString;
import com.absir.slave.InputSlaveContext;
import com.absir.sockser.SockserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.util.zip.ZipInputStream;

@Base
@Bean
public class SlaveUpgradeService {

    public static final SlaveUpgradeService ME = BeanFactoryUtils.get(SlaveUpgradeService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(SlaveUpgradeService.class);

    @Value("slave.upgrade.max")
    protected int retryMax = 10;

    protected int retryCount;

    protected boolean downloaded;

    protected DUpgradeSlave upgradeSlave;

    protected JSlaveUpgrading slaveUpgrading = new JSlaveUpgrading();

    private boolean startUpgraded;

    @Started
    protected void started() {
        commitUpgradeStatus(EUpgradeStatus.START_UPRADE_COMPLETE, false);
    }

    /**
     * 开启升级线程
     */
    protected void startUpgraded() {
        if (startUpgraded) {
            return;
        }

        synchronized (this) {
            if (startUpgraded) {
                return;
            }

            ContextUtils.getThreadPoolExecutor().execute(new Runnable() {

                @Override
                public void run() {
                    while (Environment.isStarted()) {
                        DUpgradeSlave slave = upgradeSlave;
                        if (upgradeSlave != null) {
                            try {
                                if (!downloaded) {
                                    File upgradeFile = null;
                                    if (!KernelString.isEmpty(slave.upgradeFile)) {
                                        upgradeFile = new File(BeanFactoryUtils.getBeanConfig().getResourcePath()
                                                + "upgrade/upgrade.zip");
                                        if (!upgradeFile.exists()
                                                || !HelperEncrypt.encryptionMD5(HelperFile.openInputStream(upgradeFile))
                                                .equals(slave.upgradeMd5)) {
                                            HttpURLConnection connection = HelperClient
                                                    .openConnection(
                                                            InputSlaveContext.ME.getUrl() + "api/master/download/"
                                                                    + slave.upgradeFile + "?slvky="
                                                                    + InputSlaveContext.ME.getSlaveKey(),
                                                            false, null, 0, 0);
                                            HelperFile.write(upgradeFile, connection.getInputStream());
                                            if (!HelperEncrypt.encryptionMD5(HelperFile.openInputStream(upgradeFile))
                                                    .equals(slave.upgradeMd5)) {
                                                continue;
                                            }
                                        }
                                    }

                                    File resourceFile = null;
                                    if (!KernelString.isEmpty(slave.resourceFile)) {
                                        resourceFile = new File(BeanFactoryUtils.getBeanConfig().getResourcePath()
                                                + "upgrade/resource.zip");
                                        if (!resourceFile.exists() || !HelperEncrypt
                                                .encryptionMD5(HelperFile.openInputStream(resourceFile))
                                                .equals(slave.resourceMd5)) {
                                            HttpURLConnection connection = HelperClient
                                                    .openConnection(
                                                            InputSlaveContext.ME.getUrl() + "api/master/download/"
                                                                    + slave.resourceFile + "?slvky="
                                                                    + InputSlaveContext.ME.getSlaveKey(),
                                                            false, null, 0, 0);
                                            HelperFile.write(resourceFile, connection.getInputStream());
                                            if (!HelperEncrypt.encryptionMD5(HelperFile.openInputStream(resourceFile))
                                                    .equals(slave.resourceMd5)) {
                                                continue;
                                            }
                                        }
                                    }

                                    commitUpgradeStatus(EUpgradeStatus.DOWNLOADING_COMPLETE, false);
                                    downloaded = true;
                                }

                                if (slave.beginTime <= ContextUtils.getContextTime()) {
                                    File upgradeFile = null;
                                    if (!KernelString.isEmpty(slave.upgradeFile)) {
                                        upgradeFile = new File(BeanFactoryUtils.getBeanConfig().getResourcePath()
                                                + "upgrade/upgrade.zip");
                                        if (!upgradeFile.exists()
                                                || !HelperEncrypt.encryptionMD5(HelperFile.openInputStream(upgradeFile))
                                                .equals(slave.upgradeMd5)) {
                                            downloaded = false;
                                            continue;
                                        }
                                    }

                                    File resourceFile = null;
                                    if (!KernelString.isEmpty(slave.resourceFile)) {
                                        resourceFile = new File(BeanFactoryUtils.getBeanConfig().getResourcePath()
                                                + "upgrade/resource.zip");
                                        if (!resourceFile.exists() || !HelperEncrypt
                                                .encryptionMD5(HelperFile.openInputStream(resourceFile))
                                                .equals(slave.resourceMd5)) {
                                            commitUpgradeStatus(EUpgradeStatus.READY_DOWNLOADING, false);
                                            downloaded = false;
                                            continue;
                                        }
                                    }

                                    if (resourceFile != null) {
                                        ZipInputStream zipInputStream = new ZipInputStream(
                                                new FileInputStream(resourceFile));
                                        HelperFile.copyDirectoryOverWrite(zipInputStream,
                                                new File(BeanFactoryUtils.getBeanConfig().getResourcePath()), true,
                                                null, true);
                                    }

                                    if (upgradeFile != null) {
                                        commitUpgradeStatus(EUpgradeStatus.BEGIN_SAVEDATA, false);
                                        saveAll();
                                        if (upgradeSlave == null || !upgradeSlave.silent) {
                                            kickAll();
                                        }

                                        SockserService.ME.closeAllContext();
                                        saveAll();
                                        commitUpgradeStatus(EUpgradeStatus.BEGIN_RESTART_UPRADE, false);
                                        UpgradeService.ME.restartUpgrade(new FileInputStream(upgradeFile));
                                        return;
                                    }

                                    if (resourceFile != null) {
                                        reloadResoure();
                                    }

                                    upgradeSlave = null;
                                    commitUpgradeStatus(EUpgradeStatus.REFRESH_RESOUCE_COMPLETE, false);
                                }

                            } catch (Throwable e) {
                                LOGGER.error("upgradeSlave", e);
                                if (retryCount++ >= retryMax) {
                                    upgradeSlave = null;
                                    commitUpgradeStatus(slaveUpgrading.getUpgradeStatus(), true);
                                }
                            }
                        }

                        try {
                            Thread.sleep(10000);

                        } catch (InterruptedException e1) {
                            break;
                        }
                    }
                }
            });

            startUpgraded = true;
        }
    }

    public void addDUpgradeSlave(DUpgradeSlave slave) {
        commitUpgradeStatus(EUpgradeStatus.READY_DOWNLOADING, false);
        retryCount = 0;
        downloaded = false;
        upgradeSlave = slave;
        startUpgraded();
    }

    protected void commitUpgradeStatus(EUpgradeStatus status, boolean failed) {
        slaveUpgrading.setUpgradeStatus(status);
        slaveUpgrading.setFailed(failed);
        SlaverMasterService.ME.addMasterSynch("slaveUpgrading", "api/master/upgradeStatus", slaveUpgrading);
    }

    /**
     * 踢掉客户端
     */
    protected void kickAll() {
    }

    /**
     * 重载资源
     */
    protected void reloadResoure() {
    }

    /**
     * 保存全部数据
     */
    public void saveAll() {
        saveAll(ContextUtils.getContextFactory().getUninitCount());
    }

    /**
     * 保存全部数据
     *
     * @param unit
     */
    public void saveAll(int unit) {
    }
}
