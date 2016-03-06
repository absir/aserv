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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absir.aop.AopInterceptor;
import com.absir.aop.AopProxyHandler;
import com.absir.context.core.ContextUtils;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public class AysncRunable {

	/** LOGGER */
	protected static final Logger LOGGER = LoggerFactory.getLogger(AysncRunable.class);

	/** timeout */
	protected long timeout;

	/** thread */
	protected boolean thread;

	/**
	 * @param timeout
	 * @param thread
	 */
	public AysncRunable(long timeout, boolean thread) {
		this.timeout = timeout;
		this.thread = thread;
	}

	/**
	 * @param proxy
	 * @param iterator
	 * @param proxyHandler
	 * @param method
	 * @param args
	 * @param methodProxy
	 * @throws Throwable
	 */
	public void aysnc(final Object proxy, final Iterator<AopInterceptor> iterator, final AopProxyHandler proxyHandler,
			final Method method, final Object[] args, final MethodProxy methodProxy) throws Throwable {
		aysncRun(new Runnable() {

			@Override
			public void run() {
				try {
					proxyHandler.invoke(proxy, iterator, method, args, methodProxy);

				} catch (Throwable e) {
					LOGGER.error("aysnc run", e);
				}
			}
		});
	}

	/**
	 * @param runnable
	 */
	public void aysncRun(Runnable runnable) {
		if (timeout > 0 || thread) {
			final Thread doThread = new Thread(runnable);
			doThread.setName("aysncRun");
			doThread.setDaemon(true);
			doThread.start();
			if (timeout > 0) {
				Runnable timeoutRunnable = new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(timeout);
							doThread.interrupt();

						} catch (InterruptedException e) {
						}
					}
				};

				if (thread) {
					Thread thread = new Thread(timeoutRunnable);
					thread.setName("aysncRun.timeout");
					thread.setDaemon(true);
					thread.start();

				} else {
					ContextUtils.getThreadPoolExecutor().execute(timeoutRunnable);
				}
			}

		} else {
			ContextUtils.getThreadPoolExecutor().execute(runnable);
		}
	}
}
