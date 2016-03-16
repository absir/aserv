/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月4日 下午2:08:46
 */
package jetbrick.template;

import com.absir.core.kernel.KernelCharset;
import jetbrick.template.runtime.JetWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class JetWriterNone extends JetWriter {

    public static final JetWriterNone ME = new JetWriterNone();

    public static final OutputStream OUT = new OutputStream() {

        @Override
        public void write(int b) throws IOException {

        }
    };

    @Override
    public Object getOriginStream() {
        return OUT;
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    @Override
    public Charset getCharset() {
        return KernelCharset.UTF8;
    }

    @Override
    public boolean isSkipErrors() {
        return true;
    }

    @Override
    public void print(int x) throws IOException {

    }

    @Override
    public void print(byte[] x) throws IOException {

    }

    @Override
    public void print(byte[] x, int offset, int length) throws IOException {

    }

    @Override
    public void print(char[] x) throws IOException {

    }

    @Override
    public void print(char[] x, int offset, int length) throws IOException {

    }

    @Override
    public void print(String x) throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }

}
