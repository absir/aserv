/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年7月16日 上午11:10:34
 */
package com.absir.aserv.system.service.impl;

import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.task.JaTask;
import com.absir.aserv.upgrade.UpgradeService;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.server.route.RouteAdapter;
import com.absir.servlet.InDispathContext;

import java.io.IOException;

@Base
@Bean
public class UpgradeServiceImpl extends UpgradeService {

    @Override
    public Object stop() throws IOException {
        Object context = InDispathContext.getServletContext();
        if (context != null) {
            context = KernelObject.declaredGet(context, "context");
            if (context != null) {
                context = KernelObject.declaredGet(context, "context");
                // tomcat set lock false
                KernelObject.declaredSend(context, "setAntiJARLocking", false);
                KernelObject.declaredSend(context, "setAntiResourceLocking", false);
                KernelObject.declaredSend(context, "setReloadable", false);
                KernelObject.declaredSend(context, "setPrivileged", true);
                final Object ctx = context;
                return new IUpgradeReStart() {

                    @Override
                    public void start() throws Throwable {
                        KernelObject.declaredSend(ctx, "reload");
                    }

                    @Override
                    public void stop() throws Throwable {
                        KernelObject.declaredSend(ctx, "stop");
                    }
                };
            }
        }

        return super.stop();
    }

    @Override
    public void start(Object stopDone) throws IOException {
        super.start(stopDone);
        boolean noCommand = KernelString.isEmpty(getRestartCommand());
        while (true) {
            if (stopDone != null) {
                // tomcat reload
                if (noCommand) {
                    KernelObject.declaredSend(stopDone, "start");

                } else {
                    KernelObject.declaredSend(stopDone, "stop");
                }

                break;
            }

            Object context = InDispathContext.getServletContext();
            if (context != null) {
                context = KernelObject.declaredGet(context, "context");
                if (context != null) {
                    context = KernelObject.declaredGet(context, "context");
                    // tomcat reload
                    if (noCommand) {
                        KernelObject.declaredSend(context, "reload");

                    } else {
                        KernelObject.declaredSend(context, "stop");
                    }

                    break;
                }
            }

            break;
        }
    }

    @JaTask("upgradeFile")
    @Override
    public void upgradeFile(long adapterTime, String filePath) {
        if (RouteAdapter.ADAPTER_TIME == adapterTime) {
            try {
                UpgradeService.ME.restartUpgrade(UploadCrudFactory.ME.getProtectedStream(filePath));

            } catch (Exception e) {
                LOGGER.error("upgradeFile error " + filePath, e);
            }
        }
    }
}
