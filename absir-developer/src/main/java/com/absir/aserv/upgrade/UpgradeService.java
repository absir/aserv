/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月4日 下午6:51:24
 */
package com.absir.aserv.upgrade;

import com.absir.aserv.init.InitBeanFactory;
import com.absir.aserv.system.bean.JConfigure;
import com.absir.aserv.system.bean.JEmbedSS;
import com.absir.aserv.system.bean.JVersion;
import com.absir.aserv.system.helper.HelperRandom;
import com.absir.aserv.system.service.BeanService;
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Value;
import com.absir.client.helper.HelperEncrypt;
import com.absir.context.config.BeanFactoryStopping;
import com.absir.context.schedule.value.Schedule;
import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperFileName;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelString;
import com.absir.core.kernel.KernelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Base
@Bean
public class UpgradeService {

    public static final UpgradeService ME = BeanFactoryUtils.get(UpgradeService.class);

    public static final IUpgradeReStart START_DONE = new IUpgradeReStart() {

        @Override
        public void start() throws Throwable {
        }

        @Override
        public void stop() throws Throwable {
        }
    };

    protected static final Logger LOGGER = LoggerFactory.getLogger(UpgradeService.class);

    @Value(value = "upgrade.resource", defaultValue = "${resourcePath}upgrade/")
    private String upgradeResource;

    @Value(value = "upgrade.destination", defaultValue = "${classPath}../../")
    private String upgradeDestination;

    @Value(value = "upgrade.restart")
    private String restartCommand;

    @Value(value = "upgrade.backup")
    private String backupCommand;

    public String getUpgradeResource() {
        return upgradeResource;
    }

    public String getUpgradeDestination() {
        return upgradeDestination;
    }

    /**
     * 获取版本信息
     *
     * @param updateZip
     * @return
     */
    public Map<String, Object> getVersionMap(File updateZip) {
        if (updateZip.exists() || updateZip.isDirectory()) {
            return null;
        }

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(updateZip);
            ZipEntry zipEntry = zipFile.getEntry("version.properties");
            if (zipEntry != null) {
                Map<String, Object> versionMap = new HashMap<String, Object>();
                BeanConfigImpl.readProperties(null, versionMap, zipFile.getInputStream(zipEntry), null);
                Object appCode = versionMap.get("appCode");
                if (appCode != null && appCode.equals(InitBeanFactory.ME.getAppCode())) {
                    return versionMap;
                }
            }

        } catch (Exception e) {

        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();

                } catch (IOException e) {
                }
            }
        }

        return null;
    }

    /**
     * 添加版本文件
     *
     * @param version
     * @param upgradeFile
     * @return
     */
    public JVersion addVersion(JVersion version, File upgradeFile) {
        Map<String, Object> versionMap = getVersionMap(upgradeFile);
        if (versionMap != null) {
            long time = System.currentTimeMillis();
            String versionFile = HelperRandom.randSecendId(time, 8, upgradeFile.hashCode());
            try {
                HelperFile.copyFile(upgradeFile, new File(versionFile));
                version.setVersion(KernelDyna.to(versionMap.get("version"), String.class));
                version.setVersionName(KernelDyna.to(versionMap.get("versionName"), String.class));
                version.setCreateTime(time);
                version.setVersionFile(versionFile);
                BeanService.ME.persist(version);
                return version;

            } catch (Exception e) {
                HelperFile.deleteQuietly(new File(versionFile));
            }
        }

        return null;
    }

    public JVersion upgradeVersion() {
        try {
            File upgradeFile = new File(BeanFactoryUtils.getBeanConfig().getClassPath() + "upgrade.zip");
            if (upgradeFile.exists() && !upgradeFile.isDirectory()) {
                JEmbedSS embedSS = new JEmbedSS();
                embedSS.setEid(InitBeanFactory.ME.getAppCode());
                embedSS.setMid("upgradeHash");
                JConfigure configure = BeanService.ME.get(JConfigure.class, embedSS);
                String hash = configure == null ? null : configure.getValue();
                if (hash == null || !hash.equals(HelperEncrypt.encryptionMD5(new FileInputStream(upgradeFile)))) {
                    return upgradeVersion(null, upgradeFile);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public JVersion upgradeVersion(JVersion version) {
        if (KernelUtil.compareVersion(version.getVersion(), InitBeanFactory.ME.getVersionRevert()) >= 0) {
            try {
                ME.restartUpgrade(version.getVersionFile());
                return version;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public JVersion upgradeVersion(JVersion version, File upgradeFile) {
        version = addVersion(version == null ? new JVersion() : version, upgradeFile);
        if (version != null) {
            return upgradeVersion(version);
        }

        return null;
    }

    @Async(notifier = true, thread = true)
    public void restart() throws IOException {
        Object stopDone = stop();
        start(stopDone);
    }

    /**
     * 升级删除文件
     */
    protected void rmUpgrade() {
        Map<String, Object> configMap = new HashMap<String, Object>();
        BeanConfigImpl.readProperties(null, configMap,
                new File(BeanFactoryUtils.getBeanConfig().getClassPath() + "config.properties"), null);
        String version = BeanConfigImpl.getMapValue(configMap, "version", null, String.class);
        if (version != null) {
            InitBeanFactory.upgradeRM(InitBeanFactory.ME.getVersion(), version);
        }
    }

    @Async(notifier = true, thread = true)
    public void restartUpgrade(String versionFile) throws IOException {
        Object stopDone = stop();
        upgrade(new ZipInputStream(new FileInputStream(HelperFileName.concat(upgradeResource, versionFile))));
        rmUpgrade();
        start(stopDone);
    }

    @Async(notifier = true, thread = true)
    public void restartUpgrade(InputStream inputStream) throws IOException {
        Object stopDone = stop();
        File upgradeFile = new File(BeanFactoryUtils.getBeanConfig().getResourcePath() + "upgrade/"
                + HelperRandom.randSecendId() + ".upgrd");
        try {
            HelperFile.write(upgradeFile, inputStream);
            upgrade(new ZipInputStream(new FileInputStream(upgradeFile)));
            rmUpgrade();

        } finally {
            HelperFile.deleteQuietly(upgradeFile);
        }

        start(stopDone);
    }

    public String getRestartCommand() {
        return restartCommand;
    }

    public void start(Object stopDone) throws IOException {
        BeanFactoryStopping.stoppingAll();
        if (!KernelString.isEmpty(restartCommand)) {
            Runtime.getRuntime().exec(restartCommand);
        }
    }

    public Object stop() throws IOException {
        return null;
    }

    public void upgrade(ZipInputStream inputStream) throws IOException {
        HelperFile.copyDirectoryOverWrite(inputStream, new File(upgradeDestination), true, null, true);
    }

    @Async
    @Schedule(cron = "0 0 5 * * *")
    protected void upradeBackup() throws IOException {
        if (!KernelString.isEmpty(backupCommand)) {
            Runtime.getRuntime().exec(backupCommand);
        }
    }

    public interface IUpgradeReStart {

        public void start() throws Throwable;

        public void stop() throws Throwable;

    }
}
