/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年12月29日 下午9:43:39
 */
package com.absir.core.util;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author absir
 *
 */
public class UtilOutputStream extends OutputStream {

	/** dataOutput */
	DataOutput dataOutput;

	/**
	 * @param dataOutput
	 */
	public UtilOutputStream(DataOutput dataOutput) {
		this.dataOutput = dataOutput;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		dataOutput.write((byte) b);
	}
}
