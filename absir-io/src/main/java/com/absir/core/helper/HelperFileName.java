/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.core.helper;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import org.apache.commons.io.FilenameUtils;

import com.absir.core.kernel.KernelCharset;
import com.absir.core.kernel.KernelString;

/**
 * @author absir
 * 
 */
public class HelperFileName extends FilenameUtils {

	/**
	 * The Unix separator character.
	 */
	public static final char UNIX_SEPARATOR = '/';

	/**
	 * The Windows separator character.
	 */
	public static final char WINDOWS_SEPARATOR = '\\';

	/**
	 * The system separator character.
	 */
	public static final char SYSTEM_SEPARATOR = File.separatorChar;

	/**
	 * The separator character that is the opposite of the system separator.
	 */
	public static final char OTHER_SEPARATOR = SYSTEM_SEPARATOR == WINDOWS_SEPARATOR ? UNIX_SEPARATOR
			: WINDOWS_SEPARATOR;

	/**
	 * @param cls
	 * @return
	 */
	public static String getClassPath(Class<?> cls) {
		String classPath = null;
		if (cls == null) {
			return getResourcePath(Thread.currentThread().getContextClassLoader().getResource(""));
		}

		ProtectionDomain domain = cls.getProtectionDomain();
		if (domain != null) {
			CodeSource source = domain.getCodeSource();
			if (source != null) {
				classPath = getResourcePath(source.getLocation());
			}
		}

		if (classPath == null) {
			try {
				classPath = cls.getResource("").getPath();
				int length = classPath.length();
				classPath = classPath.substring((length > 4 && classPath.startsWith("file:")) ? 5 : 0,
						length - cls.getPackage().getName().length() - 2);
				classPath = HelperFileName.getFullPath(classPath);

			} catch (Throwable e) {
				classPath = "file:///";
			}

		} else {
			URL resourceURL = Thread.currentThread().getContextClassLoader().getResource("");
			if (resourceURL != null) {
				try {
					String loaderPath = getResourcePath(resourceURL);
					int length = Math.min(loaderPath.length(), classPath.length());
					int similar = 0;
					for (; similar < length; similar++) {
						if (loaderPath.charAt(similar) != classPath.charAt(similar)) {
							break;
						}
					}

					String similarPath = classPath;
					for (int i = 0; i < 3; i++) {
						similarPath = HelperFileName.getFullPathNoEndSeparator(similarPath);
						if (similarPath.length() <= similar) {
							classPath = loaderPath;
							break;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (HelperFileName.getPrefixLength(classPath) <= 0) {
				classPath = "/" + classPath;
			}
		}

		return classPath;
	}

	/**
	 * @param resourceURL
	 * @return
	 */
	public static String getResourcePath(URL resourceURL) {
		if (resourceURL == null) {
			return null;
		}

		String realPath = null;
		try {
			realPath = HelperFileName
					.getPath(URLDecoder.decode(resourceURL.getPath(), KernelCharset.getDefault().name()));
			int length = realPath.length();
			if (length > 1) {
				if (realPath.charAt(0) == UNIX_SEPARATOR) {
					if (SYSTEM_SEPARATOR == WINDOWS_SEPARATOR) {
						realPath = realPath.substring(1);
					}

				} else if (length > 4 && realPath.startsWith("file:")) {
					realPath = realPath.substring(5);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return realPath;
	}

	/**
	 * @param filename
	 * @return
	 */
	public static String unixFilename(String filename) {
		return filename.replace(WINDOWS_SEPARATOR, UNIX_SEPARATOR);
	}

	/**
	 * @param basepath
	 * @param pathNames
	 * @param filename
	 * @return
	 */
	public static String iterateFilename(String basepath, String pathNames, String filename) {
		pathNames = HelperFileName.getFullPathNoEndSeparator(pathNames);
		return HelperFileName.iterateFilename(basepath + SYSTEM_SEPARATOR + pathNames,
				pathNames.split(Character.toString(UNIX_SEPARATOR)), filename);
	}

	/**
	 * @param basePath
	 * @param pathNames
	 * @param filename
	 * @return
	 */
	public static String iterateFilename(String basePath, String[] pathNames, String filename) {
		int length = basePath.length();
		int last = pathNames.length - 1;
		for (int i = last; i > 0; i--) {
			String pathname = pathNames[i];
			if (pathname.indexOf(EXTENSION_SEPARATOR) >= 0 && i < last) {
				break;
			}

			length -= pathname.length() + 1;
			pathname = basePath.substring(0, length) + SYSTEM_SEPARATOR + filename;
			if (HelperFile.fileExists(pathname)) {
				return pathname;
			}
		}

		return null;
	}

	/**
	 * @param filename
	 * @param subExtension
	 * @return
	 */
	public static String addFilenameSubExtension(String filename, String subExtension) {
		String extension = getExtension(filename);
		if (KernelString.isEmpty(extension)) {
			return filename + EXTENSION_SEPARATOR + subExtension;

		} else {
			return KernelString.rightSubString(filename, extension.length()) + subExtension + EXTENSION_SEPARATOR
					+ extension;
		}
	}
}
