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
import com.absir.core.util.UtilSchelduer.NextRunable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UtilSchelduer<T extends NextRunable> extends Thread {

    protected List<T> addRunables;

    protected UtilNode<T> runableHeader = new UtilNode<T>();

    protected UtilNode<T> runableFooter = runableHeader;

    protected boolean starting;

    protected long nextRunableTime;

    public synchronized void addRunables(T runable) {
        if (addRunables == null) {
            addRunables = new ArrayList<T>();
        }

        addRunables.add(runable);
        runable.start(UtilContext.getCurrentDate());
        if (runable.getNextTime() < nextRunableTime) {
            interrupt();
        }
    }

    public synchronized void removeRunables(T runable) {
        if (addRunables != null) {
            addRunables.remove(runable);
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
            UtilNode<T> node = runableHeader.getNext();
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
                        removeRunableNode(node);

                    } else {
                        sortNextRunableNode(node);
                    }

                } else {
                    break;
                }

                node = nodeNext;
            }

            if (addRunables != null) {
                List<T> adds = addRunables;
                synchronized (this) {
                    addRunables = null;
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

                    addNextRunableNode(add);
                }
            }

            node = runableHeader.getNext();
            if (node == null) {
                nextRunableTime = time + getMaxSleepTime();

            } else {
                nextRunableTime = node.getElement().getNextTime();
            }

            if (nextRunableTime > time) {
                try {
                    Thread.sleep(nextRunableTime - time);

                } catch (InterruptedException e) {
                    continue;
                }
            }
        }

    }

    protected void logThrowable(Throwable e) {
        if (Environment.getEnvironment() == Environment.DEVELOP) {
            e.printStackTrace();
        }
    }

    protected long getMaxSleepTime() {
        return 10000;
    }

    protected void computeFooter() {
        UtilNode<T> node = runableFooter.previous == null ? runableHeader : runableFooter;
        while (node.next != null) {
            node = node.next;
        }

        runableFooter = node;
    }

    protected void sortAllRunables() {
        UtilNode.sortOrderableNodeAll(runableHeader);
        computeFooter();
    }

    protected void computeAddFooter() {
        UtilNode<T> node = runableFooter.next;
        if (node != null) {
            runableFooter = node;
        }
    }

    protected void removeRunableNode(UtilNode<T> runableNode) {
        if (runableNode == runableFooter) {
            runableFooter = runableFooter.previous;
        }

        runableNode.remove();
    }

    protected void sortNextRunableNode(UtilNode<T> runableNode) {
        UtilNode.sortOrderableNode(runableNode);
        computeAddFooter();
    }

    protected void addNextRunableNode(T runableNode) {
        UtilNode.insertOrderableNodeFooter(runableFooter, runableNode);
        computeAddFooter();
    }

    public interface NextRunable extends Orderable {

        public void start(Date date);

        public long getNextTime();

        public void run(Date date);
    }

    public static abstract class NextRunableDelay implements NextRunable {

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
