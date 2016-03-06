/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.core.helper;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.absir.core.kernel.KernelLang.CallbackBreak;
import com.absir.core.kernel.KernelString;

/**
 * @author absir
 * 
 */
public class HelperFile extends FileUtils {

	/**
	 * @param filename
	 * @return
	 */
	public static URL existUrl(String filename) {
		try {
			return new URL(filename);

		} catch (MalformedURLException e) {
		}

		return null;
	}

	/**
	 * @param filename
	 * @return
	 */
	public static boolean urlExists(String filename) {
		return existUrl(filename) != null;
	}

	/**
	 * @param filename
	 * @return
	 */
	public static File existFile(String filename) {
		File fl = new File(filename);
		return (fl.exists() && fl.isFile()) ? fl : null;
	}

	/**
	 * @param filename
	 * @return
	 */
	public static boolean fileExists(String filename) {
		return existFile(filename) != null;
	}

	/**
	 * @param filename
	 * @return
	 */
	public static File existDirectory(String filename) {
		File fl = new File(filename);
		return (fl.exists() && fl.isDirectory()) ? fl : null;
	}

	/**
	 * @param filename
	 * @return
	 */
	public static boolean directoryExists(String filename) {
		return existDirectory(filename) != null;
	}

	/**
	 * @param file
	 * @param callback
	 * @throws IOException
	 */
	public static void doWithReadLine(File file, CallbackBreak<String> callback) throws IOException {
		doWithReadLine(file, Charset.defaultCharset(), callback);
	}

	/**
	 * @param file
	 * @param encoding
	 * @param callback
	 * @throws IOException
	 */
	public static void doWithReadLine(File file, String encoding, CallbackBreak<String> callback) throws IOException {
		doWithReadLine(file, Charsets.toCharset(encoding), callback);
	}

	/**
	 * @param file
	 * @param encoding
	 * @param callback
	 * @throws IOException
	 */
	public static void doWithReadLine(File file, Charset encoding, CallbackBreak<String> callback) throws IOException {
		InputStream input = null;
		try {
			input = new FileInputStream(file);
			HelperIO.doWithReadLine(input, encoding, callback);

		} finally {
			if (input != null) {
				input.close();
			}
		}
	}

	/**
	 * @param file
	 * @param input
	 * @throws IOException
	 */
	public static void write(File file, InputStream input) throws IOException {
		HelperIO.copy(input, openOutputStream(file));
	}

	/**
	 * @param file
	 * @param lastModified
	 * @return
	 * @throws IOException
	 */
	public static FileOutputStream openOutputStream(File file, Long lastModified) throws IOException {
		if (!file.exists() || lastModified == null || file.lastModified() < lastModified) {
			synchronized (file) {
				if (!file.exists()) {
					lastModified = null;
				}

				FileOutputStream output = null;
				try {
					output = HelperFile.openOutputStream(file);
					if (output.getChannel().tryLock() != null) {
						return output;
					}

				} catch (Exception e) {
					if (output != null) {
						output.close();
					}

					throw (IOException) e;
				}
			}
		}

		return null;
	}

	/**
	 * @param srcFile
	 * @param destFile
	 * @param overWrite
	 * @param preserveFileDate
	 * @return
	 * @throws IOException
	 */
	public static boolean copyFileOverWrite(File srcFile, File destFile, boolean overWrite, boolean preserveFileDate)
			throws IOException {
		if (destFile.exists() && !overWrite) {
			return false;
		}

		copyFile(srcFile, destFile, preserveFileDate);
		return true;
	}

	/**
	 * @param srcDir
	 * @param destDir
	 * @param overWrite
	 * @param preserveFileDate
	 * @throws IOException
	 */
	public static void copyDirectoryOverWrite(File srcDir, File destDir, boolean overWrite, FileFilter filter,
			boolean preserveFileDate) throws IOException {
		if (srcDir == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (destDir == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (srcDir.exists() == false) {
			throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
		}
		if (srcDir.isDirectory() == false) {
			throw new IOException("Source '" + srcDir + "' exists but is not a directory");
		}
		if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
			throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
		}

		// Cater for destination being directory within the source directory
		// (see IO-141)
		List<String> exclusionList = null;
		if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
			File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
			if (srcFiles != null && srcFiles.length > 0) {
				exclusionList = new ArrayList<String>(srcFiles.length);
				for (File srcFile : srcFiles) {
					File copiedFile = new File(destDir, srcFile.getName());
					exclusionList.add(copiedFile.getCanonicalPath());
				}
			}
		}

		doCopyDirectoryOverWrite(srcDir, destDir, overWrite, filter, preserveFileDate, exclusionList);
	}

	/**
	 * @param srcDir
	 * @param destDir
	 * @param overWrite
	 * @param filter
	 * @param preserveFileDate
	 * @param exclusionList
	 * @throws IOException
	 */
	private static void doCopyDirectoryOverWrite(File srcDir, File destDir, boolean overWrite, FileFilter filter,
			boolean preserveFileDate, List<String> exclusionList) throws IOException {
		File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
		if (srcFiles == null) { // null if abstract pathname does not denote a
								// directory, or if an I/O error occurs
			throw new IOException("Failed to list contents of " + srcDir);
		}

		if (destDir.exists()) {
			if (destDir.isDirectory() == false) {
				throw new IOException("Destination '" + destDir + "' exists but is not a directory");
			}

		} else {
			if (!destDir.mkdirs() && !destDir.isDirectory()) {
				throw new IOException("Destination '" + destDir + "' directory cannot be created");
			}
		}

		if (destDir.canWrite() == false) {
			throw new IOException("Destination '" + destDir + "' cannot be written to");
		}

		for (File srcFile : srcFiles) {
			File dstFile = new File(destDir, srcFile.getName());
			if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
				if (srcFile.isDirectory()) {
					doCopyDirectoryOverWrite(srcFile, dstFile, overWrite, filter, preserveFileDate, exclusionList);

				} else {
					copyFileOverWrite(srcFile, dstFile, overWrite, preserveFileDate);
				}
			}
		}

		// Do this last, as the above has probably affected directory metadata
		if (preserveFileDate) {
			destDir.setLastModified(srcDir.lastModified());
		}
	}

	/**
	 * @param url
	 * @param destDir
	 * @param overWrite
	 * @param preserveFileDate
	 * @throws IOException
	 */
	public static void copyDirectoryOverWrite(URL url, File destDir, boolean overWrite, FileFilter filter,
			boolean preserveFileDate) throws IOException {
		if (url == null) {
			return;
		}

		URLConnection urlConnection = url.openConnection();
		if (urlConnection instanceof JarURLConnection) {
			copyDirectoryOverWrite((JarURLConnection) urlConnection, destDir, overWrite, filter, preserveFileDate);

		} else {
			copyDirectoryOverWrite(new File(url.getFile()), destDir, overWrite, filter, preserveFileDate);
		}
	}

	/**
	 * @param jarURLConnection
	 * @param destDir
	 * @param overWrite
	 * @param preserveFileDate
	 * @throws IOException
	 */
	public static void copyDirectoryOverWrite(JarURLConnection jarURLConnection, File destDir, boolean overWrite,
			FileFilter filter, boolean preserveFileDate) throws IOException {
		JarFile jarFile = jarURLConnection.getJarFile();
		Enumeration<JarEntry> enumeration = jarFile.entries();
		String entryName = jarURLConnection.getEntryName();
		int entryLength = entryName.length();
		String destPath = destDir.getPath() + "/";
		while (enumeration.hasMoreElements()) {
			JarEntry jarEntry = enumeration.nextElement();
			String filename = jarEntry.getName();
			if (filename.startsWith(entryName)) {
				filename = KernelString.leftSubString(filename, entryLength);
				if (!jarEntry.isDirectory()) {
					File destFile = new File(destPath + filename);
					if ((overWrite || !destFile.exists()) && (filter == null || filter.accept(destFile))) {
						InputStream inputStream = null;
						try {
							inputStream = jarFile.getInputStream(jarEntry);
							HelperFile.copyInputStreamToFile(inputStream, destFile);

						} finally {
							if (inputStream != null) {
								try {
									inputStream.close();

								} catch (Exception e) {
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @param inputStream
	 * @param destDir
	 * @param overWrite
	 * @param filter
	 * @param preserveFileDate
	 * @throws IOException
	 */
	public static void copyDirectoryOverWrite(ZipInputStream inputStream, File destDir, boolean overWrite,
			FileFilter filter, boolean preserveFileDate) throws IOException {
		String destPath = destDir.getPath() + "/";
		ZipEntry zipEntry;
		try {
			while ((zipEntry = inputStream.getNextEntry()) != null) {
				if (!zipEntry.isDirectory()) {
					File destFile = new File(destPath + zipEntry.getName());
					if ((overWrite || destFile.exists()) && (filter == null || filter.accept(destFile))) {
						try {
							FileOutputStream output = HelperFile.openOutputStream(destFile);
							try {
								IOUtils.copy(inputStream, output);

							} finally {
								IOUtils.closeQuietly(output);
							}

						} finally {
							inputStream.closeEntry();
						}
					}
				}
			}

		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}
}
