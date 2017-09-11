/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月5日 下午8:43:45
 */
package com.absir.core.util;

import com.absir.core.base.Environment;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.*;

public class UtilContext {

    protected static Date currentDate;

    protected static long currentTime;

    protected static int currentShort;

    protected static int startYear;

    protected static Calendar currentCalendar = computeCalendar();

    protected static Thread idleThread;

    protected static boolean warnIdlePool;

    protected static int minIdlePool = 2;

    protected static long usableMemory = Runtime.getRuntime().freeMemory();

    private static ThreadPoolExecutor threadPoolExecutor;

    private static ThreadPoolExecutor rejectedThreadPoolExecutor;

    private static RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            warnIdlePool = true;
            if (r instanceof RunnableGuarantee) {
                getRejectThreadPoolExecutor().execute(r);
                return;
            }

            throw new RejectedExecutionException();
        }
    };

    public static final int shortTimeFor(long time) {
        return (int) (time / 1000);
    }

    protected static Calendar computeCalendar() {
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    currentCalendar = Calendar.getInstance();
                    currentDate = currentCalendar.getTime();
                    currentTime = currentCalendar.getTimeInMillis();

                } catch (Throwable e) {
                    currentTime = System.currentTimeMillis();
                }

                currentShort = (int) (currentTime / 1000);
            }
        };

        Thread thread = new Thread() {

            @Override
            public void run() {
                while (Environment.isStarted()) {
                    try {
                        runnable.run();

                    } catch (Throwable e) {
                        Environment.throwable(e);
                    }

                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };

        runnable.run();
        thread.setName("UtilScheduler.computeCalendar");
        thread.setDaemon(true);
        thread.start();
        startYear = currentCalendar.get(Calendar.YEAR);
        return currentCalendar;
    }

    public static void interruptAllThread() {
//        if (!Environment.isActive() && idleThread != null) {
//            ThreadGroup group = idleThread.getThreadGroup();
//            int estimatedSize = group.activeCount() * 2;
//            Thread[] slackList = new Thread[estimatedSize];
//            int actualSize = group.enumerate(slackList);
//            Thread[] list = new Thread[actualSize];
//            System.arraycopy(slackList, 0, list, 0, actualSize);
//            for (Thread thread : list) {
//                try {
//                    thread.interrupt();
//
//                } catch (Throwable e) {
//                    Environment.throwable(e);
//                }
//            }
//        }
    }

    public final static Calendar getCurrentCalendar() {
        return currentCalendar;
    }

    public final static Date getCurrentDate() {
        return currentDate;
    }

    public final static long getCurrentTime() {
        return currentTime;
    }

    public final static int getStartYear() {
        return startYear;
    }

    public final static int getCurrentShort() {
        return currentShort;
    }

    public static final boolean isWarnIdlePool() {
        return warnIdlePool;
    }

    public static int getMinIdlePool() {
        return minIdlePool;
    }

    public static void setMinIdlePool(int minIdlePool) {
        UtilContext.minIdlePool = minIdlePool;
    }

    public static long getUsableMemory() {
        return usableMemory;
    }

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        if (threadPoolExecutor == null) {
            setThreadPoolExecutor(new ThreadPoolExecutor(32, 128, 90000, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(32), rejectedExecutionHandler));
        }

        return threadPoolExecutor;
    }

    public static synchronized void setThreadPoolExecutor(ThreadPoolExecutor poolExecutor) {
        if (threadPoolExecutor != null) {
            throw new RuntimeException("threadPoolExecutor exist");
        }

        if (idleThread == null) {
            idleThread = new Thread() {

                @Override
                public void run() {
                    while (Environment.isStarted()) {
                        try {
                            if (warnIdlePool) {
                                warnIdlePool = minIdlePool <= 0 ? false
                                        : rejectedThreadPoolExecutor.getActiveCount() > 0 || (threadPoolExecutor.getMaximumPoolSize()
                                        - threadPoolExecutor.getActiveCount()) < minIdlePool;

                                Runtime runtime = Runtime.getRuntime();
                                usableMemory = runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory();
                            }

                        } catch (Throwable e) {
                            Environment.throwable(e);
                        }

                        try {
                            Thread.sleep(10000);

                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }

            };

            idleThread.setName("UtilScheduler.idleThread");
            idleThread.setDaemon(true);
            idleThread.start();
        }

        poolExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
        threadPoolExecutor = poolExecutor;
    }

    public static final void executeSecurity(Runnable command) {
        try {
            threadPoolExecutor.execute(command);

        } catch (Throwable e) {
            Environment.throwable(e);
        }
    }

    protected static ThreadPoolExecutor getRejectThreadPoolExecutor() {
        if (rejectedThreadPoolExecutor == null) {
            setRejectThreadPoolExecutor(
                    new ThreadPoolExecutor(8, 64, 90000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()));
        }

        return rejectedThreadPoolExecutor;
    }

    public static synchronized void setRejectThreadPoolExecutor(ThreadPoolExecutor poolExecutor) {
        if (rejectedThreadPoolExecutor != null) {
            throw new RuntimeException("rejectedThreadPoolExecutor exist");
        }

        rejectedThreadPoolExecutor = poolExecutor;
    }

    public static void stop() {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdownNow();
        }

        if (rejectedThreadPoolExecutor != null) {
            rejectedThreadPoolExecutor.shutdownNow();
        }
    }

    public static interface RunnableGuarantee extends Runnable {
    }
}
