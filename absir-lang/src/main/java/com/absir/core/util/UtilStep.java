/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年10月26日 上午11:10:30
 */
package com.absir.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.absir.core.base.Environment;

/**
 * @author absir
 *
 */
public class UtilStep extends Thread {

	/** status */
	private int status;

	/** sleepTime */
	private long sleepTime;

	/** steps */
	private List<IStep> steps = new LinkedList<UtilStep.IStep>();

	/** addSteps */
	private List<IStep> addSteps;

	/**
	 * @author absir
	 *
	 */
	public interface IStep {

		/**
		 * @param contextTime
		 * @return
		 */
		public boolean stepDone(long contextTime);

	}

	/**
	 * @param daemon
	 * @param name
	 * @param sleep
	 * @return
	 */
	public static UtilStep openUtilStep(boolean daemon, String name, long sleep) {
		return openUtilStep(daemon, name, false, sleep);
	}

	/**
	 * @param daemon
	 * @param name
	 * @param closed
	 * @param sleep
	 * @return
	 */
	public static UtilStep openUtilStep(boolean daemon, String name, boolean closed, long sleep) {
		UtilStep utilStep = new UtilStep(closed, sleep);
		utilStep.setDaemon(daemon);
		utilStep.setName(name);
		utilStep.start();
		return utilStep;
	}

	/**
	 * @param closed
	 * @param sleep
	 */
	public UtilStep(boolean closed, long sleep) {
		status = closed ? 0 : 1;
		if (sleep < 1000) {
			sleep = 1000;
		}

		sleepTime = sleep;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#start()
	 */
	@Override
	public synchronized void start() {
		if (status < 2) {
			status += 2;
			super.start();
		}
	}

	/**
	 * 
	 */
	public synchronized void close() {
		if (status == 2) {
			status = 0;
		}
	}

	/**
	 * @param step
	 */
	public synchronized void addStep(IStep step) {
		if (addSteps == null) {
			addSteps = new ArrayList<IStep>();
		}

		addSteps.add(step);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (Environment.isStarted() && status > 1) {
			try {
				Thread.sleep(sleepTime);

			} catch (Throwable e) {
				break;
			}

			long contextTime = getContextTime();
			Iterator<IStep> iterator = steps.iterator();
			while (iterator.hasNext()) {
				try {
					if (iterator.next().stepDone(contextTime)) {
						iterator.remove();
					}

				} catch (Throwable e) {
					logThrowable(e);
				}
			}

			if (addSteps != null) {
				synchronized (this) {
					steps.addAll(addSteps);
					addSteps = null;
				}
			}
		}

		status = status == 2 ? 0 : 1;
	}

	/**
	 * @return
	 */
	protected long getContextTime() {
		return UtilContext.getCurrentTime();
	}

	/**
	 * @param e
	 */
	protected void logThrowable(Throwable e) {
		if (Environment.getEnvironment() == Environment.DEVELOP) {
			e.printStackTrace();
		}
	}

}
