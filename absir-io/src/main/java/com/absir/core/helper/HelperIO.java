/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.core.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;

/**
 * @author absir
 * 
 */
public class HelperIO extends IOUtils {

	/**
	 * @param input
	 * @param callback
	 * @throws IOException
	 */
	public static void doWithReadLine(InputStream input, CallbackBreak<String> callback) throws IOException {
		doWithReadLine(input, Charset.defaultCharset(), callback);
	}

	/**
	 * @param input
	 * @param encoding
	 * @param callback
	 * @throws IOException
	 */
	public static void doWithReadLine(InputStream input, String encoding, CallbackBreak<String> callback)
			throws IOException {
		doWithReadLine(input, Charset.forName(encoding), callback);
	}

	/**
	 * @param input
	 * @param encoding
	 * @param callback
	 * @throws IOException
	 */
	public static void doWithReadLine(InputStream input, Charset encoding, CallbackBreak<String> callback)
			throws IOException {
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(input, encoding);
			doWithReadLine(reader, callback);

		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * @param reader
	 * @param callback
	 * @throws IOException
	 */
	public static void doWithReadLine(Reader reader, CallbackBreak<String> callback) throws IOException {
		if (reader instanceof BufferedReader) {
			doWithReadLine((BufferedReader) reader, callback);

		} else {
			BufferedReader bufferedReader = null;
			try {
				bufferedReader = new BufferedReader(reader);
				doWithReadLine(bufferedReader, callback);

			} finally {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			}
		}
	}

	/**
	 * @param reader
	 * @param callback
	 * @throws IOException
	 */
	public static void doWithReadLine(BufferedReader reader, CallbackBreak<String> callback) throws IOException {
		while (true) {
			try {
				String line = reader.readLine();
				if (line == null) {
					break;
				}

				callback.doWith(line);

			} catch (BreakException e) {
				break;
			}
		}
	}
}
