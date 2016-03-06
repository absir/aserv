/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.support.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.jsp.JspWriter;

/**
 * @author absir
 * 
 */
public class WebResponseWrapper extends HttpServletResponseWrapper {

	/** writer */
	private Writer writer;

	/** jspWriter */
	private JspWriter jspWriter;

	/** printWriter */
	private PrintWriter printWriter;

	/**
	 * @param response
	 */
	public WebResponseWrapper(ServletResponse response) {
		this(response, null);
	}

	/**
	 * @param response
	 * @param jspWriter
	 */
	public WebResponseWrapper(ServletResponse response, JspWriter jspWriter) {
		super((HttpServletResponse) response);
		writer = jspWriter;
		if (writer == null) {
			writer = new StringWriter();
		}

		this.jspWriter = jspWriter;
		this.printWriter = new PrintWriter(writer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponseWrapper#getWriter()
	 */
	@Override
	public PrintWriter getWriter() throws IOException {
		return printWriter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponseWrapper#getOutputStream()
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		throw new IllegalStateException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponseWrapper#resetBuffer()
	 */
	@Override
	public void resetBuffer() {
		if (jspWriter == null) {
			writer = new StringWriter();
			printWriter = new PrintWriter(writer);

		} else {
			try {
				jspWriter.clearBuffer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return
	 */
	public String getContent() {
		return writer.toString();
	}
}
