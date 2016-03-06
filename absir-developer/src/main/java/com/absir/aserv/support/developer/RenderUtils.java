/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年7月28日 下午3:21:31
 */
package com.absir.aserv.support.developer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import com.absir.bean.core.BeanFactoryUtils;
import com.absir.context.core.ContextUtils;
import com.absir.core.base.Environment;

/**
 * @author absir
 *
 */
public class RenderUtils {

	/**
	 * @param include
	 * @param renders
	 * @return
	 * @throws IOException
	 */
	public static String load(String include, Object... renders) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		IRender.ME.rend(byteArrayOutputStream, include, renders);
		return byteArrayOutputStream.toString(ContextUtils.getCharset().displayName());

	}

	/**
	 * @param include
	 * @param renders
	 * @return
	 * @throws IOException
	 */
	public static String loadExist(String include, Object... renders) throws IOException {
		if (new File(IRender.ME.getRealPath(include, renders)).exists()) {
			return load(include, renders);
		}

		return null;
	}

	/**
	 * @param include
	 * @param renders
	 * @throws IOException
	 */
	public static void generate(String include, Object... renders) throws IOException {
		if (BeanFactoryUtils.getEnvironment() != Environment.PRODUCT && IDeveloper.ME != null) {
			generate(include, IRender.ME.getPath(renders), renders);
		}
	}

	/**
	 * @param include
	 * @param generate
	 * @param renders
	 * @throws IOException
	 */
	public static void generate(String include, String generate, Object... renders) throws IOException {
		if (BeanFactoryUtils.getEnvironment() != Environment.PRODUCT && IDeveloper.ME != null) {
			IDeveloper.ME.generate(IRender.ME.getFullPath(include, renders), IRender.ME.getFullPath(generate, renders), renders);
		}
	}

	/**
	 * @param include
	 * @param renders
	 * @throws IOException
	 */
	public static void include(String include, Object... renders) throws IOException {
		if (BeanFactoryUtils.getEnvironment() != Environment.PRODUCT && IDeveloper.ME != null) {
			include(include, IRender.ME.getPath(renders), renders);
		}
	}

	/**
	 * @param include
	 * @param generate
	 * @param renders
	 * @throws IOException
	 */
	public static void include(String include, String generate, Object... renders) throws IOException {
		generate(include, generate, renders);
		IRender.ME.include(include, renders);
	}

	/**
	 * @param path
	 * @param renders
	 * @return
	 * @throws IOException
	 */
	public static boolean includeExist(String path, Object... renders) throws IOException {
		if (new File(IRender.ME.getRealPath(path)).exists()) {
			IRender.ME.include(path, renders);
			return true;
		}

		return false;
	}

}
