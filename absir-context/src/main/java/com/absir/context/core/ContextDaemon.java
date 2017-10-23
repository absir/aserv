package com.absir.context.core;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Value;
import com.absir.context.config.BeanFactoryStopping;
import com.absir.core.base.Environment;
import com.absir.core.helper.HelperFile;
import com.absir.core.util.UtilContext;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by absir on 2017/1/5.
 */
@Base
@Bean
public class ContextDaemon implements Runnable {

    public static final ContextDaemon ME = BeanFactoryUtils.get(ContextDaemon.class);

    protected String daemonDir;

    protected File shutdownFile;

    protected File stoppedFile;

    protected File developerFile;

    @Value("daemon.developer.timeout")
    protected long developerTimeout = 300000;

    @Value("daemon.idle.time")
    protected long idleTime = 2000;

    @Inject
    protected void injectShutDown() throws IOException {
        daemonDir = BeanFactoryUtils.getBeanConfig().getResourcePath() + "protected/daemon/";

        shutdownFile = new File(daemonDir + "shutdown");
        HelperFile.write(shutdownFile, "");
        if (shutdownFile.exists()) {
            shutdownFile.delete();
        }

        stoppedFile = new File(daemonDir + "stopped");
        if (stoppedFile.exists()) {
            stoppedFile.delete();
        }

        developerFile = new File(daemonDir + "developer");
        Thread thread = new Thread(ME);
        thread.setDaemon(true);
        thread.setName("ContextDaemon");
        thread.start();
    }

    public boolean isDeveloper(String daemon) {
        if (developerFile.exists() && developerFile.lastModified() + developerTimeout > UtilContext.getCurrentTime()) {
            try {
                if (HelperFile.readFileToString(developerFile).contains(daemon)) {
                    return true;
                }

            } catch (IOException e) {
            }
        }

        return false;
    }

    @Override
    public void run() {
        while (Environment.isActive()) {
            try {
                Thread.sleep(idleTime);
                if (shutdownFile.exists()) {
                    File stoppingFile = new File(daemonDir + "stopping");
                    stoppingFile.createNewFile();
                    Environment.setActive(false);
                    boolean done = false;
                    while (!done) {
                        try {
                            shutdown();
                            stoppedFile.createNewFile();
                            stoppingFile.delete();
                            done = true;
                            BeanFactoryStopping.stoppingAll();

                        } catch (Throwable e) {
                            Environment.throwable(e);
                        }
                    }
                }

            } catch (Throwable e) {
                Environment.throwable(e);
            }
        }
    }

    protected void shutdown() {
        ContextFactory contextFactory = ContextUtils.getContextFactory();
        for (Class cls : contextFactory.getContextClasses()) {
            Map<Serializable, Context> map = contextFactory.getContextMap(cls);
            Iterator<Map.Entry<Serializable, Context>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Context context = iterator.next().getValue();
                if (context != null && context.unInitializeDone()) {
                    context.unInitialize();
                }

                iterator.remove();
            }
        }
    }

}
