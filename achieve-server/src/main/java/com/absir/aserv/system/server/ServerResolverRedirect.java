/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-12 下午4:39:43
 */
package com.absir.aserv.system.server;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import com.absir.aserv.system.server.value.Redirect;
import com.absir.bean.inject.value.Bean;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.returned.ReturnedResolver;
import com.absir.servlet.InputRequest;

/**
 * @author absir
 * 
 */
@Bean
public class ServerResolverRedirect implements ReturnedResolver<Boolean> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.server.route.returned.ReturnedResolver#getReturned(java.lang
	 * .reflect.Method)
	 */
	@Override
	public Boolean getReturned(Method method) {
		Redirect redirect = method.getAnnotation(Redirect.class);
		return redirect == null ? null : redirect.forward();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.server.route.returned.ReturnedResolver#getReturned(java.lang
	 * .Class)
	 */
	@Override
	public Boolean getReturned(Class<?> beanClass) {
		Redirect redirect = beanClass.getAnnotation(Redirect.class);
		return redirect == null ? null : redirect.forward();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.server.route.returned.ReturnedResolver#resolveReturnedValue
	 * (java.lang.Object, java.lang.Object, com.absir.server.on.OnPut)
	 */
	@Override
	public void resolveReturnedValue(Object returnValue, Boolean returned, OnPut onPut) throws Exception {
		Input input = onPut.getInput();
		if (input instanceof InputRequest) {
			String redirect = null;
			if (returnValue != null && returnValue instanceof String) {
				redirect = (String) returnValue;
			}

			if (redirect != null) {
				if (returned) {
					InputRequest inputRequest = (InputRequest) input;
					HttpServletRequest request = inputRequest.getRequest();
					request.getRequestDispatcher(redirect).forward(request, inputRequest.getResponse());

				} else {
					((InputRequest) input).getResponse().sendRedirect(redirect);
				}
			}
		}
	}

	/**
	 * @param url
	 * @param forward
	 * @param input
	 * @throws Exception
	 */
	public static void redirect(String url, boolean forward, Input input) throws Exception {
		if (input instanceof InputRequest) {
			if (url != null) {
				if (forward) {
					InputRequest inputRequest = (InputRequest) input;
					HttpServletRequest request = inputRequest.getRequest();
					request.getRequestDispatcher(url).forward(request, inputRequest.getResponse());

				} else {
					((InputRequest) input).getResponse().sendRedirect(url);
				}
			}
		}

		throw new ServerException(ServerStatus.ON_SUCCESS);
	}
}
