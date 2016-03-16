/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-14 下午5:22:44
 */
package com.absir.core.kernel;

public class KernelByte {

    public static int getLength(byte[] destination, int destionationIndex) {
        int length = destination[destionationIndex] & 0xFF;
        length += (destination[++destionationIndex] & 0xFF) << 8;
        length += (destination[++destionationIndex] & 0xFF) << 16;
        length += (destination[++destionationIndex] & 0xFF) << 24;
        return length;
    }

    public static void setLength(byte[] destination, int destionationIndex, int length) {
        destination[destionationIndex] = (byte) (length);
        destination[++destionationIndex] = (byte) (length >> 8);
        destination[++destionationIndex] = (byte) (length >> 16);
        destination[++destionationIndex] = (byte) (length >> 24);
    }

    public static byte[] getLengthBytes(int length) {
        byte[] destination = new byte[4];
        setLength(destination, 0, length);
        return destination;
    }

    public static final void copy(byte[] source, byte[] destination, int sourceIndex, int destionationIndex,
                                  int length) {
        System.arraycopy(source, sourceIndex, destination, destionationIndex, length);
    }

}
