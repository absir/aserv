/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-31 下午12:04:51
 */
package com.absir.server.socket;

import com.absir.client.SocketAdapterSel;
import com.absir.core.util.UtilActivePool;
import com.absir.core.util.UtilPipedStream;
import com.absir.core.util.UtilQueue;

import java.io.Serializable;

public class SocketBuffer {

    private Serializable id;

    private int length;

    private int lengthIndex;

    private byte[] buff;

    private int buffLengthIndex;

    private UtilQueue<byte[]> bufferQueue;

    private int concurrent;

    private int concurrentMax;

    private UtilActivePool activePool;

    private UtilPipedStream pipedStream;

    private Object encryptKey;

    public Serializable getId() {
        return id;
    }

    public void setId(Serializable id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLengthIndex() {
        return lengthIndex;
    }

    public void setLengthIndex(int lengthIndex) {
        this.lengthIndex = lengthIndex;
    }

    public byte[] getBuff() {
        return buff;
    }

    public void setBuff(byte[] buff) {
        this.buff = buff;
    }

    public int getBuffLengthIndex() {
        return buffLengthIndex;
    }

    public void setBuffLengthIndex(int buffLengthIndex) {
        this.buffLengthIndex = buffLengthIndex;
    }

    public void setQueueConcurrent(int queueSize, int max) {
        if (queueSize > 0 && max > 0) {
            bufferQueue = new UtilQueue<byte[]>(queueSize);
            concurrentMax = max;

        } else {
            bufferQueue = null;
            concurrentMax = 0;
        }
    }

    public boolean addBufferQueue(byte[] buff) {
        if (bufferQueue != null) {
            synchronized (this) {
                if (concurrent < concurrentMax) {
                    concurrent++;

                } else {
                    bufferQueue.addElement(buff);
                    return true;
                }
            }
        }

        return false;

    }

    public byte[] readBufferQueue() {
        if (bufferQueue != null) {
            synchronized (this) {
                byte[] buff = bufferQueue.readElement();
                if (buff != null) {
                    return buff;
                }

                if (concurrent > 0) {
                    concurrent--;
                }
            }
        }

        return null;
    }

    public UtilActivePool getActivePool() {
        if (activePool == null) {
            synchronized (this) {
                if (activePool == null) {
                    activePool = new UtilActivePool();
                }
            }
        }

        return activePool;
    }

    protected UtilPipedStream createPipedStream() {
        return new UtilPipedStream(SocketAdapterSel.PIPED_STREAM_TIMEOUT);
    }

    public UtilPipedStream getPipedStream() {
        if (pipedStream == null) {
            synchronized (this) {
                if (pipedStream == null) {
                    pipedStream = createPipedStream();
                }
            }
        }

        return pipedStream;
    }

    public Object getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(Object encryptKey) {
        this.encryptKey = encryptKey;
    }

    public void close() {
        if (activePool != null) {
            activePool.clear();
        }

        if (pipedStream != null) {
            pipedStream.close();
        }
    }
}
