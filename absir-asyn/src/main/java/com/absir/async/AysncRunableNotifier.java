/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-26 下午4:39:08
 */
package com.absir.async;

import java.lang.reflect.Method;
import java.util.Iterator;

import net.sf.cglib.proxy.MethodProxy;

import com.absir.aop.AopInterceptor;
import com.absir.aop.AopProxyHandler;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public class AysncRunableNotifier extends AysncRunable {

	/**
	 * @param timeout
	 * @param thread
	 */
	public AysncRunableNotifier(long timeout, boolean thread) {
		super(timeout, thread);
	}

	/** notifying */
	private boolean notifying;

	/** notifierIterator */
	private NotifierIterator notifierIterator;

	/**
	 * @author absir
	 * 
	 */
	private static class NotifierIterator {

		/** proxy */
		private Object proxy;

		/** iterator */
		private Iterator<AopInterceptor> iterator;

		/** proxyHandler */
		private AopProxyHandler proxyHandler;

		/** method */
		private Method method;

		/** args */
		private Object[] args;

		/** methodProxy */
		private MethodProxy methodProxy;
	}

	/**
	 * @param proxy
	 * @param iterator
	 * @param proxyHandler
	 * @param method
	 * @param args
	 * @param methodProxy
	 * @return
	 */
	public Runnable notifierRunable(final Object proxy, final Iterator<AopInterceptor> iterator,
			final AopProxyHandler proxyHandler, final Method method, final Object[] args, final MethodProxy methodProxy) {
		return new Runnable() {

			@Override
			public void run() {
				try {
					proxyHandler.invoke(proxy, iterator, method, args, methodProxy);

				} catch (Throwable e) {
					LOGGER.error("aysnc notifier run", e);

				} finally {
					checkNotifierIterator();
				}
			}
		};
	}

	/**
	 * 
	 */
	protected void checkNotifierIterator() {
		NotifierIterator iterator = null;
		synchronized (this) {
			if (notifierIterator == null) {
				notifying = false;
				return;

			} else {
				iterator = notifierIterator;
				notifierIterator = null;
			}
		}

		try {
			aysncRun(notifierRunable(iterator.proxy, iterator.iterator, iterator.proxyHandler, iterator.method, iterator.args,
					iterator.methodProxy));

		} catch (Throwable e) {
			checkNotifierIterator();
			LOGGER.error("aysnc notifier run", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.async.AysncRunable#aysnc(java.lang.Object,
	 * java.util.Iterator, com.absir.aop.AopProxyHandler,
	 * java.lang.reflect.Method, java.lang.Object[],
	 * net.sf.cglib.proxy.MethodProxy)
	 */
	@Override
	public void aysnc(Object proxy, Iterator<AopInterceptor> iterator, AopProxyHandler proxyHandler, Method method, Object[] args,
			MethodProxy methodProxy) throws Throwable {
		synchronized (this) {
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

		try {
			aysncRun(notifierRunable(proxy, iterator, proxyHandler, method, args, methodProxy));

		} catch (Throwable e) {
			checkNotifierIterator();
			LOGGER.error("aysnc notifier run", e);
		}
	}
}
