/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-5-23 下午2:14:25
 */
package com.absir.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author absir
 * 
 */
@SuppressWarnings("unchecked")
public class InputCookies {

	/** COOKIES_NAME */
	private static final String COOKIES_NAME = InputCookies.class.getName() + "@COOKIES";

	/**
	 * @param request
	 * @return
	 */
	public static Map<String, Cookie> getCookies(HttpServletRequest request) {
		Object cookies = request.getAttribute(COOKIES_NAME);
		if (cookies == null || !(cookies instanceof Map)) {
			Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
			if (request.getCookies() != null) {
				for (Cookie cookie : request.getCookies()) {
					cookieMap.put(cookie.getName(), cookie);
				}
			}

			request.setAttribute(COOKIES_NAME, cookieMap);
			return cookieMap;
		}

		return (Map<String, Cookie>) cookies;
	}

	/**
	 * @param request
	 * @param name
	 * @return
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		return getCookies(request).get(name);
	}

	/**
	 * @param request
	 * @param name
	 * @return
	 */
	public static String getCookieValue(HttpServletRequest request, String name) {
		Cookie cookie = getCookie(request, name);
		return cookie == null ? null : cookie.getValue();
	}

	/**
	 * @param request
	 * @param name
	 * @param value
	 * @return
	 */
	public static boolean containCookie(HttpServletRequest request, String name, String value) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(name) && value.equals(value)) {
					return true;
				}
			}
		}

		return false;
	}
}
