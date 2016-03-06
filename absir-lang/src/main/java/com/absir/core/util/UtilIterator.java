/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-5-17 上午9:38:36
 */
package com.absir.core.util;

import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;

/**
 * @author absir
 * 
 */
public class UtilIterator {

	/**
	 * @param name
	 * @param ch
	 * @param callback
	 */
	public static void iterateName(String name, char ch, CallbackBreak<String> callback) {
		iterateName(name, ch, callback, 0);
	}

	/**
	 * @param name
	 * @param ch
	 * @param callback
	 * @param fromIndex
	 */
	public static void iterateName(String name, char ch, CallbackBreak<String> callback, int fromIndex) {
		while (true) {
			try {
				callback.doWith(name);
				int endIndex = name.lastIndexOf(ch, fromIndex);
				if (endIndex > 0) {
					name = name.substring(0, endIndex);
					continue;
				}

			} catch (BreakException e) {
			}

			break;
		}
	}

	/**
	 * @param name
	 * @param str
	 * @param callback
	 */
	public static void iterateName(String name, String str, CallbackBreak<String> callback) {
		iterateName(name, str, callback, 0);
	}

	/**
	 * @param name
	 * @param str
	 * @param callback
	 * @param fromIndex
	 */
	public static void iterateName(String name, String str, CallbackBreak<String> callback, int fromIndex) {
		while (true) {
			try {
				callback.doWith(name);
				int endIndex = name.lastIndexOf(str, fromIndex);
				if (endIndex > 0) {
					name = name.substring(0, endIndex);
					continue;
				}

			} catch (BreakException e) {
			}

			break;
		}
	}

	/**
	 * @param name
	 * @param ch
	 * @param callback
	 */
	public static void reverseName(String name, char ch, CallbackBreak<String> callback) {
		reverseName(name, ch, callback, 0);
	}

	/**
	 * @param name
	 * @param ch
	 * @param callback
	 * @param fromIndex
	 */
	public static void reverseName(String name, char ch, CallbackBreak<String> callback, int fromIndex) {
		reverseName(name, ch, callback, fromIndex, name.length());
	}

	/**
	 * @param name
	 * @param ch
	 * @param callback
	 * @param fromIndex
	 * @param endIndex
	 */
	public static void reverseName(String name, char ch, CallbackBreak<String> callback, int fromIndex, int endIndex) {
		while (true) {
			try {
				fromIndex = name.indexOf(ch, fromIndex);
				if (fromIndex < endIndex) {
					callback.doWith(name.substring(0, fromIndex));
					continue;
				}

			} catch (BreakException e) {
			}

			break;
		}
	}

	/**
	 * @param name
	 * @param str
	 * @param callback
	 */
	public static void reverseName(String name, String str, CallbackBreak<String> callback) {
		reverseName(name, str, callback, 0);
	}

	/**
	 * @param name
	 * @param str
	 * @param callback
	 * @param fromIndex
	 */
	public static void reverseName(String name, String str, CallbackBreak<String> callback, int fromIndex) {
		reverseName(name, str, callback, fromIndex, name.length());
	}

	/**
	 * @param name
	 * @param str
	 * @param callback
	 * @param fromIndex
	 * @param endIndex
	 */
	public static void reverseName(String name, String str, CallbackBreak<String> callback, int fromIndex, int endIndex) {
		while (true) {
			try {
				fromIndex = name.indexOf(str, fromIndex);
				if (fromIndex < endIndex) {
					callback.doWith(name.substring(0, fromIndex));
					continue;
				}

			} catch (BreakException e) {
			}

			break;
		}
	}
}
