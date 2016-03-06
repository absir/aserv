/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年4月7日 上午11:20:38
 */
package com.absir.client;

import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelLang.ObjectEntry;

/**
 * @author absir
 *
 */
public class SocketAdapter {

	/** LOGGER */
	protected static final Logger LOGGER = LoggerFactory.getLogger(SocketAdapter.class);

	/**
	 * @author absir
	 *
	 */
	public static interface CallbackAdapte {

		/**
		 * @param adapter
		 * @param offset
		 * @param buffer
		 */
		public void doWith(SocketAdapter adapter, int offset, byte[] buffer);
	}

	/** bit */
	public static final byte[] bit = "b".getBytes();

	/** ok */
	public static final byte[] ok = "ok".getBytes();

	/** failed */
	public static final byte[] failed = "failed".getBytes();

	/** retryConnect */
	private int retryConnect;

	/** socket */
	private Socket socket;

	/** beats */
	private byte[] beats = bit;

	/** beatLifeTime */
	private long beatLifeTime;

	/** callbackConnect */
	private CallbackAdapte callbackConnect;

	/** callbackDisconnect */
	private CallbackAdapte callbackDisconnect;

	/** acceptCallback */
	private CallbackAdapte acceptCallback;

	/** registered */
	protected boolean registered;

	/** registerCallback */
	private CallbackAdapte registerCallback;

	/** registeredCallback */
	protected LinkedList<RegisteredRunable> registeredRunables = new LinkedList<RegisteredRunable>();

	/** callbackIndex */
	private int callbackIndex;

	/** receiveCallback */
	protected CallbackAdapte receiveCallback;

	/** receiveCallbacks */
	protected Map<Integer, ObjectEntry<CallbackAdapte, CallbackTimeout>> receiveCallbacks = new HashMap<Integer, ObjectEntry<CallbackAdapte, CallbackTimeout>>();

	/** receiveStarted */
	protected boolean receiveStarted;

	/**
	 * @param e
	 */
	public static void printException(Throwable e) {
		printException(e);
	}

	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * @param socket
	 *            the socket to set
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	/**
	 * @return the beats
	 */
	public byte[] getBeats() {
		return beats;
	}

	/**
	 * @param beats
	 *            the beats to set
	 */
	public void setBeats(byte[] beats) {
		this.beats = beats;
	}

	/**
	 * @return the beatLifeTime
	 */
	public long getBeatLifeTime() {
		return beatLifeTime;
	}

	/**
	 * @param beatLifeTime
	 *            the beatLifeTime to set
	 */
	public void setBeatLifeTime(long beatLifeTime) {
		this.beatLifeTime = beatLifeTime;
	}

	/**
	 * @return the callbackConnect
	 */
	public CallbackAdapte getCallbackConnect() {
		return callbackConnect;
	}

	/**
	 * @param callbackConnect
	 *            the callbackConnect to set
	 */
	public void setCallbackConnect(CallbackAdapte callbackConnect) {
		this.callbackConnect = callbackConnect;
	}

	/**
	 * @return the callbackDisconnect
	 */
	public CallbackAdapte getCallbackDisconnect() {
		return callbackDisconnect;
	}

	/**
	 * @param callbackDisconnect
	 *            the callbackDisconnect to set
	 */
	public void setCallbackDisconnect(CallbackAdapte callbackDisconnect) {
		this.callbackDisconnect = callbackDisconnect;
	}

	/**
	 * @return the acceptCallback
	 */
	public CallbackAdapte getAcceptCallback() {
		return acceptCallback;
	}

	/**
	 * @param acceptCallback
	 *            the acceptCallback to set
	 */
	public void setAcceptCallback(CallbackAdapte acceptCallback) {
		this.acceptCallback = acceptCallback;
	}

	/**
	 * @return the registerCallback
	 */
	public CallbackAdapte getRegisterCallback() {
		return registerCallback;
	}

	/**
	 * @param registerCallback
	 *            the registerCallback to set
	 */
	public void setRegisterCallback(CallbackAdapte registerCallback) {
		this.registerCallback = registerCallback;
	}

	/**
	 * @return the registered
	 */
	public boolean isRegistered() {
		return registered;
	}

	/**
	 * @param registered
	 *            the registered to set
	 */
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	/**
	 * @return the registeredRunables
	 */
	public LinkedList<RegisteredRunable> getRegisteredRunables() {
		return registeredRunables;
	}

	/**
	 * @return the callbackIndex
	 */
	public int getCallbackIndex() {
		return callbackIndex;
	}

	/**
	 * @return the receiveCallback
	 */
	public CallbackAdapte getReceiveCallback() {
		return receiveCallback;
	}

	/**
	 * @param receiveCallback
	 *            the receiveCallback to set
	 */
	public void setReceiveCallback(CallbackAdapte receiveCallback) {
		this.receiveCallback = receiveCallback;
	}

	/**
	 * @return the receiveCallbacks
	 */
	public Map<Integer, ObjectEntry<CallbackAdapte, CallbackTimeout>> getReceiveCallbacks() {
		return receiveCallbacks;
	}

	/**
	 * @param callbackIndex
	 * @param callbackAdapte
	 * @return
	 */
	public CallbackTimeout putReceiveCallbacks(int callbackIndex, int timeout, CallbackAdapte callbackAdapte) {
		ObjectEntry<CallbackAdapte, CallbackTimeout> entry = new ObjectEntry<CallbackAdapte, CallbackTimeout>(
				callbackAdapte, null);
		CallbackTimeout callbackTimeout = null;
		if (timeout > 0 && callbackIndex > getMinCallbackIndex()) {
			callbackTimeout = new CallbackTimeout();
			callbackTimeout.timeout = System.currentTimeMillis() + timeout;
			callbackTimeout.socketAdapter = this;
			callbackTimeout.callbackIndex = callbackIndex;
			addCallbackTimeout(callbackTimeout);
			entry.setValue(callbackTimeout);
		}

		receiveCallbacks.put(callbackIndex, entry);
		return callbackTimeout;
	}

	/**
	 * @return
	 */
	public int getMinCallbackIndex() {
		return 2048;
	}

	/**
	 * @return
	 */
	public int getMaxBufferLength() {
		return 204800;
	}

	/**
	 * @return
	 */
	public synchronized int generateCallbackIndex() {
		int minCallbackIndex = getMinCallbackIndex();
		while (true) {
			if (++callbackIndex < minCallbackIndex || callbackIndex >= Integer.MAX_VALUE) {
				callbackIndex = minCallbackIndex + 1;
			}

			if (!receiveCallbacks.containsKey(callbackIndex)) {
				break;
			}
		}

		return callbackIndex;
	}

	/**
	 * 
	 */
	public void clearRetryConnect() {
		retryConnect = 0;
	}

	/**
	 * @return
	 */
	public boolean isRetryConnectMax() {
		return retryConnect >= 3;
	}

	/**
	 * 开始连接
	 */
	public void connect() {
		if (socket != null && (beatLifeTime <= System.currentTimeMillis() || !socket.isConnected())) {
			disconnect(socket);
		}

		if (socket == null && callbackConnect != null) {
			synchronized (this) {
				if (socket == null && !isRetryConnectMax()) {
					retryConnect++;
					callbackConnect.doWith(this, 0, null);
					if (socket != null) {
						waiteAccept();
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	public void close() {
		if (socket != null) {
			try {
				socket.close();

			} catch (Exception e) {
			}
		}

		socket = null;
		registered = false;
		receiveStarted = false;
	}

	/**
	 * 断开连接
	 * 
	 * @param st
	 */
	public void disconnect(Socket st) {
		if (st == null || st == socket) {
			synchronized (this) {
				if (st == null || st == socket) {
					close();
					if (Environment.isActive() && callbackDisconnect != null) {
						callbackDisconnect.doWith(this, 0, null);
					}
				}
			}
		}
	}

	/**
	 * 最新心跳
	 */
	public void lastedBeat() {
		beatLifeTime = System.currentTimeMillis() + 60000;
	}

	/**
	 * 接受心跳
	 */
	public void recieverBeat() {
		lastedBeat();
	}

	/**
	 * 等待连接
	 */
	public void waiteAccept() {
		lastedBeat();
	}

	/**
	 * 最新登录
	 */
	public boolean lastedResgiter() {
		if (registered) {
			clearRetryConnect();
			lastedBeat();
			// 执行登录等待请求
			if (!registeredRunables.isEmpty()) {
				try {
					List<RegisteredRunable> runnables = registeredRunables;
					registeredRunables = new LinkedList<RegisteredRunable>();
					boolean failed = false;
					Socket st = socket;
					for (RegisteredRunable runnable : runnables) {
						if (failed) {
							if (runnable.removed) {

							} else {
								registeredRunables.add(runnable);
							}

						} else {
							runnable.run();
							failed = runnable.failed;
							if (failed) {
								registeredRunables.add(runnable);
								disconnect(st);
							}
						}
					}

				} catch (Throwable e) {
					printException(e);
				}
			}

			return true;
		}

		return false;
	}

	/** lengthIndex */
	protected int lengthIndex;

	/** buffLength */
	protected int buffLength;

	/** buffer */
	protected byte[] buff;

	/** buffLengthIndex */
	protected int buffLengthIndex;

	/**
	 * 接收数据
	 * 
	 * @param st
	 * @param buffer
	 * @param off
	 * @param len
	 */
	public void receiveByteBuffer(Socket st, byte[] buffer, int off, int len) {
		if (st != socket) {
			return;
		}

		for (; off < len; off++) {
			if (buff == null) {
				if (lengthIndex < 4) {
					int length = buffer[off] & 0xFF;
					if (lengthIndex > 0) {
						length = buffLength + (length << (8 * lengthIndex));
					}

					buffLength = length;
					lengthIndex++;
					if (lengthIndex == 4) {
						if (buffLength >= 0 && buffLength < getMaxBufferLength()) {
							buff = new byte[buffLength];
							buffLengthIndex = 0;
							if (buffLength == 0) {
								receiveBuffDone();
							}

						} else {
							lengthIndex = 0;
							buffLength = 0;
						}
					}
				}

			} else {
				buff[buffLengthIndex] = buffer[off];
				if (++buffLengthIndex >= buffLength) {
					receiveBuffDone();
					continue;
				}
			}
		}
	}

	/**
	 * 清除数据
	 */
	protected final void clearReceiveBuff() {
		lengthIndex = 0;
		buffLength = 0;
		buff = null;
	}

	/**
	 * 接收完成
	 */
	public void receiveBuffDone() {
		byte[] buffer = buff;
		clearReceiveBuff();
		receiveBuffDone(buffer);
	}

	/** acceptSocket */
	private Socket acceptSocket;

	/**
	 * 接收完成数据
	 * 
	 * @param buffer
	 */
	public void receiveBuffDone(byte[] buffer) {
		int length = buffer.length;
		// 检测心跳
		if (beats != null && beats.length == buffer.length) {
			int i;
			for (i = 0; i < length; i++) {
				if (buffer[i] != beats[i]) {
					break;
				}
			}

			if (i >= length) {
				recieverBeat();
				return;
			}
		}

		// 接收请求
		if (acceptSocket != socket) {
			acceptSocket = socket;
			if (acceptCallback != null) {
				acceptCallback.doWith(this, 0, buffer);
				lastedResgiter();
				return;
			}
		}

		// 注册请求
		if (!registered) {
			registerCallback.doWith(this, 0, buffer);
			lastedResgiter();
			return;
		}

		int offset;
		byte flag;
		if (length == 0) {
			offset = 0;
			flag = 0;

		} else {
			offset = 1;
			flag = buffer[0];
		}

		receiveCallback(offset, buffer, flag);
	}

	/**
	 * @param offset
	 * @param buffer
	 * @param flag
	 */
	public void receiveCallback(int offset, byte[] buffer, byte flag) {
		// 转发请求
		int length = buffer.length;
		Integer callbackIndex = null;
		if (length > 4 && (flag & CALLBACK_FLAG) != 0) {
			offset += 4;
			int index = buffer[1] & 0xFF;
			index += (buffer[2] & 0xFF) << 8;
			index += (buffer[3] & 0xFF) << 16;
			index += (buffer[4] & 0xFF) << 24;
			callbackIndex = index;
		}

		receiveCallback(offset, buffer, flag, callbackIndex);
	}

	/**
	 * 接收数据回调
	 * 
	 * @param offset
	 * @param buffer
	 * @param flag
	 * @param callbackIndex
	 */
	public void receiveCallback(int offset, byte[] buffer, byte flag, Integer callbackIndex) {
		if (callbackIndex != null) {
			boolean minCallback = callbackIndex <= getMinCallbackIndex();
			ObjectEntry<CallbackAdapte, CallbackTimeout> entry = minCallback ? receiveCallbacks.get(callbackIndex)
					: receiveCallbacks.remove(callbackIndex);
			if (entry != null) {
				if (minCallback) {
					entry.getKey().doWith(this, offset, buffer);

				} else {
					synchronized (entry) {
						CallbackAdapte callbackAdapte = entry.getKey();
						if (callbackAdapte != null) {
							entry.setKey(null);
							CallbackTimeout callbackTimeout = entry.getValue();
							if (callbackTimeout != null) {
								callbackTimeout.socketAdapter = null;
							}

							callbackAdapte.doWith(this, offset, buffer);
						}
					}
				}

				return;
			}
		}

		if (buffer != null && receiveCallback != null) {
			receiveCallback(receiveCallback, offset, buffer, flag, callbackIndex);
		}
	}

	/**
	 * @param callbackAdapte
	 * @param offset
	 * @param buffer
	 * @param flag
	 * @param callbackIndex
	 */
	public void receiveCallback(CallbackAdapte callbackAdapte, int offset, byte[] buffer, byte flag,
			Integer callbackIndex) {
		callbackAdapte.doWith(this, offset, buffer);
	}

	/** STREAM_FLAG */
	public static final byte STREAM_FLAG = 0x01;

	/** STREAM_OFF_FLAG */
	public static final byte STREAM_CLOSE_FLAG = 0x01 << 1;

	/** RESPONSE_FLAG */
	public static final byte RESPONSE_FLAG = 0x01 << 2;

	/** ERROR_FLAG */
	public static final byte ERROR_FLAG = 0x01 << 3;

	/** POST_FLAG */
	public static final byte POST_FLAG = 0x01 << 4;

	/** CALLBACK_FLAG */
	public static final byte CALLBACK_FLAG = 0x01 << 5;

	/** DEBUG_FLAG */
	public static final byte DEBUG_FLAG = 0x01 << 6;

	/**
	 * 生成发送数据包
	 * 
	 * @param dataBytes
	 * @param head
	 * @param debug
	 * @param callbackIndex
	 * @param postData
	 * @return
	 */
	public byte[] sendDataBytes(byte[] dataBytes, boolean head, boolean debug, int callbackIndex, byte[] postData) {
		return sendDataBytes(0, dataBytes, head, debug, callbackIndex, postData);
	}

	/**
	 * 生成发送数据包
	 * 
	 * @param off
	 * @param dataBytes
	 * @param head
	 * @param debug
	 * @param callbackIndex
	 * @param postData
	 * @return
	 */
	public byte[] sendDataBytes(int off, byte[] dataBytes, boolean head, boolean debug, int callbackIndex,
			byte[] postData) {
		return sendDataBytes(off, dataBytes, head, debug, 0, callbackIndex, postData);
	}

	/**
	 * 生成发送数据包
	 * 
	 * @param off
	 * @param dataBytes
	 * @param head
	 * @param debug
	 * @param flag
	 * @param callbackIndex
	 * @param postData
	 * @return
	 */
	public byte[] sendDataBytes(int off, byte[] dataBytes, boolean head, boolean debug, int flag, int callbackIndex,
			byte[] postData) {
		return sendDataBytes(off, dataBytes, 0, dataBytes == null ? 0 : dataBytes.length, head, debug, flag,
				callbackIndex, postData, 0, postData == null ? 0 : postData.length);
	}

	/**
	 * @param off
	 * @param dataBytes
	 * @param offset
	 * @param length
	 * @param head
	 * @param debug
	 * @param flag
	 * @param callbackIndex
	 * @param postData
	 * @return
	 */
	public byte[] sendDataBytes(int off, byte[] dataBytes, int dataOff, int dataLen, boolean head, boolean debug,
			int flag, int callbackIndex, byte[] postData, int postOff, int postLen) {
		byte headFlag = 0x00;
		int headLength = off + (callbackIndex == 0 ? 4 : 8);
		if (head) {
			headLength++;
		} else if (callbackIndex != 0) {
			head = true;
			headLength++;
		}

		int dataLength = dataLen - dataOff;
		byte[] sendDataBytes;
		if (postData == null) {
			// no post
			dataLength += headLength;
			sendDataBytes = new byte[dataLength];
			if (dataBytes != null) {
				System.arraycopy(dataBytes, dataOff, sendDataBytes, headLength, dataLength - headLength);
			}

		} else {
			// post head
			if (!head) {
				head = true;
				headLength++;
			}

			headFlag |= POST_FLAG;
			headLength += 4;
			int postLength = postLen - postOff;
			dataLength += headLength + postLength;
			sendDataBytes = new byte[dataLength];
			if (dataBytes != null) {
				System.arraycopy(dataBytes, dataOff, sendDataBytes, headLength, dataLength - headLength - postLength);
			}

			System.arraycopy(postData, postOff, sendDataBytes, dataLength - postLength, postLength);
			sendDataBytes[headLength - 4] = (byte) postLength;
			sendDataBytes[headLength - 3] = (byte) (postLength >> 8);
			sendDataBytes[headLength - 2] = (byte) (postLength >> 16);
			sendDataBytes[headLength - 1] = (byte) (postLength >> 24);
		}

		// headFlag
		if (head) {
			if (debug) {
				headFlag |= DEBUG_FLAG;
			}

			if (callbackIndex != 0) {
				headFlag |= CALLBACK_FLAG;
				sendDataBytes[off + 5] = (byte) callbackIndex;
				sendDataBytes[off + 6] = (byte) (callbackIndex >> 8);
				sendDataBytes[off + 7] = (byte) (callbackIndex >> 16);
				sendDataBytes[off + 8] = (byte) (callbackIndex >> 24);
			}

			headFlag |= flag;
			sendDataBytes[off + 4] = headFlag;
		}

		// send data bytes length
		dataLength -= 4;
		sendDataBytes[0] = (byte) dataLength;
		sendDataBytes[1] = (byte) (dataLength >> 8);
		sendDataBytes[2] = (byte) (dataLength >> 16);
		sendDataBytes[3] = (byte) (dataLength >> 24);
		return sendDataBytes;
	}

	/**
	 * @author absir
	 *
	 */
	public static abstract class RegisteredRunable {

		/** failed */
		protected boolean failed;

		/** removed */
		protected boolean removed;

		/**
		 * 
		 */
		public void run() {
			if (!removed) {
				doRun();
			}
		}

		/**
		 * 执行
		 */
		protected abstract void doRun();
	}

	/**
	 * @param buffer
	 * @return
	 */
	public boolean sendData(byte[] buffer) {
		return sendData(buffer, 0, buffer.length);
	}

	/**
	 * @param buffer
	 * @param offset
	 * @param length
	 * @return
	 */
	public boolean sendData(byte[] buffer, int offset, int length) {
		Socket sendSocket = socket;
		if (sendSocket != null) {
			try {
				sendSocket.getOutputStream().write(buffer);
				return true;

			} catch (Exception e) {
				printException(e);
			}

			disconnect(sendSocket);
		}

		return false;
	}

	/**
	 * @param dataBytes
	 * @param head
	 * @param debug
	 * @param callbackIndex
	 * @param postData
	 * @return
	 */
	protected RegisteredRunable sendData(byte[] dataBytes, boolean head, boolean debug, int callbackIndex,
			byte[] postData) {
		connect();
		final byte[] buffer = sendDataBytes(dataBytes, head, debug, callbackIndex, postData);
		if (registered && sendData(buffer)) {
			return null;
		}

		RegisteredRunable runnable = new RegisteredRunable() {

			@Override
			public void doRun() {
				failed = !sendData(buffer);
			}
		};

		registeredRunables.add(runnable);
		lastedResgiter();
		return runnable;
	}

	/** receiveSocket */
	protected Socket receiveSocket;

	/**
	 * @param socketChannel
	 */
	protected void receiveSocketChannel() {
		receiveSocket = socket;
		clearReceiveBuff();
		try {
			int buffLength = 0;
			byte[] buffer = new byte[256];
			InputStream inputStream = receiveSocket.getInputStream();
			while (Environment.isActive() && receiveSocket == socket && (buffLength = inputStream.read(buffer)) > 0) {
				receiveByteBuffer(receiveSocket, buffer, 0, buffLength);
			}

		} catch (Exception e) {
			printException(e);
		}

		disconnect(receiveSocket);
	}

	/**
	 * 接收数据线程开启
	 */
	public synchronized void receiveSocketChannelStart() {
		if (receiveStarted) {
			return;
		}

		receiveStarted = true;
		Thread thread = new Thread() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				receiveSocketChannel();
			}
		};

		// 设置为守护线程
		thread.setName("SocketAdapter.receiveSocketChannelStart");
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * @author absir
	 *
	 */
	public static class CallbackTimeout {

		/** timeout */
		public long timeout;

		/** registeredRunable */
		public RegisteredRunable registeredRunable;

		/** socketAdapter */
		public SocketAdapter socketAdapter;

		/** callbackIndex */
		public int callbackIndex;

		/**
		 * 超时执行
		 */
		public void run() {
			if (registeredRunable != null) {
				registeredRunable.removed = true;
			}

			if (socketAdapter != null) {
				try {
					socketAdapter.receiveCallback(0, null, (byte) 0, callbackIndex);

				} catch (Throwable e) {
					LOGGER.error("socket adapter timeout run", e);
				}
			}
		}
	}

	/**
	 * @author absir
	 *
	 */
	protected static class TimeoutThread extends Thread {

		/** addTimeouts */
		private final List<CallbackTimeout> addTimeouts = new ArrayList<CallbackTimeout>();

		/** 超时执行队列 */
		private final List<CallbackTimeout> callbackTimeouts = new LinkedList<CallbackTimeout>();

		/**
		 * @param timeout
		 */
		public synchronized void add(CallbackTimeout timeout) {
			addTimeouts.add(timeout);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			try {
				// 超时执行检测
				long contextTime;
				Iterator<CallbackTimeout> iterator;
				CallbackTimeout callbackTimeout;
				while (Environment.isActive()) {
					Thread.sleep(5000);
					contextTime = System.currentTimeMillis();
					if (!addTimeouts.isEmpty()) {
						synchronized (this) {
							callbackTimeouts.addAll(addTimeouts);
							addTimeouts.clear();
						}
					}

					iterator = callbackTimeouts.iterator();
					while (iterator.hasNext()) {
						callbackTimeout = iterator.next();
						if (callbackTimeout.socketAdapter == null || callbackTimeout.timeout <= contextTime) {
							callbackTimeout.run();
							iterator.remove();
						}
					}
				}

			} catch (InterruptedException e) {
			}
		}
	}

	/** timeoutThread */
	private static TimeoutThread timeoutThread;

	/**
	 * 开启超时线程
	 */
	public static TimeoutThread startTimeout() {
		TimeoutThread thead = timeoutThread;
		if (thead == null) {
			synchronized (SocketAdapter.class) {
				if (timeoutThread == null) {
					timeoutThread = new TimeoutThread();
					timeoutThread.setName("SocketAdapter.TimeoutThread");
					timeoutThread.setDaemon(true);
					timeoutThread.start();
				}

				thead = timeoutThread;
			}
		}

		return thead;
	}

	/**
	 * 关闭超时线程
	 */
	public static void stopTimeout() {
		if (timeoutThread != null) {
			synchronized (SocketAdapter.class) {
				if (timeoutThread != null) {
					timeoutThread.interrupt();
					timeoutThread = null;
				}
			}
		}
	}

	/**
	 * 添加超时回调
	 * 
	 * @param callbackTimeout
	 */
	public static void addCallbackTimeout(CallbackTimeout callbackTimeout) {
		startTimeout().add(callbackTimeout);
	}

	/**
	 * 发送回调方法
	 * 
	 * @param dataBytes
	 * @param head
	 * @param debug
	 * @param postData
	 * @param timeout
	 * @param callbackAdapte
	 */
	public void sendData(byte[] dataBytes, boolean head, boolean debug, byte[] postData, int timeout,
			CallbackAdapte callbackAdapte) {
		sendDataIndex(generateCallbackIndex(), dataBytes, head, debug, postData, timeout, callbackAdapte);
	}

	/**
	 * 发送目标数据
	 * 
	 * @param callbackIndex
	 * @param dataBytes
	 * @param head
	 * @param debug
	 * @param postData
	 * @param timeout
	 * @param callbackAdapte
	 */
	public void sendDataIndex(int callbackIndex, byte[] dataBytes, boolean head, boolean debug, byte[] postData,
			int timeout, CallbackAdapte callbackAdapte) {
		CallbackTimeout callbackTimeout = null;
		if (callbackAdapte != null) {
			callbackTimeout = putReceiveCallbacks(callbackIndex, timeout, callbackAdapte);
		}

		RegisteredRunable registeredRunable = sendData(dataBytes, head, debug, callbackIndex, postData);
		if (callbackTimeout != null) {
			callbackTimeout.registeredRunable = registeredRunable;
		}
	}
}
