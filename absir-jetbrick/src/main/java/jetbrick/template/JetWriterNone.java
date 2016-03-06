/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年5月4日 下午2:08:46
 */
package jetbrick.template;

import java.io.IOException;
import java.nio.charset.Charset;

import com.absir.core.kernel.KernelCharset;

import jetbrick.template.runtime.JetWriter;

/**
 * @author absir
 *
 */
public class JetWriterNone extends JetWriter {
	
	/** ME */
	public static final JetWriterNone ME = new JetWriterNone();

	/*
	 * (non-Javadoc)
	 * 
	 * @see jetbrick.template.runtime.JetWriter#getOriginStream()
	 */
	@Override
	public Object getOriginStream() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jetbrick.template.runtime.JetWriter#isStreaming()
	 */
	@Override
	public boolean isStreaming() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jetbrick.template.runtime.JetWriter#getCharset()
	 */
	@Override
	public Charset getCharset() {
		return KernelCharset.UTF8;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jetbrick.template.runtime.JetWriter#isSkipErrors()
	 */
	@Override
	public boolean isSkipErrors() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jetbrick.template.runtime.JetWriter#print(int)
	 */
	@Override
	public void print(int x) throws IOException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jetbrick.template.runtime.JetWriter#print(byte[])
	 */
	@Override
	public void print(byte[] x) throws IOException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jetbrick.template.runtime.JetWriter#print(byte[], int, int)
	 */
	@Override
	public void print(byte[] x, int offset, int length) throws IOException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jetbrick.template.runtime.JetWriter#print(char[])
	 */
	@Override
	public void print(char[] x) throws IOException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jetbrick.template.runtime.JetWriter#print(char[], int, int)
	 */
	@Override
	public void print(char[] x, int offset, int length) throws IOException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jetbrick.template.runtime.JetWriter#print(java.lang.String)
	 */
	@Override
	public void print(String x) throws IOException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jetbrick.template.runtime.JetWriter#flush()
	 */
	@Override
	public void flush() throws IOException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jetbrick.template.runtime.JetWriter#close()
	 */
	@Override
	public void close() throws IOException {

	}

}
