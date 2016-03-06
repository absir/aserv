/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年10月5日 上午10:20:12
 */
package com.absir.core.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelList.Orderable;
import com.absir.core.util.UtilSchelduer.NextRunable;

/**
 * @author absir
 *
 */
public class UtilSchelduer<T extends NextRunable> extends Thread {

	/** addRunables */
	protected List<T> addRunables;

	/** runableHeader */
	protected UtilNode<T> runableHeader = new UtilNode<T>();

	/** runableFooter */
	protected UtilNode<T> runableFooter = runableHeader;

	/** starting */
	protected boolean starting;

	/** nextRunableTime */
	protected long nextRunableTime;

	/**
	 * @author absir
	 *
	 */
	public interface NextRunable extends Orderable {

		/**
		 * @param date
		 */
		public void start(Date date);

		/**
		 * @return
		 */
		public long getNextTime();

		/**
		 * @param date
		 */
		public void run(Date date);
	}

	/**
	 * @author absir
	 *
	 */
	public static abstract class NextRunableDelay implements NextRunable {

		/** nextTime */
		protected long nextTime;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
		 */
		@Override
		public int getOrder() {
			return (int) (getNextTime() >> 10);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.core.util.UtilSchelduer.NextRunable#start(java.util.Date)
		 */
		@Override
		public void start(Date date) {
			nextTime += date.getTime();
		}

		/**
		 * @return the nextTime
		 */
		public long getNextTime() {
			return nextTime;
		}

		/**
		 * @param nextTime
		 *            the nextTime to set
		 */
		public void setNextTime(long nextTime) {
			this.nextTime = nextTime;
		}
	}

	/**
	 * @param runable
	 */
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

	/**
	 * @param runable
	 */
	public synchronized void removeRunables(T runable) {
		if (addRunables != null) {
			addRunables.remove(runable);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#start()
	 */
	@Override
	public synchronized void start() {
		if (starting) {
			return;
		}

		starting = true;
		super.start();
	}

	/**
	 * 
	 */
	public synchronized void stopNow() {
		starting = false;
		interrupt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
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

	/**
	 * @param e
	 */
	protected void logThrowable(Throwable e) {
		if (Environment.getEnvironment() == Environment.DEVELOP) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	protected long getMaxSleepTime() {
		return 10000;
	}

	/**
	 * 
	 */
	protected void computeFooter() {
		UtilNode<T> node = runableFooter.previous == null ? runableHeader : runableFooter;
		while (node.next != null) {
			node = node.next;
		}

		runableFooter = node;
	}

	/**
	 * 
	 */
	protected void sortAllRunables() {
		UtilNode.sortOrderableNodeAll(runableHeader);
		computeFooter();
	}

	/**
	 * 
	 */
	protected void computeAddFooter() {
		UtilNode<T> node = runableFooter.next;
		if (node != null) {
			runableFooter = node;
		}
	}

	/**
	 * @param runableNode
	 */
	protected void removeRunableNode(UtilNode<T> runableNode) {
		if (runableNode == runableFooter) {
			runableFooter = runableFooter.previous;
		}

		runableNode.remove();
	}

	/**
	 * @param runableNode
	 */
	protected void sortNextRunableNode(UtilNode<T> runableNode) {
		UtilNode.sortOrderableNode(runableNode);
		computeAddFooter();
	}

	/**
	 * @param runableElement
	 */
	protected void addNextRunableNode(T runableNode) {
		UtilNode.insertOrderableNodeFooter(runableFooter, runableNode);
		computeAddFooter();
	}
}
