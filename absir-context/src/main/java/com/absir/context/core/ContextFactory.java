/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-14 下午4:12:13
 */
package com.absir.context.core;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.*;
import com.absir.context.bean.IContext;
import com.absir.core.kernel.KernelClass;
import com.absir.core.util.UtilAbsir;
import com.absir.core.util.UtilAtom;
import com.absir.core.util.UtilContext;
import com.absir.core.util.UtilContext.RunnableGuarantee;
import com.absir.core.util.UtilDump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

@SuppressWarnings({"rawtypes", "unchecked"})
@Base
@Bean
public class ContextFactory {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ContextFactory.class);

    private long contextTime = forContextTime();

    private int shortTime = (int) (contextTime / 1000);

    private Queue<ContextBase> contextBases = new ConcurrentLinkedQueue<ContextBase>();

    private Map<Object, Object> tokenMap = new HashMap<Object, Object>();

    private Map<Class<?>, Map<Serializable, Context>> classMapIdMapContext = new HashMap<Class<?>, Map<Serializable, Context>>();

    private Queue<ContextBean> contextBeans = new ConcurrentLinkedQueue<ContextBean>();

    private ThreadPoolExecutor threadPoolExecutor;

    @Value("context.maxThread")
    private int maxThread = 128;

    @Value("context.delay")
    private int delay = 1000;

    @Value("context.stopDelay")
    private int stopDelay = 1000;

    @Value("context.unInitCount")
    private int unInitCount = 3;

    @Value("context.removeIdleTime")
    private long removeIdleTime = 60000;

    @Value("context.minUsableMemory")
    private long minUsableMemory = 4096000;

    private Timer contextTimer = new Timer("ContextFactory.ContextTimer", true);

    private TimerTask contextTimerTask = new TimerTask() {

        @Override
        public void run() {
            contextTime = forContextTime();
            Iterator<ContextBase> contextBaseIterator = contextBases.iterator();
            while (contextBaseIterator.hasNext()) {
                final ContextBase contextBase = contextBaseIterator.next();
                try {
                    if (contextBase.isExpiration() || contextBase.stepDone(contextTime)) {
                        contextBaseIterator.remove();
                        if (!contextBase.unInitializeDone()) {
                            threadPoolExecutor.execute(new RunnableGuarantee() {

                                @Override
                                public void run() {
                                    for (int i = 0; i < unInitCount; i++) {
                                        try {
                                            contextBase.unInitialize();
                                            break;

                                        } catch (Throwable e) {
                                            LOGGER.error("stepDone " + contextBase, e);
                                        }
                                    }
                                }
                            });
                        }
                    }

                } catch (Throwable e) {
                    LOGGER.error("contextBase error " + contextBase, e);
                }
            }

            long minIdleTime = 0;
            if (UtilContext.getUsableMemory() < minUsableMemory) {
                minIdleTime = contextTime - removeIdleTime * UtilContext.getUsableMemory() / minUsableMemory;
            }

            Iterator<ContextBean> contextBeanIterator = contextBeans.iterator();
            while (contextBeanIterator.hasNext()) {
                final ContextBean contextBean = contextBeanIterator.next();
                try {
                    if (contextBean.isExpiration() || contextBean.stepDone(contextTime) || (minIdleTime > 0 && contextBean.retainAt >= 0 && contextBean.retainAt < minIdleTime)) {
                        contextBeanIterator.remove();
                        contextBean.setExpiration();
                        final Map<Serializable, Context> contextMap = classMapIdMapContext.get(contextBean.getContextClass());
                        if (contextBean.unInitializeDone()) {
                            if (contextMap != null) {
                                synchronized (contextMap) {
                                    if (contextBean.isExpiration()) {
                                        contextMap.remove(contextBean.getId());
                                        continue;
                                    }

                                    contextBeans.add(contextBean);
                                }
                            }

                        } else {
                            threadPoolExecutor.execute(new RunnableGuarantee() {

                                @Override
                                public void run() {
                                    for (int i = 0; i < unInitCount; i++) {
                                        try {
                                            contextBean.unInitialize();
                                            if (contextMap != null) {
                                                synchronized (contextMap) {
                                                    if (contextBean.isExpiration()) {
                                                        contextMap.remove(contextBean.getId());
                                                        return;
                                                    }
                                                }

                                                contextBeans.add(contextBean);
                                                break;
                                            }

                                        } catch (Throwable e) {
                                            LOGGER.error("stepDone " + contextBean + " => " + contextBean.getId(), e);
                                        }
                                    }
                                }
                            });
                        }
                    }

                } catch (Throwable e) {
                    LOGGER.error("contextBean error " + contextBean, e);
                }
            }
        }
    };

    public static UtilAtom getUtilAtom(int maxThread) {
        return maxThread <= 0 ? new UtilAtom() : new ContextAtom(maxThread);
    }

    protected long forContextTime() {
        return System.currentTimeMillis();
    }

    public long getContextTime() {
        return contextTime;
    }

    public int getShortTime() {
        return shortTime;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public int getUnInitCount() {
        return unInitCount;
    }

    @Started
    private void scanner() {
        List<IContext> contexts = BeanFactoryUtils.get().getBeanObjects(IContext.class);
        for (IContext context : contexts) {
            contextBases.add(context instanceof ContextBase ? (ContextBase) context : new ContextWrapper(context));
        }
    }

    public void addContext(ContextBase context) {
        context.retainAt(contextTime);
        contextBases.add(context);
    }

    public void removeContext(ContextBase context) {
        context.setExpiration();
    }

    @Inject
    @InjectOrder(-255)
    protected void injectExecutor(@Value(value = "context.corePoolSize", defaultValue = "256") int corePoolSize,
                                  @Value(value = "context.maximumPoolSize", defaultValue = "1024") int maximumPoolSize,
                                  @Value(value = "context.keepAliveTime", defaultValue = "90000") int keepAliveTime,
                                  @Value(value = "context.minIdlePool", defaultValue = "32") int minIdlePool) {
        setThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime);
        UtilContext.setMinIdlePool(minIdlePool);
    }

    protected void setThreadPoolExecutor(int corePoolSize, int maximumPoolSize, int keepAliveTime) {
        // 请求处理线程池
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(corePoolSize));

        //监听调试异常线程池
//        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
//                new ArrayBlockingQueue<Runnable>(corePoolSize)) {
//            @Override
//            public void execute(final Runnable command) {
//                final UtilDump.TimeoutException exception = UtilDump.addTimeoutException(30000);
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            command.run();
//
//                        } finally {
//                            exception.complete();
//                        }
//                    }
//                };
//
//                boolean start = false;
//                try {
//                    super.execute(runnable);
//                    start = true;
//
//                } finally {
//                    if (!start) {
//                        exception.fail();
//                    }
//                }
//            }
//        };

        UtilContext.setThreadPoolExecutor(threadPoolExecutor);
    }

    @Inject
    @InjectOrder(-255)
    protected void injectRejectExecutor(@Value(value = "context.rejectPoolSize", defaultValue = "16") int corePoolSize,
                                        @Value(value = "context.rejectMaximumPoolSize", defaultValue = "32") int maximumPoolSize,
                                        @Value(value = "context.rejectKeepAliveTime", defaultValue = "90000") int keepAliveTime) {
        UtilContext.setRejectThreadPoolExecutor(new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()));
    }

    public Object getToken(String tokenId) {
        return UtilAbsir.getToken(tokenId, tokenMap);
    }

    public void clearToken(String tokenId) {
        UtilAbsir.clearToken(tokenId, tokenMap);
    }

    public Map<Serializable, Context> getContextMap(Class<?> cls) {
        Map<Serializable, Context> contextMap = classMapIdMapContext.get(cls);
        if (contextMap == null) {
            synchronized (cls) {
                contextMap = classMapIdMapContext.get(cls);
                if (contextMap == null) {
                    contextMap = new ConcurrentHashMap<Serializable, Context>();
                    classMapIdMapContext.put(cls, contextMap);
                }
            }
        }

        return contextMap;
    }

    public List<Class> getContextClasses() {
        return new ArrayList<Class>(classMapIdMapContext.keySet());
    }

    public Map<Serializable, Context> findContextMap(Class<?> cls) {
        return classMapIdMapContext.get(cls);
    }

    public <T extends Context<ID>, ID extends Serializable> T getContext(Class<T> ctxClass, ID id, Class<?> cls,
                                                                         boolean concurrent) {
        return getContext(getContextMap(cls), ctxClass, id, cls, concurrent);
    }

    private <T extends Context<ID>, ID extends Serializable> T getContext(Map<Serializable, Context> contextMap,
                                                                          Class<T> ctxClass, ID id, Class<?> cls, boolean concurrent) {
        Context context = contextMap.get(id);
        if (context == null) {
            try {
                String tokenId = concurrent ? UtilAbsir.getId(cls, id) : null;
                synchronized (concurrent ? getToken(tokenId) : contextMap) {
                    try {
                        context = contextMap.get(id);
                        if (context == null) {
                            context = KernelClass.newInstance(ctxClass);
                            context.setId(id);
                            context.initialize();
                            if (concurrent) {
                                Context initialized = null;
                                synchronized (contextMap) {
                                    initialized = contextMap.get(id);
                                    if (initialized == null) {
                                        contextMap.put(id, context);

                                    } else if (initialized instanceof IContext) {
                                        ((IContext) initialized).retainAt(contextTime);
                                    }
                                }

                                if (initialized != null) {
                                    return (T) initialized;
                                }

                            } else {
                                contextMap.put(id, context);
                            }

                            if (context instanceof ContextBean) {
                                ((ContextBean) context).retainAt(contextTime);
                                if (ctxClass != cls && context instanceof ContextBeanO) {
                                    ((ContextBeanO) context).contextClass = cls;
                                }

                                contextBeans.add((ContextBean) context);
                                return (T) context;
                            }
                        }

                    } finally {
                        if (concurrent) {
                            clearToken(tokenId);
                        }
                    }
                }

            } catch (Exception e) {
                LOGGER.error("getContext " + ctxClass + " => " + id, e);
            }
        }

        if (context instanceof IContext) {
            ((IContext) context).retainAt(contextTime);
        }

        return (T) context;
    }

    public void clearContext(Context context, Class cls, boolean concurrent) {
        if (context instanceof ContextBean) {
            ((ContextBean) context).setExpiration();

        } else {
            Map<Serializable, Context> contextMap = classMapIdMapContext.get(cls);
            if (contextMap != null) {
                synchronized (concurrent ? UtilAbsir.getToken(cls, context.getId(), contextMap) : contextMap) {
                    context.unInitialize();
                    contextMap.remove(context.getId());
                }

            } else {
                context.unInitialize();
            }
        }
    }

    @InjectOrder(value = -1024)
    @Started
    private void start() {
        contextTimer.schedule(contextTimerTask, 0, delay);
    }

    @InjectOrder(value = 1024)
    @Stopping
    private void stop() {
        LOGGER.info("stop begin");
        contextTimerTask.cancel();
        contextTimer.cancel();
        final UtilAtom utilAtom = getUtilAtom(maxThread * 10);
        utilAtom.increment();
        Queue<ContextBase> contextBases = this.contextBases;
        this.contextBases = new ConcurrentLinkedQueue<ContextBase>();
        for (final ContextBase contextBase : contextBases) {
            if (contextBase.unInitializeDone()) {
                continue;
            }

            utilAtom.increment();
            threadPoolExecutor.execute(new RunnableGuarantee() {

                @Override
                public void run() {
                    try {
                        for (int i = 0; i < unInitCount; i++) {
                            try {
                                contextBase.unInitialize();
                                break;

                            } catch (Exception e) {
                                LOGGER.error("stop " + contextBase, e);
                            }
                        }

                    } finally {
                        utilAtom.decrement();
                    }
                }
            });
        }

        Map<Class<?>, Map<Serializable, Context>> classMapIdMapContext = this.classMapIdMapContext;
        this.classMapIdMapContext = new HashMap<Class<?>, Map<Serializable, Context>>();
        for (Entry<Class<?>, Map<Serializable, Context>> entry : classMapIdMapContext.entrySet()) {
            for (Entry<Serializable, Context> contextEntry : entry.getValue().entrySet()) {
                final Context context = contextEntry.getValue();
                if (context.unInitializeDone()) {
                    continue;
                }

                utilAtom.increment();
                threadPoolExecutor.execute(new RunnableGuarantee() {

                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < unInitCount; i++) {
                                try {
                                    context.unInitialize();
                                    break;

                                } catch (Exception e) {
                                    LOGGER.error("stop " + context + " => " + context.getId(), e);
                                }
                            }

                        } finally {
                            utilAtom.decrement();
                        }
                    }
                });

            }
        }

        utilAtom.decrement();
        LOGGER.info("stop await");
        utilAtom.await();
        LOGGER.info("stop complete");
        threadPoolExecutor.shutdownNow();

        try {
            Thread.sleep(stopDelay);

        } catch (Throwable e) {
        }

        UtilContext.stop();
        LOGGER.info("stop await count [" + threadPoolExecutor.getActiveCount() + "]");
        try {
            UtilDump.dumpThreadPoolExecutorError(threadPoolExecutor);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
