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

/**
 * @author absir
 */
public class JetWriterNone extends JetWriter {

    /**
     * ME
     */
    public static final JetWriterNone ME = new JetWriterNone();

    /**
     * OUT
     */
    public static final OutputStream OUT = new OutputStream() {

        /**
         * @param b
         * @throws IOException
         */
        @Override
        public void write(int b) throws IOException {

        }
    };

    /*
     * (non-Javadoc)
     *
     * @see jetbrick.template.runtime.JetWriter#getOriginStream()
     */
    @Override
    public Object getOriginStream() {
        return OUT;
    }

    /*
     * (non-Javadoc)
     *
     * @see jetbrick.template.runtime.JetWriter#isStreaming()
     */
    @Override
    public boolean isStreaming() {
        return true;
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
