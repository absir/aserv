package com.absir.aserv.system.service;

import com.absir.aserv.init.InitBeanFactory;
import com.absir.aserv.system.helper.HelperString;
import com.absir.bean.basis.Base;
import com.absir.bean.basis.BeanConfig;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.InjectOrder;
import com.absir.bean.inject.value.Started;
import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperFileName;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by absir on 2017/1/4.
 */
@Base
@Bean
public class MaintenanceService {

    public interface IMaintenance {
    }

    public static final MaintenanceService ME = BeanFactoryUtils.get(MaintenanceService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceService.class);

    protected String[] maintenanceChmodXs;

    protected void chmodX(String filePath) throws IOException {
        if (maintenanceChmodXs != null) {
            int length = maintenanceChmodXs.length;
            String[] commands = new String[length + 1];
            for (int i = 0; i < length; i++) {
                commands[i] = maintenanceChmodXs[i];
            }

            commands[length] = filePath;
            HelperIO.executeArray(commands);
        }
    }

    @InjectOrder(-1)
    @Started
    protected void postConstruct() throws IOException {
        BeanConfig beanConfig = BeanFactoryUtils.getBeanConfig();
        String maintenanceChmodX = beanConfig.getExpressionObject("maintenance.chmod.x", null, String.class);
        if (maintenanceChmodX == null && HelperFileName.SYSTEM_SEPARATOR != HelperFileName.WINDOWS_SEPARATOR) {
            maintenanceChmodX = "chmod -R 755";
        }

        if (!KernelString.isEmpty(maintenanceChmodX)) {
            maintenanceChmodXs = HelperString.split(maintenanceChmodX);
        }

        if (InitBeanFactory.ME.isDevelopOrVersionChange()) {
            String maintenancePath = HelperFileName.normalizeNoEndSeparator(beanConfig.getResourcePath() + "protected/maintenance");
            File maintenanceFile = new File(maintenancePath);
            List<Class<?>> maintenanceClasses = new ArrayList<Class<?>>();
            maintenanceClasses.add(MaintenanceService.class);
            for (IMaintenance maintenance : BeanFactoryUtils.getOrderBeanObjects(IMaintenance.class)) {
                maintenanceClasses.add(maintenance.getClass());
            }

            for (Class<?> maintenanceClass : maintenanceClasses) {
                try {
                    HelperFile.copyDirectoryOverWrite(maintenanceClass.getResource("/maintenance"), maintenanceFile, false, null, true);

                } catch (Throwable e) {
                    LOGGER.error("deploy maintenanceClass " + maintenanceClass, e);
                }
            }

            // war deploy maintenance
            File classMaintenanceFile = new File(beanConfig.getClassPath() + "maintenance");
            if (classMaintenanceFile.exists() && classMaintenanceFile.isDirectory()) {
                HelperFile.copyDirectoryOverWrite(classMaintenanceFile, maintenanceFile, true, null, true);
            }

            chmodX(maintenancePath + "/scripts");
        }
    }

}
