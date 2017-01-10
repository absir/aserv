package com.absir.aserv.slave.handler;

import com.absir.aserv.task.JaTask;
import com.absir.aserv.task.TaskService;
import com.absir.aserv.upgrade.UpgradeService;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Started;
import com.absir.bean.inject.value.Value;
import com.absir.client.helper.HelperEncrypt;
import com.absir.client.rpc.RpcData;
import com.absir.context.core.ContextUtils;
import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperFileName;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilInputStream;
import com.absir.server.handler.HandlerType;
import com.absir.server.handler.IHandler;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteAdapter;
import com.absir.server.value.Handler;
import com.absir.shared.bean.EUpgradeStatus;
import com.absir.shared.bean.SlaveUpgrade;
import com.absir.shared.master.IMaster;
import com.absir.shared.slave.ISlave;
import com.absir.slave.InputSlave;
import com.absir.slave.InputSlaveContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by absir on 2016/10/27.
 */
@Base
@Bean
@Handler
public class SlaveHandler implements IHandler, ISlave {

    public static final SlaveHandler ME = BeanFactoryUtils.get(SlaveHandler.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(SlaveHandler.class);

    private IMaster master;

    @Value("slave.upgrade.passTime")
    protected long passTime = 3600000;

    @Value("slave.upgrade.retryCount")
    protected int retryCount = 3;

    @Value("slave.upgrade.maxCount")
    private int maxCount = 10;

    public IMaster getMaster() {
        return master;
    }

    @Started
    protected void started() {
        master = InputSlaveContext.ME.getSlaveAdapter().getRpcInvoker(IMaster.class);
        master.upgradeStatues(EUpgradeStatus.RESTART_COMPLETE, null, false);
    }

    @Override
    public boolean _permission(OnPut onPut) {
        return InputSlave.onAuthentication(onPut.getInput());
    }

    @Override
    public void _finally(OnPut onPut, HandlerType.HandlerMethod method) {
    }

    @Override
    public long time() {
        return System.currentTimeMillis();
    }

    @Override
    public RpcData upgrade(SlaveUpgrade slaveUpgrade) {
        if (slaveUpgrade == null || slaveUpgrade.getActionTime() > RouteAdapter.ADAPTER_TIME) {
            try {
                if (slaveUpgrade == null) {
                    TaskService.ME.removePanel("slaveUpgrade");
                    slaveUpgrade(slaveUpgrade, false);

                } else {
                    if (slaveUpgrade.getBeginTime() > ContextUtils.getContextTime()) {
                        slaveUpgrade(slaveUpgrade, false);
                        TaskService.ME.addPanel(null, "slaveUpgrade", slaveUpgrade.getBeginTime(), slaveUpgrade.getBeginTime() + passTime, retryCount, slaveUpgrade, true);
                    }
                }

            } catch (IOException e) {
            }
        }

        return null;
    }

    protected class SlaveUpgradeThread extends Thread {

        protected SlaveUpgrade slaveUpgrade;

        protected boolean doUpgrade;

        @Override
        public void run() {
            int i = 0;
            while (!Thread.interrupted()) {
                File upgradeFile = null;
                File resourceFile = null;
                String downloadName = null;
                try {
                    String slaveUpgradeDir = BeanFactoryUtils.getBeanConfig().getResourcePath() + "protected/slaveUpgrade/";
                    File slaveUpgradeFile = new File(slaveUpgradeDir);
                    if (slaveUpgradeFile.exists()) {
                        if (slaveUpgradeFile.isDirectory()) {
                            HelperFile.releaseCacheFiles(slaveUpgradeFile.listFiles(), maxCount);

                        } else {
                            slaveUpgradeFile.delete();
                        }
                    }

                    if (!KernelString.isEmpty(slaveUpgrade.getUpgradeFile())) {
                        upgradeFile = new File(slaveUpgradeDir + HelperFileName.getName(slaveUpgrade.getUpgradeFile()));
                        if (!upgradeFile.exists()) {
                            downloadName = upgradeFile.getName();
                            master.upgradeStatues(EUpgradeStatus.DOWNLOADING, downloadName, false);
                            HelperFile.write(upgradeFile, new UtilInputStream.ThreadInputStream(master.download(slaveUpgrade.getUpgradeFile())));
                            master.upgradeStatues(EUpgradeStatus.DOWNLOAD_COMPLETE, downloadName, false);
                            downloadName = null;
                        }
                    }

                    if (!KernelString.isEmpty(slaveUpgrade.getResourceFile())) {
                        resourceFile = new File(slaveUpgradeDir + HelperFileName.getName(slaveUpgrade.getResourceFile()));
                        if (!resourceFile.exists()) {
                            downloadName = resourceFile.getName();
                            master.upgradeStatues(EUpgradeStatus.DOWNLOADING, downloadName, false);
                            HelperFile.write(resourceFile, new UtilInputStream.ThreadInputStream(master.download(slaveUpgrade.getResourceFile())));
                            master.upgradeStatues(EUpgradeStatus.DOWNLOAD_COMPLETE, downloadName, false);
                            downloadName = null;
                        }
                    }

                    synchronized (SlaveUpgradeThread.class) {
                        if (slaveUpgradeThread.doUpgrade) {
                            slaveUpgradeThread = null;
                            ME.doSlaveUpgrade(slaveUpgrade, upgradeFile, resourceFile);
                            break;
                        }
                    }

                } catch (Throwable e) {
                    LOGGER.error("SlaveUpgradeThread slaveUpgrade[" + i + "] stop", e);
                    if (upgradeFile != null) {
                        upgradeFile.delete();
                    }

                    if (resourceFile != null) {
                        resourceFile.delete();
                    }
                }

                if (downloadName != null) {
                    master.upgradeStatues(EUpgradeStatus.DOWNLOADING, downloadName, true);
                }

                if (++i >= retryCount) {
                    break;
                }
            }

            synchronized (SlaveUpgradeThread.class) {
                slaveUpgradeThread = null;
            }
        }

        protected boolean equalsSlaveUpgrade(SlaveUpgrade upgrade) {
            return KernelObject.equals(upgrade.getResourceFile(), slaveUpgrade.getResourceFile()) && KernelObject.equals(upgrade.getUpgradeFile(), slaveUpgrade.getUpgradeFile());
        }
    }

    protected SlaveUpgradeThread slaveUpgradeThread;

    @JaTask("slaveUpgrade")
    protected void slaveUpgrade(SlaveUpgrade slaveUpgrade, boolean doUpgrade) throws IOException {
        if (slaveUpgrade != null && slaveUpgrade.getActionTime() <= RouteAdapter.ADAPTER_TIME) {
            return;
        }

        synchronized (SlaveUpgradeThread.class) {
            if (slaveUpgrade == null) {
                if (slaveUpgradeThread != null) {
                    slaveUpgradeThread.interrupt();
                    slaveUpgradeThread = null;
                }

            } else {
                if (slaveUpgradeThread != null) {
                    if (slaveUpgradeThread.equalsSlaveUpgrade(slaveUpgrade)) {
                        slaveUpgradeThread.slaveUpgrade = slaveUpgrade;
                        slaveUpgradeThread.doUpgrade = doUpgrade;
                        return;
                    }

                    slaveUpgradeThread.interrupt();
                    slaveUpgradeThread = null;
                }

                slaveUpgradeThread = new SlaveUpgradeThread();
                slaveUpgradeThread.slaveUpgrade = slaveUpgrade;
                slaveUpgradeThread.doUpgrade = doUpgrade;
                slaveUpgradeThread.setDaemon(true);
                slaveUpgradeThread.setName("slave UpgradeThread");
                slaveUpgradeThread.start();
            }
        }
    }

    protected void doSlaveUpgrade(SlaveUpgrade slaveUpgrade, File upgradeFile, File resourceFile) throws IOException {
        boolean restart = slaveUpgrade.isForceRestart();
        String validateName = null;
        try {
            if (upgradeFile != null) {
                validateName = upgradeFile.getName();
                master.upgradeStatues(EUpgradeStatus.FILE_VALIDATE, validateName, false);
                if (!HelperEncrypt.encryptionMD5(new FileInputStream(upgradeFile)).equals(slaveUpgrade.getUpgradeMd5())) {
                    throw new IOException("upgradeFile md5 not match");
                }

                validateName = null;
                restart = true;
            }

            if (resourceFile != null) {
                validateName = resourceFile.getName();
                master.upgradeStatues(EUpgradeStatus.FILE_VALIDATE, validateName, false);
                if (!HelperEncrypt.encryptionMD5(new FileInputStream(resourceFile)).equals(slaveUpgrade.getResourceMd5())) {
                    throw new IOException("resourceFile md5 not match");
                }

                validateName = null;
                master.upgradeStatues(EUpgradeStatus.RESOURCE_READY, resourceFile.getName(), false);
                doSlaveUpgradeResource(slaveUpgrade, resourceFile, restart);
                master.upgradeStatues(EUpgradeStatus.RESOURCE_COMPLETE, resourceFile.getName(), false);
            }

        } finally {
            if (validateName != null) {
                master.upgradeStatues(EUpgradeStatus.FILE_VALIDATE, validateName, true);
            }
        }

        if (restart) {
            master.upgradeStatues(EUpgradeStatus.RESTART_READY, null, false);
            doSlaveUpgradeRestart(slaveUpgrade);
            master.upgradeStatues(EUpgradeStatus.RESTART_BEGIN, null, false);
            if (upgradeFile == null) {
                UpgradeService.ME.restart();

            } else {
                UpgradeService.ME.restartUpgrade(upgradeFile);
            }
        }
    }

    protected void doSlaveUpgradeResource(SlaveUpgrade slaveUpgrade, File resourceFile, boolean restart) {
    }

    protected void doSlaveUpgradeRestart(SlaveUpgrade slaveUpgrade) {
    }

}
