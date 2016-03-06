/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-7-3 下午1:26:16
 */
package com.absir.aserv.system.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.absir.core.helper.HelperIO;

/**
 * @author absir
 * 
 */
public class HelperServer {

	/**
	 * @param buffer
	 * @param offset
	 * @param count
	 * @param outStream
	 * @throws IOException
	 */
	public static void zipCompress(byte[] buffer, int offset, int count, OutputStream outStream) throws IOException {
		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outStream);
		gzipOutputStream.write(buffer, offset, count);
		gzipOutputStream.close();
	}

	/**
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static byte[] zipDeCompress(InputStream inputStream) throws IOException {
		GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
		byte[] buffer = HelperIO.toByteArray(gzipInputStream);
		gzipInputStream.close();
		return buffer;
	}
}
