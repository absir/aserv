/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月2日 上午11:19:48
 */
package com.absir.server.socket.resolver;

import com.absir.client.SocketAdapter;
import com.absir.client.SocketAdapterSel;
import com.absir.client.SocketNIO;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelByte;
import com.absir.core.kernel.KernelLang;
import com.absir.core.util.UtilContext;
import com.absir.core.util.UtilPipedStream;
import com.absir.server.socket.SelSession;
import com.absir.server.socket.SocketBuffer;
import com.absir.server.socket.SocketServer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketBufferResolver implements IBufferResolver {

    protected static long bufferMax = 10240;
    protected boolean varints = true;

    public static long getBufferMax() {
        return bufferMax;
    }

    public static void setBufferMax(long max) {
        if (max < 8) {
            max = 8;
        }

        bufferMax = max;
    }

    public static byte[] createByteBufferFull(IBufferResolver bufferResolver, SocketChannel socketChannel,
                                              int headerLength, byte[] bytes, int offset, int length) {
        ByteBuffer byteBuffer = bufferResolver.createByteBuffer(socketChannel, headerLength, bytes, offset,
                length);
        byte[] headers = bufferResolver.createByteHeader(headerLength, byteBuffer);
        int headerLen = headers == null ? 0 : headers.length;
        int bufferOffset = byteBuffer.arrayOffset();
        int bufferLen = headers == null ? 0 : byteBuffer.limit() - bufferOffset;
        byte[] bufferFull = new byte[headerLen + bufferLen];
        if (headerLen > 0) {
            System.arraycopy(headers, 0, bufferFull, 0, headerLen);
        }

        if (bufferLen > 0) {
            System.arraycopy(byteBuffer.array(), bufferOffset, bufferFull, headerLen, bufferLen);
        }

        return bufferFull;
    }

    public static boolean writeBufferTimeout(SelSession selSession, SocketChannel socketChannel, byte[] bytes) {
        return writeBufferTimeout(selSession, socketChannel, bytes, 0, bytes.length);
    }

    public static boolean writeBufferTimeout(SelSession selSession, SocketChannel socketChannel, byte[] bytes,
                                             int offset, int length) {
        return writeBufferTimeout(selSession, socketChannel, bytes, offset, length, SocketNIO.getWriteTimeout());
    }

    public static boolean writeBufferTimeout(SelSession selSession, SocketChannel socketChannel, byte[] bytes,
                                             int offset, int length, long writeTimeout) {
        synchronized (socketChannel) {
            try {
                SocketNIO.writeTimeout(socketChannel, ByteBuffer.wrap(bytes, offset, length), writeTimeout);
                return true;

            } catch (Throwable e) {
                SocketServer.close(selSession, socketChannel);
            }
        }

        return false;
    }

    public static boolean writeBufferTimeout(SelSession selSession, IBufferResolver bufferResolver,
                                             SocketChannel socketChannel, int headerLength, byte[] bytes, int offset, int length) {
        return writeBufferTimeout(selSession, bufferResolver, socketChannel, headerLength, bytes, offset, length,
                SocketNIO.getWriteTimeout());
    }

    public static boolean writeBufferTimeout(SelSession selSession, IBufferResolver bufferResolver,
                                             SocketChannel socketChannel, int headerLength, byte[] bytes, int offset, int length, long writeTimeout) {
        ByteBuffer byteBuffer = bufferResolver.createByteBuffer(socketChannel, headerLength, bytes, offset,
                length);
        byte[] headers = bufferResolver.createByteHeader(headerLength, byteBuffer);
        synchronized (socketChannel) {
            try {
                if (headers != null) {
                    SocketNIO.writeTimeout(socketChannel, ByteBuffer.wrap(headers), writeTimeout);
                }

                SocketNIO.writeTimeout(socketChannel, byteBuffer, writeTimeout);
                return true;

            } catch (Throwable e) {
                SocketServer.close(selSession, socketChannel);
            }
        }

        return false;
    }

    @Override
    public SocketBuffer createSocketBuff() {
        return new SocketBuffer();
    }

    @Override
    public int readByteBuffer(SocketBuffer socketBuffer, byte[] buffer, int position, int length) {
        for (; position < length; position++) {
            if (socketBuffer.getBuff() == null) {
                int lengthIndex = socketBuffer.getLengthIndex();
                int b = buffer[position];
                int buffLength = socketBuffer.getLength();
                if (lengthIndex == 0) {
                    buffLength += (b & 0x7F);

                } else if (lengthIndex == 1) {
                    buffLength += (b & 0x7F) << 7;

                } else if (lengthIndex == 2) {
                    buffLength += (b & 0x7F) << 14;

                } else {
                    buffLength += (b & 0x7F) << 22;
                }

                socketBuffer.setLength(buffLength);
                socketBuffer.setLengthIndex(++lengthIndex);
                if (lengthIndex == 4 || (b & 0x80) == 0) {
                    if (buffLength > 0 && buffLength < bufferMax) {
                        socketBuffer.setBuffLengthIndex(0);
                        socketBuffer.setBuff(new byte[buffLength]);

                    } else {
                        socketBuffer.setLength(0);
                        socketBuffer.setLengthIndex(0);
                    }
                }

            } else {
                int buffLengthIndex = socketBuffer.getBuffLengthIndex();
                socketBuffer.getBuff()[buffLengthIndex] = buffer[position];
                socketBuffer.setBuffLengthIndex(++buffLengthIndex);
                if (buffLengthIndex >= socketBuffer.getLength()) {
                    break;
                }
            }
        }

        return position;
    }

    @Override
    public void readByteBufferDone(SocketBuffer socketBuffer) {
        socketBuffer.setBuff(null);
        socketBuffer.setLength(0);
        socketBuffer.setLengthIndex(0);
    }

    @Override
    public ByteBuffer createByteBuffer(SocketChannel socketChannel, int headerLength, byte[] bytes, int offset, int length) {
        return ByteBuffer.wrap(bytes, offset, length);
    }

    public boolean isVarints() {
        return varints;
    }

    public void setVarints(boolean varints) {
        this.varints = varints;
    }

    @Override
    public byte[] createByteHeader(int headerLength, ByteBuffer byteBuffer) {
        int length = headerLength + byteBuffer.limit();
        if (varints) {
            if (length > KernelByte.VARINTS_4_LENGTH) {
                throw new RuntimeException("varints buffer size to max = " + length);
            }

            byte[] header = new byte[KernelByte.getVarintsLength(length) + headerLength];
            KernelByte.setVarintsLength(header, 0, length);
            return header;

        } else {
            byte[] header = new byte[4 + headerLength];
            KernelByte.setLength(header, 0, length);
            return header;
        }
    }

    @Override
    public int getPostBufferLen() {
        return SocketAdapterSel.POST_BUFF_LEN;
    }

    @Override
    public void writeInputStream(final SelSession selSession, final KernelLang.ObjectTemplate<Integer> template, final int streamIndex, final InputStream inputStream) {

        UtilContext.getThreadPoolExecutor().execute(new Runnable() {

            @Override
            public void run() {
                try {
                    int indexLength = SocketAdapter.getVarintsLength(streamIndex);

                    SocketChannel socketChannel = selSession.getSocketChannel();
                    int postBuffLen = getPostBufferLen();

                    byte[] sendBuffer = createByteBufferFull(SocketBufferResolver.this,
                            socketChannel, 1 + indexLength + postBuffLen, null, 0, 0);

                    sendBuffer[2] = SocketAdapter.STREAM_FLAG | SocketAdapter.POST_FLAG;

                    SocketAdapter.setVarintsLength(sendBuffer, 3, streamIndex);

                    int length = sendBuffer.length;
                    int offLen = 3 + indexLength;
                    int len;
                    try {
                        while ((len = inputStream.read(sendBuffer, offLen, length)) > 0) {
                            len += offLen - 2;
                            sendBuffer[0] = (byte) ((len & 0x7F) | 0x80);
                            sendBuffer[1] = (byte) ((len >> 7) & 0x7F);

                            if (template.object == null) {
                                try {
                                    SocketNIO.writeTimeout(socketChannel, ByteBuffer.wrap(sendBuffer, 0, len + 2));
                                    continue;

                                } catch (IOException e) {
                                    SocketServer.close(socketChannel);
                                }
                            }

                            break;
                        }

                    } catch (Exception e) {
                        Environment.throwable(e);
                        return;
                    }

                } finally {
                    selSession.getSocketBuffer().getActivePool().remove(streamIndex);
                    UtilPipedStream.closeCloseable(inputStream);
                }
            }
        });
    }
}
