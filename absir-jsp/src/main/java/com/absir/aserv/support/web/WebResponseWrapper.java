/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.support.web;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class WebResponseWrapper extends HttpServletResponseWrapper {

    private Writer writer;

    private JspWriter jspWriter;

    private PrintWriter printWriter;

    public WebResponseWrapper(ServletResponse response) {
        this(response, null);
    }

    public WebResponseWrapper(ServletResponse response, JspWriter jspWriter) {
        super((HttpServletResponse) response);
        writer = jspWriter;
        if (writer == null) {
            writer = new StringWriter();
        }

        this.jspWriter = jspWriter;
        this.printWriter = new PrintWriter(writer);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        throw new IllegalStateException();
    }

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

    public String getContent() {
        return writer.toString();
    }
}
