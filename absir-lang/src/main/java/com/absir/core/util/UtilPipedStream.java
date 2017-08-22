/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月22日 下午5:13:50
 */
package com.absir.core.util;

import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelLang.ObjectEntry;
import com.absir.core.util.UtilStep.IStep;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class UtilPipedStream implements IStep {

    public static final UtilStep STREAM_STEP = UtilStep.openUtilStep(true, "UtilPipedStream.STEP", 5000);

    protected long idleTime;

    private int size;

    private int index;

    private Map<Object, NextOutputStream> addMapOutStream;

    private Map<Object, NextOutputStream> nextMapOutStream = new HashMap<Object, NextOutputStream>();

    public UtilPipedStream(long idleTime) {
        if (idleTime < 1000) {
            idleTime = 1000;
        }

        this.idleTime = idleTime;
        STREAM_STEP.addStep(this);
    }

    public static final void closeCloseable(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();

        } catch (IOException e) {
            Environment.throwable(e);
        }
    }

    public static final long getHashIndex(int hashKey, int index) {
        long hashIndex = hashKey;
        hashIndex <<= 32;
        hashIndex += index;
        return hashIndex;
    }

    @Override
    public boolean stepDone(long currentTime) {
        if (idleTime > 0) {
            currentTime = UtilContext.getCurrentTime();
            Iterator<Entry<Object, NextOutputStream>> iterator = nextMapOutStream.entrySet().iterator();
            Entry<Object, NextOutputStream> entry;
            NextOutputStream outputStream;
            while (iterator.hasNext()) {
                entry = iterator.next();
                outputStream = entry.getValue();
                if (outputStream.passTime < currentTime) {
                    iterator.remove();
                    closeCloseable(outputStream);
                }
            }

            synchronized (this) {
                if (addMapOutStream != null) {
                    nextMapOutStream.putAll(addMapOutStream);
                    addMapOutStream = null;
                }

                size = nextMapOutStream.size();
            }

            return false;

        } else {
            for (NextOutputStream outputStream : nextMapOutStream.values()) {
                closeCloseable(outputStream);
            }

            nextMapOutStream.clear();
            synchronized (this) {
                if (addMapOutStream != null) {
                    for (NextOutputStream outputStream : addMapOutStream.values()) {
                        closeCloseable(outputStream);
                    }

                    addMapOutStream = null;
                }

                size = 0;
            }

            return true;
        }
    }

    public int getSize() {
        return size;
    }

    public synchronized void close() {
        idleTime = 0;
    }

    // 批量产出OutputStream
    public synchronized Entry<Integer, NextOutputStream> nextOutputStream(int hashKey) {
        if (idleTime > 0) {
            boolean maxed = false;
            long hashIndex = getHashIndex(hashKey, index);
            while (true) {
                if (getOutputStream(hashIndex) == null) {
                    NextOutputStream outputStream = createNextOutputStream();
                    addNextOutputStream(hashIndex, outputStream);
                    return new ObjectEntry<Integer, NextOutputStream>(index, outputStream);
                }

                if (index >= Integer.MAX_VALUE) {
                    if (maxed) {
                        return null;
                    }

                    maxed = true;
                    index = 0;
                    hashIndex = getHashIndex(hashKey, index);

                } else {
                    index++;
                    hashIndex++;
                }
            }
        }

        return null;
    }

    // 创建OutStream
    protected NextOutputStream createNextOutputStream() {
        return new NextOutputStream();
    }

    // 添加OutStream
    protected synchronized void addNextOutputStream(Object key, NextOutputStream value) {
        if (addMapOutStream == null) {
            addMapOutStream = new HashMap<Object, NextOutputStream>();
        }

        size++;
        value.retainAt();
        addMapOutStream.put(key, value);
    }

    // 获取hashIndex对应的OutputStream
    public NextOutputStream getOutputStream(Object hashIndex) {
        Map<Object, NextOutputStream> mapOutStream = addMapOutStream;
        NextOutputStream outputStream = nextMapOutStream.get(hashIndex);
        if (outputStream == null) {
            if (mapOutStream != null) {
                outputStream = mapOutStream.get(hashIndex);
            }
        }

        return outputStream;
    }

    public NextOutputStream getOutputStream(int hashKey, int index) {
        return getOutputStream(getHashIndex(hashKey, index));
    }

    // 强制创建hashIndex对应的OutputStream
    public NextOutputStream createNextOutputStream(Object hashIndex) {
        NextOutputStream outputStream = createNextOutputStream();
        setNextOutputStream(hashIndex, outputStream);
        return outputStream;
    }

    // 强制设置hashIndex对应的OutputStream
    public synchronized void setNextOutputStream(Object hashIndex, NextOutputStream value) {
        NextOutputStream outputStream = getOutputStream(hashIndex);
        if (outputStream != null) {
            closeCloseable(outputStream);
        }

        addNextOutputStream(hashIndex, value);
    }

    public static class WrapOutStream extends OutputStream {

        OutInputStream outInputStream;

        public WrapOutStream(OutInputStream inputStream) {
            outInputStream = inputStream;
        }

        @Override
        public void write(int b) throws IOException {
            outInputStream.receive(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            outInputStream.receive(b, off, len);
        }

        @Override
        public void close() throws IOException {
            outInputStream.close();
        }
    }

    public static class OutInputStream extends InputStream {

        public static final int DEFAULT_PIPE_SIZE = 1056;

        protected boolean closed;

        protected byte buffer[];

        protected int in = -1;

        protected int out = 0;

        public boolean isClosed() {
            return closed;
        }

        public OutInputStream() {
            initPipe(DEFAULT_PIPE_SIZE);
        }

        public OutInputStream(int pipeSize) {
            initPipe(pipeSize);
        }

        private void initPipe(int pipeSize) {
            if (pipeSize <= 0) {
                throw new IllegalArgumentException("Pipe Size <= 0");
            }

            buffer = new byte[pipeSize];
        }

        protected synchronized void receive(int b) throws IOException {
            checkStateForReceive(0);
            if (in == out)
                awaitSpace();
            if (in < 0) {
                in = 0;
                out = 0;
            }
            buffer[in++] = (byte) (b & 0xFF);
            if (in >= buffer.length) {
                in = 0;
            }
        }

        protected synchronized void receive(byte b[], int off, int len) throws IOException {
            checkStateForReceive(0);
            int bytesToTransfer = len;
            while (bytesToTransfer > 0) {
                if (in == out)
                    awaitSpace();
                int nextTransferAmount = 0;
                if (out < in) {
                    nextTransferAmount = buffer.length - in;
                } else if (in < out) {
                    if (in == -1) {
                        in = out = 0;
                        nextTransferAmount = buffer.length - in;
                    } else {
                        nextTransferAmount = out - in;
                    }
                }
                if (nextTransferAmount > bytesToTransfer)
                    nextTransferAmount = bytesToTransfer;
                assert (nextTransferAmount > 0);
                System.arraycopy(b, off, buffer, in, nextTransferAmount);
                bytesToTransfer -= nextTransferAmount;
                off += nextTransferAmount;
                in += nextTransferAmount;
                if (in >= buffer.length) {
                    in = 0;
                }
            }
        }

        protected void checkStateForReceive(int i) throws IOException {
            if (closed) {
                throw new IOException("OutInputStream is closed");
            }

            if (i > 0) {
                throw new IOException("OutInputStream not enough buffer write");
            }
        }

        private void awaitSpace() throws IOException {
            int i = 0;
            while (in == out) {
                checkStateForReceive(i++);

            /* full: kick any waiting readers */
                notifyAll();
                try {
                    wait(1000);
                } catch (InterruptedException ex) {
                    throw new java.io.InterruptedIOException();
                }
            }
        }

        protected synchronized void receivedLast() {
            notifyAll();
        }

        public synchronized int read() throws IOException {

            int trials = 2;
            while (in < 0) {
                if (closed) {
                /* closed by writer, return EOF */
                    return -1;
                }

            /* might be a writer waiting */
                notifyAll();
                try {
                    wait(1000);
                } catch (InterruptedException ex) {
                    throw new java.io.InterruptedIOException();
                }
            }
            int ret = buffer[out++] & 0xFF;
            if (out >= buffer.length) {
                out = 0;
            }
            if (in == out) {
            /* now empty */
                in = -1;
            }

            return ret;
        }

        public synchronized int read(byte b[], int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            }

        /* possibly wait on the first character */
            int c = read();
            if (c < 0) {
                return -1;
            }
            b[off] = (byte) c;
            int rlen = 1;
            while ((in >= 0) && (len > 1)) {

                int available;

                if (in > out) {
                    available = Math.min((buffer.length - out), (in - out));
                } else {
                    available = buffer.length - out;
                }

                // A byte is read beforehand outside the loop
                if (available > (len - 1)) {
                    available = len - 1;
                }
                System.arraycopy(buffer, out, b, off + rlen, available);
                out += available;
                rlen += available;
                len -= available;

                if (out >= buffer.length) {
                    out = 0;
                }
                if (in == out) {
                /* now empty */
                    in = -1;
                }
            }
            return rlen;
        }

        public synchronized int available() throws IOException {
            if (in < 0)
                return 0;
            else if (in == out)
                return buffer.length;
            else if (in > out)
                return in - out;
            else
                return in + buffer.length - out;
        }

        @Override
        public void close() throws IOException {
            if (!closed) {
                synchronized (this) {
                    closed = true;
                    notifyAll();
                }
            }
        }
    }

    public static class OutInputStreamTimeout extends OutInputStream {

        private int timeout;

        public OutInputStreamTimeout(int timeout) {
            this.timeout = timeout;
        }

        @Override
        protected void checkStateForReceive(int i) throws IOException {
            if (closed) {
                throw new IOException("OutInputStream is closed");
            }

            if (i > timeout) {
                throw new IOException("OutInputStream not enough buffer write");
            }
        }
    }

    public class NextOutputStream extends OutInputStream {

        protected long passTime;

        public NextOutputStream() {
            retainAt();
        }

        public final void retainAt() {
            passTime = UtilContext.getCurrentTime() + idleTime;
        }

        public void write(int b) throws IOException {
            super.receive(b);
            retainAt();
        }

        public void write(byte[] b, int off, int len) throws IOException {
            super.receive(b, off, len);
            retainAt();
        }

        @Override
        public void close() throws IOException {
            if (passTime > 0) {
                size--;
                passTime = 0;
                super.close();
            }
        }
    }

}
