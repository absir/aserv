/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-20 下午1:39:32
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.core.kernel.KernelClass;

import java.util.ArrayList;
import java.util.List;

public abstract class BeanDefineAbstractor extends BeanDefineAbstract {

    public static boolean processDelay;

    public static List<Runnable> processRunnables;

    private boolean loaded;

    public static void loadInterfaces(Class<?> type) {
        while (type != null && type != Object.class) {
            for (Class<?> iCls : type.getInterfaces()) {
                KernelClass.forName(iCls.getName());
            }

            type = type.getSuperclass();
        }
    }

    /**
     * 开启延时处理对象
     *
     * @return
     */
    public static boolean openProccessDelay() {
        if (!processDelay) {
            synchronized (BeanDefineAbstractor.class) {
                processDelay = true;
                if (processRunnables == null) {
                    processRunnables = new ArrayList<Runnable>();
                }

                return true;
            }
        }

        return false;
    }

    /**
     * 移除延时处理对象
     */
    public static void removeProccessDelay() {
        processDelay = false;
    }

    /**
     * 清算延时处理对象
     */
    public static void clearProccessDelay() {
        if (processRunnables != null) {
            synchronized (BeanDefineAbstractor.class) {
                if (processRunnables != null) {
                    List<Runnable> runnables = processRunnables;
                    processDelay = false;
                    processRunnables = null;
                    int last = runnables.size();
                    while (true) {
                        if (last > 0) {
                            last--;
                            runnables.get(last).run();

                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }

    public static Object getBeanObject(final BeanFactory beanFactory, BeanDefine beanDefine, BeanDefine beanDefineRoot,
                                       BeanDefine beanDefineWrapper) {
        final Object beanObject = beanDefine.getBeanObject(beanFactory);
        if (beanDefine instanceof BeanDefineAbstractor && ((BeanDefineAbstractor) beanDefine).loaded) {
            BeanDefine beanDefineLoaded = beanFactory.getBeanDefine(beanDefineRoot.getBeanName());
            if (beanDefineLoaded != null && beanDefineLoaded != beanDefineRoot) {
                return beanDefineLoaded.getBeanObject(beanFactory);
            }
        }

        final BeanScope beanScope = beanDefineRoot.getBeanScope();
        Object beanProxy = beanObject;
        if (beanDefineRoot != null) {
            beanProxy = beanDefineRoot.getBeanProxy(beanProxy, beanDefineRoot, beanFactory);
        }

        beanDefine = getBeanDefine(beanDefineRoot.getBeanType(), beanDefineRoot.getBeanName(), beanProxy, beanScope,
                beanDefine);
        if (beanDefineWrapper != null && beanDefineWrapper instanceof BeanDefineWrapper) {
            ((BeanDefineWrapper) beanDefineWrapper).beanDefine = beanDefine;
        }

        if (beanScope == BeanScope.PROTOTYPE) {
            beanDefineWrapper = beanDefine;
            if (beanDefineRoot != null && beanDefineRoot instanceof BeanDefineWrapper) {
                beanDefine = ((BeanDefineWrapper) beanDefineRoot).retrenchBeanDefine();
            }
        }

        if (beanDefine != beanDefineWrapper) {
            BeanDefine registeredBeanDefine = beanFactory.getBeanDefine(beanDefineWrapper.getBeanName());
            if (registeredBeanDefine != null
                    && BeanFactoryImpl.containBeanDefine(registeredBeanDefine, beanDefineWrapper)) {
                BeanFactoryImpl.getBeanFactoryImpl(beanFactory).replaceRegisteredBeanDefine(beanDefine);
            }
        }

        if (processDelay) {
            // 延迟处理对象
            synchronized (BeanDefineAbstractor.class) {
                if (processDelay) {
                    final BeanDefine root = beanDefineRoot;
                    final Object proxy = beanProxy;
                    processRunnables.add(new Runnable() {

                        @Override
                        public void run() {
                            beanFactory.processBeanObject(beanScope, root, beanObject, proxy);
                        }
                    });

                    return beanProxy;
                }
            }

        } else {
            clearProccessDelay();
        }

        beanFactory.processBeanObject(beanScope, beanDefineRoot, beanObject, beanProxy);
        return beanProxy;
    }

    public abstract void preloadBeanDefine();

    @Override
    public Object getBeanObject(BeanFactory beanFactory, BeanDefine beanDefineRoot, BeanDefine beanDefineWrapper) {
        if (loaded) {
            BeanDefine beanDefineLoaded = beanFactory.getBeanDefine(beanDefineRoot.getBeanName());
            if (beanDefineLoaded != null && beanDefineLoaded != beanDefineRoot) {
                return beanDefineLoaded.getBeanObject(beanFactory);
            }

        } else {
            if (beanDefineRoot.getBeanScope() != BeanScope.PROTOTYPE) {
                loaded = true;
                preloadBeanDefine();
                BeanDefine beanDefineLoaded = beanFactory.getBeanDefine(beanDefineRoot.getBeanName());
                if (beanDefineLoaded != null && beanDefineLoaded != beanDefineRoot) {
                    return beanDefineLoaded.getBeanObject(beanFactory);
                }
            }
        }

        return getBeanObject(beanFactory, this, beanDefineRoot, beanDefineWrapper);
    }
}
