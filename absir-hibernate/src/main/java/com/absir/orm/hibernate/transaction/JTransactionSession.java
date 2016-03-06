/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-3 下午4:23:10
 */
package com.absir.orm.hibernate.transaction;

import java.util.Stack;

import com.absir.orm.transaction.TransactionSession;

/**
 * @author absir
 * 
 */
public class JTransactionSession extends TransactionSession {

	/** currentSession */
	JSession currentSession;

	/** sessionStack */
	private Stack<JSession> sessionStack;

	/**
	 * @return
	 */
	public JSession getCurrentSession() {
		return currentSession;
	}

	/**
	 * 
	 */
	public void pushCurrentSession() {
		if (currentSession != null) {
			if (sessionStack == null) {
				sessionStack = new Stack<JSession>();
			}

			sessionStack.add(currentSession);
			currentSession = null;
		}
	}

	/**
	 * @param currentSession
	 */
	public void openCurrentSession(JSession currentSession) {
		pushCurrentSession();
		this.currentSession = currentSession;
	}

	/**
	 * 
	 */
	public void popCurrentSession() {
		if (currentSession == null && sessionStack != null) {
			if (sessionStack.isEmpty()) {
				currentSession = null;
				sessionStack = null;

			} else {
				currentSession = sessionStack.pop();
				if (sessionStack.isEmpty()) {
					sessionStack = null;
				}
			}
		}
	}

	/**
	 * 
	 */
	public JSession closeCurrentSession() {
		JSession jSession = currentSession;
		if (currentSession != null) {
			currentSession = null;
			popCurrentSession();
		}

		return jSession;
	}
}
