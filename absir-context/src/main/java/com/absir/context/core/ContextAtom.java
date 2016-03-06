/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-15 上午10:05:23
 */
package com.absir.context.core;

import java.util.concurrent.locks.Condition;

import com.absir.core.base.Environment;
import com.absir.core.util.UtilAtom;

/**
 * @author absir
 * 
 */
public class ContextAtom extends UtilAtom {

	/** maxAtom */
	private int maxAtom;

	/** maxAwait */
	private boolean maxAwait;

	/** maxCondition */
	private Condition maxCondition = lock.newCondition();

	/**
	 * 
	 */
	public ContextAtom() {
		this(Integer.MAX_VALUE);
	}

	/**
	 * @param maxAtom
	 */
	public ContextAtom(int maxAtom) {
		this.maxAtom = maxAtom;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.core.util.UtilAtom#increment()
	 */
	@Override
	public void increment() {
		lock.lock();
		if (++atomic > maxAtom) {
			maxAwait = true;
			try {
				maxCondition.await();
				return;

			} catch (Exception e) {
				if (Environment.getEnvironment() == Environment.DEVELOP) {
					e.printStackTrace();
				}
			}
		}

		lock.unlock();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.core.util.UtilAtom#decrement()
	 */
	@Override
	public void decrement() {
		lock.lock();
		if (--atomic == 0) {
			condition.signal();
		}

		if (maxAwait && atomic < maxAtom) {
			maxAwait = false;
			maxCondition.signal();
		}

		lock.unlock();
	}
}
