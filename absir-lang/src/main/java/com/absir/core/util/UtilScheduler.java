/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月5日 上午10:20:12
 */
package com.absir.core.util;

import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelList.Orderable;
import com.absir.core.util.UtilScheduler.NextRunnable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UtilScheduler<T extends NextRunnable> extends Thread {

    protected List<T> addRunnables;

    protected UtilNode<T> runnableHeader = new UtilNode<T>();

    protected UtilNode<T> runnableFooter = runnableHeader;

    protected boolean starting;

    protected long nextRunnableTime;

    public synchronized void addRunnable(T runnable) {
        if (addRunnables == null) {
            addRunnables = new ArrayList<T>();
        }

        addRunnables.add(runnable);
        runnable.start(UtilContext.getCurrentDate());
        if (runnable.getNextTime() < nextRunnableTime) {
            interrupt();
        }
    }

    public synchronized void removeRunnable(T runnable) {
        if (addRunnables != null) {
            addRunnables.remove(runnable);
        }
    }

    @Override
    public synchronized void start() {
        if (starting) {
            return;
        }

        starting = true;
        super.start();
    }

    public synchronized void stopNow() {
        starting = false;
        interrupt();
    }

    @Override
    public void run() {
        while (Environment.isActive() && starting) {
            long time = UtilContext.getCurrentTime();
            Date date = UtilContext.getCurrentDate();
            UtilNode<T> node = runnableHeader.getNext();
            UtilNode<T> nodeNext = null;
            T runable;
            while (node != null) {
                nodeNext = node.getNext();
                runable = node.getElement();
                if (runable.getNextTime() <= time) {
                    try {
                        runable.run(date);

                    } catch (Throwable e) {
                        logThrowable(e);
                    }

                    if (runable.getNextTime() <= time) {
                        removeRunnableNode(node);

                    } else {
                        sortNextRunnableNode(node);
                    }

                } else {
                    break;
                }

                node = nodeNext;
            }

            if (addRunnables != null) {
                List<T> adds = addRunnables;
                synchronized (this) {
                    addRunnables = null;
                }

                for (T add : adds) {
                    add.start(date);
                    if (add.getNextTime() <= time) {
                        try {
                            add.run(date);

                        } catch (Throwable e) {
                            logThrowable(e);
                        }

                        if (add.getNextTime() <= time) {
                            continue;
                        }
                    }

                    addNextRunnableNode(add);
                }
            }

            node = runnableHeader.getNext();
            if (node == null) {
                nextRunnableTime = time + getMaxSleepTime();

            } else {
                nextRunnableTime = node.getElement().getNextTime();
            }

            if (nextRunnableTime > time) {
                try {
                    Thread.sleep(nextRunnableTime - time);

                } catch (InterruptedException e) {
                    continue;
                }
            }
        }

    }

    protected void logThrowable(Throwable e) {
        Environment.throwable(e);
    }

    protected long getMaxSleepTime() {
        return 10000;
    }

    protected void computeFooter() {
        UtilNode<T> node = runnableFooter.previous == null ? runnableHeader : runnableFooter;
        while (node.next != null) {
            node = node.next;
        }

        runnableFooter = node;
    }

    protected void sortAllRunnables() {
        UtilNode.sortOrderableNodeAll(runnableHeader);
        computeFooter();
    }

    protected void computeAddFooter() {
        UtilNode<T> node = runnableFooter.next;
        if (node != null) {
            runnableFooter = node;
        }
    }

    protected void removeRunnableNode(UtilNode<T> runableNode) {
        if (runableNode == runnableFooter) {
            runnableFooter = runnableFooter.previous;
        }

        runableNode.remove();
    }

    protected void sortNextRunnableNode(UtilNode<T> runableNode) {
        UtilNode.sortOrderableNode(runableNode);
        computeAddFooter();
    }

    protected void addNextRunnableNode(T runableNode) {
        UtilNode.insertOrderableNodeFooter(runnableFooter, runableNode);
        computeAddFooter();
    }

    public interface NextRunnable extends Orderable {

        public void start(Date date);

        public long getNextTime();

        public void run(Date date);
    }

    public static abstract class NextRunnableDelay implements NextRunnable {

        protected long nextTime;

        @Override
        public int getOrder() {
            return (int) (getNextTime() >> 10);
        }

        @Override
        public void start(Date date) {
            nextTime += date.getTime();
        }

        public long getNextTime() {
            return nextTime;
        }

        public void setNextTime(long nextTime) {
            this.nextTime = nextTime;
        }
    }
}
