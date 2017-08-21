/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月29日 下午9:48:04
 */
package com.absir.context.config;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.config.IBeanFactoryStopping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BeanFactoryStopping {

    public static final List<BeanFactoryStopping> FACTORY_STOPPINGS = new ArrayList<BeanFactoryStopping>();

    protected static final Logger LOGGER = LoggerFactory.getLogger(BeanFactoryStopping.class);

    protected BeanFactory beanFactory;

    protected List<IBeanFactoryStopping> beanFactoryStoppings = new ArrayList<IBeanFactoryStopping>();

    public static void addStopping(BeanFactory beanFactory, List<IBeanFactoryStopping> stoppings) {
        BeanFactoryStopping beanFactoryStopping = new BeanFactoryStopping();
        beanFactoryStopping.beanFactory = beanFactory;
        beanFactoryStopping.beanFactoryStoppings = stoppings;
        FACTORY_STOPPINGS.add(beanFactoryStopping);
    }

    private static boolean addShutdown;

    private static boolean stopping = false;

    public static void addShutdownHook() {
        synchronized (BeanFactoryStopping.class) {
            if (addShutdown) {
                return;
            }

            addShutdown = true;
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                stoppingAll();
            }
        });
    }

    public static void stoppingAll() {
        synchronized (BeanFactoryStopping.class) {
            if (stopping) {
                return;
            }

            stopping = true;
        }

        if (!FACTORY_STOPPINGS.isEmpty()) {
            for (BeanFactoryStopping FACTORY_STOPPING : FACTORY_STOPPINGS) {
                BeanFactory beanFactory = FACTORY_STOPPING.beanFactory;
                List<IBeanFactoryStopping> stoppings = FACTORY_STOPPING.beanFactoryStoppings;
                LOGGER.info("stopping [" + (stoppings == null ? 0 : stoppings.size()) + "]");
                for (IBeanFactoryStopping stopping : stoppings) {
                    try {
                        LOGGER.info("stopping " + stopping);
                        stopping.stopping(beanFactory);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            FACTORY_STOPPINGS.clear();
            LOGGER.info("stopping complete");
        }
    }
}
