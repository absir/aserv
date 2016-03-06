/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-31 下午12:04:51
 */
package com.absir.server.socket;

import java.io.Serializable;

import com.absir.client.SocketAdapterSel;
import com.absir.core.util.UtilActivePool;
import com.absir.core.util.UtilPipedStream;
import com.absir.core.util.UtilQueue;

/**
 * @author absir
 * 
 */
public class SocketBuffer {

	/** id */
	private Serializable id;

	/** length */
	private int length;

	/** lengthIndex */
	private int lengthIndex;

	/** buff */
	private byte[] buff;

	/** buffLengthIndex */
	private int buffLengthIndex;

	/**
	 * @return the id
	 */
	public Serializable getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Serializable id) {
		this.id = id;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the lengthIndex
	 */
	public int getLengthIndex() {
		return lengthIndex;
	}

	/**
	 * @param lengthIndex
	 *            the lengthIndex to set
	 */
	public void setLengthIndex(int lengthIndex) {
		this.lengthIndex = lengthIndex;
	}

	/**
	 * @return the buff
	 */
	public byte[] getBuff() {
		return buff;
	}

	/**
	 * @param buff
	 *            the buff to set
	 */
	public void setBuff(byte[] buff) {
		this.buff = buff;
	}

	/**
	 * @return the buffLengthIndex
	 */
	public int getBuffLengthIndex() {
		return buffLengthIndex;
	}

	/**
	 * @param buffLengthIndex
	 *            the buffLengthIndex to set
	 */
	public void setBuffLengthIndex(int buffLengthIndex) {
		this.buffLengthIndex = buffLengthIndex;
	}

	/** bufferQueue */
	private UtilQueue<byte[]> bufferQueue;

	/** concurrent */
	private int concurrent;

	/** concurrentMax */
	private int concurrentMax;

	/**
	 * @param queueSize
	 * @param concurrent
	 */
	public void setQueueConcurrent(int queueSize, int max) {
		if (queueSize > 0 && max > 0) {
			bufferQueue = new UtilQueue<byte[]>(queueSize);
			concurrentMax = max;

		} else {
			bufferQueue = null;
			concurrentMax = 0;
		}
	}

	/**
	 * @param buff
	 * @return
	 */
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

	/**
	 * @return
	 */
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

	/** activePool */
	private UtilActivePool activePool;

	/** pipedStream */
	private UtilPipedStream pipedStream;

	/**
	 * @return the activePool
	 */
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

	/**
	 * @return
	 */
	protected UtilPipedStream createPipedStream() {
		return new UtilPipedStream(SocketAdapterSel.PIPED_STREAM_TIMEOUT);
	}

	/**
	 * @return the pipedStream
	 */
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

	/**
	 * 
	 */
	public void close() {
		if (activePool != null) {
			activePool.clear();
		}

		if (pipedStream != null) {
			pipedStream.close();
		}
	}
}
