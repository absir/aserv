/**
 * Copyright 2015 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2015年10月5日 下午8:43:45
 */
package com.absir.core.util;

import com.absir.core.base.Environment;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @author absir
 */
public class UtilContext {

    /**
     * currentDate
     */
    protected static Date currentDate;
    /**
     * currentTime
     */
    protected static long currentTime;
    /**
     * currentShort
     */
    protected static int currentShort;
    /**
     * currentCalendar
     */
    protected static Calendar currentCalendar = computeCalendar();
    /**
     * idleThread
     */
    protected static Thread idleThread;
    /**
     * warnIdlePool
     */
    protected static boolean warnIdlePool;
    /**
     * minIdleCount
     */
    protected static int minIdlePool = 2;
    /**
     * threadPoolExecutor
     */
    private static ThreadPoolExecutor threadPoolExecutor;
    /**
     * rejectedThreadPoolExecutor
     */
    private static ThreadPoolExecutor rejectedThreadPoolExecutor;
    /**
     * rejectedExecutionHandler
     */
    private static RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            warnIdlePool = true;
            if (r instanceof RunableGuaranted) {
                getRejectThreadPoolExecutor().execute(r);
                return;
            }

            throw new RejectedExecutionException();
        }
    };

    /**
     * @return
     */
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
            /*
             * (non-Javadoc)
             *
             * @see java.lang.Thread#run()
             */
            @Override
            public void run() {
                while (Environment.isStarted()) {
                    runnable.run();
                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        continue;
                    }
                }
            }
        };

        runnable.run();
        thread.setName("UtilSchelduer.computeCalendar");
        thread.setDaemon(true);
        thread.start();
        return currentCalendar;
    }

    /**
     * @return the currentCalendar
     */
    public final static Calendar getCurrentCalendar() {
        return currentCalendar;
    }

    /**
     * @return the currentDate
     */
    public final static Date getCurrentDate() {
        return currentDate;
    }

    /**
     * @return the currentTime
     */
    public final static long getCurrentTime() {
        return currentTime;
    }

    /**
     * @return the currentShort
     */
    public final static int getCurrentShort() {
        return currentShort;
    }

    /**
     * @return the warnIdlePool
     */
    public static final boolean isWarnIdlePool() {
        return warnIdlePool;
    }

    /**
     * @return the minIdlePool
     */
    public static int getMinIdlePool() {
        return minIdlePool;
    }

    /**
     * @param minIdlePool the minIdlePool to set
     */
    public static void setMinIdlePool(int minIdlePool) {
        UtilContext.minIdlePool = minIdlePool;
    }

    /**
     * @return the threadPoolExecutor
     */
    public static ThreadPoolExecutor getThreadPoolExecutor() {
        if (threadPoolExecutor == null) {
            setThreadPoolExecutor(new ThreadPoolExecutor(32, 128, 90000, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(32), rejectedExecutionHandler));
        }

        return threadPoolExecutor;
    }

    /**
     * @param threadPoolExecutor the threadPoolExecutor to set
     */
    public static synchronized void setThreadPoolExecutor(ThreadPoolExecutor poolExecutor) {
        if (threadPoolExecutor != null) {
            throw new RuntimeException("threadPoolExecutor exist");
        }

        if (idleThread == null) {
            idleThread = new Thread() {
                /*
                 * (non-Javadoc)
                 *
                 * @see java.lang.Thread#run()
                 */
                @Override
                public void run() {
                    while (Environment.isStarted()) {
                        if (warnIdlePool) {
                            warnIdlePool = minIdlePool <= 0 ? false
                                    : rejectedThreadPoolExecutor.getActiveCount() > 0 || (threadPoolExecutor.getMaximumPoolSize()
                                    - threadPoolExecutor.getActiveCount()) < minIdlePool;
                        }

                        try {
                            Thread.sleep(30000);

                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }

                ;
            };

            idleThread.setName("UtilSchelduer.idleThread");
            idleThread.setDaemon(true);
            idleThread.start();
        }

        poolExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
        threadPoolExecutor = poolExecutor;
    }

    /**
     * @param command
     */
    public static final void executeSecurity(Runnable command) {
        try {
            threadPoolExecutor.execute(command);

        } catch (Throwable e) {
        }
    }

    /**
     * @return the threadPoolExecutor
     */
    protected static ThreadPoolExecutor getRejectThreadPoolExecutor() {
        if (rejectedThreadPoolExecutor == null) {
            setRejectThreadPoolExecutor(
                    new ThreadPoolExecutor(8, 64, 90000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()));
        }

        return rejectedThreadPoolExecutor;
    }

    /**
     * @param threadPoolExecutor the threadPoolExecutor to set
     */
    public static synchronized void setRejectThreadPoolExecutor(ThreadPoolExecutor poolExecutor) {
        if (rejectedThreadPoolExecutor != null) {
            throw new RuntimeException("rejectedThreadPoolExecutor exist");
        }

        rejectedThreadPoolExecutor = poolExecutor;
    }

    /**
     *
     */
    public static void stop() {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdownNow();
        }

        if (rejectedThreadPoolExecutor != null) {
            rejectedThreadPoolExecutor.shutdownNow();
        }
    }

    /**
     * @author absir
     */
    public static interface RunableGuaranted extends Runnable {
    }
}
