/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月4日 下午6:51:24
 */
package com.absir.aserv.upgrade;

import com.absir.aserv.init.InitBeanFactory;
import com.absir.aserv.system.helper.HelperRandom;
import com.absir.aserv.task.JaTask;
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Value;
import com.absir.bean.lang.LangCodeUtils;
import com.absir.context.config.BeanFactoryStopping;
import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperFileName;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.server.route.RouteAdapter;
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

    public static final String NO_APP_CODE = LangCodeUtils.get("应用不存在", UpgradeService.class);
    public static final String NOT_VALIDATOR = LangCodeUtils.get("升级文件验证失败", UpgradeService.class);
    public static final String IO_EXCEPTION = LangCodeUtils.get("文件异常", UpgradeService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(UpgradeService.class);
    protected static final String incrementalUpgrade = "incrementalUpgrade";
    @Value(value = "upgrade.config")
    private String upgradeConfig = "WEB-INF/classes/config.properties";
    @Value(value = "upgrade.destination", defaultValue = "${classPath}../../")
    private String upgradeDestination;
    @Value(value = "upgrade.restart")
    private String restartCommand;

    @Inject
    protected void initService() {
        upgradeDestination = HelperFileName.normalize(upgradeDestination);
    }

    public String getUpgradeDestination() {
        return upgradeDestination;
    }

    public String getRestartCommand() {
        return restartCommand;
    }

    public void restartCommand() throws IOException {
        if (!KernelString.isEmpty(restartCommand)) {
            HelperIO.execute(restartCommand);
        }
    }

    public void start(Object stopDone) throws IOException {
        BeanFactoryStopping.stoppingAll();
        restartCommand();
    }

    public Object stop() throws IOException {
        return null;
    }

    @Async(notifier = true, thread = true)
    public void restart() throws IOException {
        Object stopDone = stop();
        start(stopDone);
    }

    /**
     * 获取版本信息
     */
    public Map<String, Object> getVersionMap(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(file);
            ZipEntry zipEntry = zipFile.getEntry(upgradeConfig);
            if (zipEntry != null) {
                Map<String, Object> versionMap = new HashMap<String, Object>();
                BeanConfigImpl.readProperties(null, versionMap, zipFile.getInputStream(zipEntry), null);
                versionMap.put(incrementalUpgrade, zipFile.getEntry(incrementalUpgrade) != null ? Boolean.TRUE : Boolean.FALSE);
                return versionMap;
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

    public String getAppCode(Map<String, Object> configMap) {
        return BeanConfigImpl.getMapValue(configMap, "app", null, String.class);
    }

    public boolean validateAppCode(Map<String, Object> configMap, String appCode) {
        String app = configMap == null ? null : getAppCode(configMap);
        return KernelObject.equals(app, appCode);
    }

    public String getVersion(Map<String, Object> configMap) {
        return BeanConfigImpl.getMapValue(configMap, "version", null, String.class);
    }

    public String getVersionName(Map<String, Object> configMap) {
        return BeanConfigImpl.getMapValue(configMap, "version.name", null, String.class);
    }

    /**
     * 升级删除文件
     */
    protected void upgradeRM(Map<String, Object> configMap) {
        String version = getVersion(configMap);
        if (version != null) {
            InitBeanFactory.upgradeRM(InitBeanFactory.ME.getVersion(), version);
        }
    }

    protected boolean upgrade(File upgradeFile) throws IOException {
        Map<String, Object> configMap = getVersionMap(upgradeFile);
        if (configMap != null && validateAppCode(configMap, InitBeanFactory.ME.getAppCode())) {
            if (configMap.get(incrementalUpgrade) != Boolean.TRUE) {
                //不是增量更新
                String classPath = BeanFactoryUtils.getBeanConfig().getClassPath();
                HelperFile.deleteFileNoBreak(new File(classPath), null);
                HelperFile.deleteFileNoBreak(new File(classPath + "../lib"), null);
            }

            HelperFile.copyDirectoryOverWrite(new ZipInputStream(new FileInputStream(upgradeFile)), new File(upgradeDestination), true, null, true);
            upgradeRM(configMap);
            String value = BeanConfigImpl.getMapValue(configMap, "upgrade.restart", null, String.class);
            restartCommand = BeanFactoryUtils.getBeanConfig().getExpression(value);
        }

        LOGGER.warn("upgrade.fail => " + upgradeFile);
        return false;
    }

    @Async(notifier = true, thread = true)
    public void restartUpgrade(File file) throws IOException {
        Object stopDone = stop();
        upgrade(file);
        start(stopDone);
    }

    @Async(notifier = true, thread = true)
    public void restartUpgrade(InputStream inputStream) throws IOException {
        File upgradeFile = new File(HelperFileName.normalize(BeanFactoryUtils.getBeanConfig().getClassPath() + "../upgrade/"
                + HelperRandom.randSecondId() + ".zip"));
        Object stopDone = null;
        try {
            HelperFile.write(upgradeFile, inputStream);
            stopDone = stop();
            upgrade(upgradeFile);

        } finally {
            HelperFile.deleteQuietly(upgradeFile);
        }

        start(stopDone);
    }

    @JaTask("upgradeFile")
    public void upgradeFile(long adapterTime, String filePath) {
        if (RouteAdapter.ADAPTER_TIME == adapterTime) {
            try {
                UpgradeService.ME.restartUpgrade(new File(filePath));

            } catch (Exception e) {
                LOGGER.error("upgradeFile error " + filePath, e);
            }
        }
    }

    public interface IUpgradeReStart {

        public void start() throws Throwable;

        public void stop() throws Throwable;

    }

}
