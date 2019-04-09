/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-26 下午4:39:08
 */
package com.absir.async;

import com.absir.aop.AopInterceptor;
import com.absir.aop.AopProxyHandler;
import com.absir.context.core.ContextUtils;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
public class AsyncRunnableNotifier extends AsyncRunnable {

    public static final Runnable _NOTIFIER_RUNNABLE = new Runnable() {
        @Override
        public void run() {

        }
    };

    private boolean notifying;

    private NotifierIterator notifierIterator;

    public AsyncRunnableNotifier(long timeout, boolean thread) {
        super(timeout, thread);
    }

    public static void notifierProxyRun(final INotifierProxy proxy, final Runnable runnable) {
        synchronized (proxy) {
            Runnable _runnable = proxy.getNotifierRunnable();
            if (_runnable == null) {
                proxy.setNotifierRunnable(_NOTIFIER_RUNNABLE);
                try {
                    ContextUtils.getThreadPoolExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            Runnable _runnable = runnable;
                            while (true) {
                                if (runnable != null) {
                                    try {
                                        runnable.run();

                                    } catch (Throwable e) {
                                        LOGGER.error("notifierProxyRun run error: " + proxy, e);
                                    }
                                }

                                synchronized (proxy) {
                                    _runnable = proxy.getNotifierRunnable();
                                    if (_runnable == null || _runnable == _NOTIFIER_RUNNABLE) {
                                        proxy.setNotifierRunnable(null);
                                        return;
                                    }

                                    proxy.setNotifierRunnable(_NOTIFIER_RUNNABLE);
                                }
                            }
                        }
                    });

                    _runnable = _NOTIFIER_RUNNABLE;

                } finally {
                    if (_runnable == null) {
                        proxy.setNotifierRunnable(null);
                    }
                }

            } else {
                proxy.setNotifierRunnable(runnable);
            }
        }
    }

    public Runnable notifierRunnable(final Object proxy, final Iterator<AopInterceptor> iterator,
                                     final AopProxyHandler proxyHandler, final Method method, final Object[] args, final MethodProxy methodProxy) {
        return new Runnable() {

            @Override
            public void run() {
                try {
                    synchronized (AsyncRunnableNotifier.this) {
                        if (notifying) {
                            if (notifierIterator == null) {
                                notifierIterator = new NotifierIterator();
                            }

                            notifierIterator.proxy = proxy;
                            notifierIterator.iterator = iterator;
                            notifierIterator.proxyHandler = proxyHandler;
                            notifierIterator.method = method;
                            notifierIterator.args = args;
                            notifierIterator.methodProxy = methodProxy;
                            return;
                        }

                        notifying = true;
                    }

                    proxyHandler.invoke(proxy, iterator, method, args, methodProxy);

                } catch (Throwable e) {
                    LOGGER.error("async notifier run", e);

                } finally {
                    checkNotifierIterator();
                }
            }
        };
    }

    protected void checkNotifierIterator() {
        NotifierIterator iterator = null;
        synchronized (this) {
            notifying = false;
            if (notifierIterator == null) {
                return;

            } else {
                iterator = notifierIterator;
            }
        }

        try {
            aysncRun(notifierRunnable(iterator.proxy, iterator.iterator, iterator.proxyHandler, iterator.method, iterator.args,
                    iterator.methodProxy));

        } catch (Throwable e) {
            checkNotifierIterator();
            LOGGER.error("async notifier run", e);
        }
    }

    @Override
    public void aysnc(Object proxy, Iterator<AopInterceptor> iterator, AopProxyHandler proxyHandler, Method method, Object[] args,
                      MethodProxy methodProxy) throws Throwable {
        try {
            aysncRun(notifierRunnable(proxy, iterator, proxyHandler, method, args, methodProxy));

        } catch (Throwable e) {
            checkNotifierIterator();
            LOGGER.error("async notifier run", e);
        }
    }

    public interface INotifierProxy {

        public Runnable getNotifierRunnable();

        public void setNotifierRunnable(Runnable runnable);

    }

    private static class NotifierIterator {

        private Object proxy;

        private Iterator<AopInterceptor> iterator;

        private AopProxyHandler proxyHandler;

        private Method method;

        private Object[] args;

        private MethodProxy methodProxy;
    }

}
