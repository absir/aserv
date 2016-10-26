/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-14 下午5:22:44
 */
package com.absir.core.kernel;

public class KernelByte {

    public static final int VARINTS_1_LENGTH = 0x7F;
    public static final int VARINTS_2_LENGTH = VARINTS_1_LENGTH + (0x7F << 7);
    public static final int VARINTS_3_LENGTH = VARINTS_2_LENGTH + (0x7F << 14);
    public static final int VARINTS_4_LENGTH = VARINTS_3_LENGTH + (0x7F << 22);

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

    public static int getVarintsLength(int varints) {
        if (varints <= VARINTS_1_LENGTH) {
            return 1;
        }

        if (varints <= VARINTS_2_LENGTH) {
            return 2;
        }

        if (varints <= VARINTS_3_LENGTH) {
            return 3;
        }

        return 4;
    }

    public static byte[] getVarintsLengthBytes(int varints) {
        byte[] bytes = new byte[getVarintsLength(varints)];
        setVarintsLength(bytes, 0, varints);
        return bytes;
    }

    public static int getVarintsLength(byte[] destination, int destionationIndex) {
        byte b = destination[destionationIndex];
        int length = b & 0x7F;
        if ((b & 0x80) != 0) {
            b = destination[++destionationIndex];
            length += (b & 0x7F) << 7;
            if ((b & 0x80) != 0) {
                b = destination[++destionationIndex];
                length += (b & 0x7F) << 14;
                if ((b & 0x80) != 0) {
                    b = destination[++destionationIndex];
                    length += (b & 0x7F) << 22;
                }
            }
        }

        return length;
    }

    public static void setVarintsLength(byte[] destination, int destionationIndex, int length) {
        if (length > VARINTS_1_LENGTH) {
            destination[destionationIndex] = (byte) ((length & 0x7F) | 0x80);
            if (length > VARINTS_2_LENGTH) {
                destination[++destionationIndex] = (byte) (((length >> 7) & 0x7F) | 0x80);
                if (length > VARINTS_3_LENGTH) {
                    destination[++destionationIndex] = (byte) (((length >> 14) & 0x7F) | 0x80);
                    destination[++destionationIndex] = (byte) ((length >> 22) & 0x7F);

                } else {
                    destination[++destionationIndex] = (byte) ((length >> 14) & 0x7F);
                }

            } else {
                destination[++destionationIndex] = (byte) ((length >> 7) & 0x7F);
            }

        } else {
            destination[destionationIndex] = (byte) (length & 0x7F);
        }
    }

}
