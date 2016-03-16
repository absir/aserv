/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-3 下午4:23:10
 */
package com.absir.orm.hibernate.transaction;

import com.absir.orm.transaction.TransactionSession;

import java.util.Stack;

public class JTransactionSession extends TransactionSession {

    JSession currentSession;

    private Stack<JSession> sessionStack;

    public JSession getCurrentSession() {
        return currentSession;
    }

    public void pushCurrentSession() {
        if (currentSession != null) {
            if (sessionStack == null) {
                sessionStack = new Stack<JSession>();
            }

            sessionStack.add(currentSession);
            currentSession = null;
        }
    }

    public void openCurrentSession(JSession currentSession) {
        pushCurrentSession();
        this.currentSession = currentSession;
    }

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

    public JSession closeCurrentSession() {
        JSession jSession = currentSession;
        if (currentSession != null) {
            currentSession = null;
            popCurrentSession();
        }

        return jSession;
    }
}
