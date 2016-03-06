/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-5-28 下午4:49:18
 */
package com.absir.aserv.system.helper;

import java.io.File;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public class HelperClass {

	/** CLASS_FILE_EXTENSION */
	public static final String CLASS_FILE_EXTENSION = ".class";

	/**
	 * @param cls
	 * @return
	 */
	public static File getClassFile(Class cls) {
		File file = new File(cls.getResource(cls.getSimpleName().concat(CLASS_FILE_EXTENSION)).getFile());
		if (!file.exists()) {
			file = new File(cls.getProtectionDomain().getCodeSource().getLocation().getFile());
		}

		return file;
	}

	/**
	 * @param cls
	 * @return
	 */
	public static Long lastModified(Class cls) {
		return getClassFile(cls).lastModified();
	}
}
