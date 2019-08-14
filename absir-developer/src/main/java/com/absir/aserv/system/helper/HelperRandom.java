/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-28 下午4:49:18
 */
package com.absir.aserv.system.helper;

import com.absir.client.helper.HelperEncrypt;
import com.absir.core.kernel.KernelByte;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.FilterTemplate;
import com.absir.core.kernel.KernelObject;

import java.awt.*;
import java.security.SecureRandom;
import java.util.List;
import java.util.*;

public class HelperRandom {

    public static final Random RANDOM = new Random(System.currentTimeMillis());

    public static final SecureRandom SECURE_RANDOM = new SecureRandom(Long.toHexString(System.currentTimeMillis()).getBytes());

    private static final int SECEND_SIZE = 3;

    //abcdefghijklmnopqrstuvwxyz
    private static final char[] DIG_LETTER_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private static final char[] DIG_LETTER_M_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public static int nextInt(int max) {
        return RANDOM.nextInt(max);
    }

    public static int nextInt(int min, int max) {
        max -= min;
        max = max > 0 ? RANDOM.nextInt(max) : 0;
        return min + max;
    }

    public static int randInt(int rnd, int began, int end) {
        if (rnd < began) {
            rnd = began;

        } else if (rnd > end) {
            rnd = end;
        }

        return rnd;
    }

    public static int randInt(int min, int max, int began, int end) {
        int rnd = nextInt(min, max);
        return randInt(rnd, began, end);
    }

    public static <T> T randElement(Collection<? extends T> collection) {
        int size = collection.size();
        size = nextInt(size);
        if (collection instanceof List) {
            return ((List<? extends T>) collection).get(size);

        } else {
            for (T element : collection) {
                if (size-- == 0) {
                    return element;
                }
            }
        }

        return null;
    }

    public static <T> T randList(List<? extends T> collection) {
        int size = collection.size();
        return collection.remove(nextInt(size));
    }

    public static float getTotal(float[] rares) {
        float total = 0;
        for (float rare : rares) {
            total += rare;
        }

        return total;
    }

    public static float getTotal(Collection<Float> rares) {
        float total = 0;
        for (float rare : rares) {
            total += rare;
        }

        return total;
    }

    public static float[] getProbabilities(float[] rares) {
        if (rares == null) {
            return null;
        }

        float total = getTotal(rares);
        int last = rares.length;
        float[] probabilities = new float[last];
        if (total != 0) {
            for (last--; last >= 0; last--) {
                probabilities[last] = rares[last] / total;
            }
        }

        return probabilities;
    }

    public static float[] getProbabilities(List<Float> rares) {
        if (rares == null) {
            return null;
        }

        float total = getTotal(rares);
        int last = rares.size();
        float[] probabilities = new float[last];
        if (total != 0) {
            for (last--; last >= 0; last--) {
                probabilities[last] = rares.get(last) / total;
            }
        }

        return probabilities;
    }

    public static int randIndex(float[] probabilities) {
        float total = RANDOM.nextFloat();
        int i = probabilities.length;
        while (i-- > 0 && (total -= probabilities[i]) > 0)
            ;
        return i;
    }

    public static int randIndex(float[] probabilities, float total) {
        total *= RANDOM.nextFloat();
        int i = probabilities.length;
        while (i-- > 0 && (total -= probabilities[i]) > 0)
            ;
        return i;
    }

    public static int randIndex(List<Float> probabilities, float total) {
        total *= RANDOM.nextFloat();
        int i = probabilities.size();
        while (i-- > 0 && (total -= probabilities.get(i)) > 0)
            ;
        return i;
    }

    public static Color randColor(int b, int e) {
        return randColor(b, e, b);
    }

    public static Color randColor(int b, int e, int a) {
        return randColor(b, e, b, e, b, e, a, e);
    }

    public static Color randColor(int rb, int re, int gb, int ge, int bb, int be, int ab, int ae) {
        rb = randInt(rb, re, 0, 255);
        gb = randInt(gb, ge, 0, 255);
        bb = randInt(bb, be, 0, 255);
        if (ab < 0) {
            return new Color(rb, gb, bb);

        } else {
            ab = randInt(ab, ae, 0, 255);
        }

        return new Color(rb, gb, bb, ab);
    }

    public static String randChars(int size) {
        return randChars(size, 0);
    }

    public static String randChars(int size, int type) {
        int rb = (type & 0x00) == 0 ? 0 : 10;
        int re = (type & 0x01) == 0 ? 26 : 10;
        if (rb == 0) {
            re += 10;

        } else if (re == 10) {
            re = 26;
        }

        int lower = (type & 0x02) == 0 ? 65 : 96;
        StringBuilder buffer = new StringBuilder();
        for (; size > 0; size--) {
            int rnd = rb + RANDOM.nextInt(re);
            if (rnd < 10) {
                buffer.append(rnd);

            } else {
                buffer.append((char) (rnd - 10 + lower));
            }
        }

        return buffer.toString();
    }

    public static void appendFormat(StringBuilder stringBuilder, int size, char[] chars) {
        appendFormat(stringBuilder, 0, size, chars);
    }

    public static void appendFormat(StringBuilder stringBuilder, int offset, int size, char[] chars) {
        int length = chars.length;
        if (length <= size) {
            for (; length < size; size--) {
                stringBuilder.append('0');
            }

            stringBuilder.append(chars);

        } else {
            if (offset != 0) {
                int _off = length - size;
                if (offset < 0 || _off < offset) {
                    offset = _off;
                }
            }

            stringBuilder.append(chars, offset, size);
        }
    }

    public static void appendFormat(StringBuilder stringBuilder, IFormatType type, int i) {
        appendFormat(stringBuilder, type, i, 0, type.intLen());
    }

    public static void appendFormat(StringBuilder stringBuilder, IFormatType type, int i, int offset, int size) {
        appendFormat(stringBuilder, offset, size, type.charsForInt(i));
    }

    public static void appendFormatLong(StringBuilder stringBuilder, IFormatType type, long l) {
        appendFormatLong(stringBuilder, type, l, 0, type.longLen());
    }

    public static void appendFormatLong(StringBuilder stringBuilder, IFormatType type, long l, int offset, int size) {
        appendFormat(stringBuilder, offset, size, type.charsForLong(l));
    }

    public static void appendFormatLongMd5(StringBuilder stringBuilder, IFormatType type, long l, int size) {
        appendFormatLongMd5(stringBuilder, type, Long.toHexString(l).getBytes(), size);
    }

    static byte[] srKey = HelperEncrypt.getSROREncryptKey("*&^()!@#$~_+<>?");

    public static void appendFormatSrLong(StringBuilder sb, long l, boolean time, IFormatType type) {
        byte[] bytes;
        if (time || l <= 0) {
            bytes = KernelByte.getLongBytes(l <= 0 ? System.currentTimeMillis() : l);
            bytes[0] = (byte) RANDOM.nextInt(255);
            bytes[1] = (byte) RANDOM.nextInt(255);

        } else {
            bytes = KernelByte.getLongBytes(l);
        }

        HelperEncrypt.decryptSRORKey(bytes, srKey);
        appendFormatLong(sb, type, KernelByte.getLong(bytes, 0));
    }

    public static void appendFormatLongMd5(StringBuilder stringBuilder, IFormatType type, byte[] bytes, int size) {
        char[] md5 = HelperEncrypt.encryptionMD5Chars(bytes);
        if (size > type.longLen()) {
            if (md5[16] > '7') {
                md5[16] = (char) ('0' + md5[16] % 7);
            }

            appendFormatLong(stringBuilder, type, Long.parseLong(new String(md5, 16, 16), 16));
            size -= type.longLen();
        }

        if (md5[0] > '7') {
            md5[0] = (char) ('0' + md5[0] % 7);
        }

        appendFormatLong(stringBuilder, type, Long.parseLong(new String(md5, 0, 16), 16), 0, size);
    }

    public static void randAppendFormat(StringBuilder stringBuilder, int size) {
        randAppendFormat(stringBuilder, size, FormatType.HEX);
    }

    public static void randAppendFormat(StringBuilder stringBuilder, int size, IFormatType type) {
        while (size > 0) {
            char[] chars = size <= type.intLen() ? type.charsForInt(RANDOM.nextInt()) : type.charsForLong(RANDOM.nextLong());
            int length = chars.length;
            if (length < size) {
                if (size <= type.intLen()) {
                    length = type.intLen();

                } else {
                    length = type.longLen();
                }

            } else {
                length = size;
            }

            appendFormat(stringBuilder, length, chars);
            size -= length;
        }
    }

    public static String randSecondId() {
        return randSecondId(SECEND_SIZE);
    }

    public static String randSecondId(int size) {
        return randSecondId(System.currentTimeMillis(), size);
    }

    public static String randSecondId(long time, int size) {
        return randSecondBuilder(time, size).toString();
    }

    public static StringBuilder randSecondBuilder(long time, int size) {
        return randSecondBuilder(time, size, FormatType.HEX);
    }

    public static StringBuilder randSecondBuilder(long time, int size, IFormatType formatType) {
        StringBuilder stringBuilder = new StringBuilder();
        appendFormatLong(stringBuilder, formatType, time);
        randAppendFormat(stringBuilder, size, formatType);
        return stringBuilder;
    }

    public static String randSecondId(long time, int size, int id) {
        return randSecondId(time, size, id, FormatType.HEX);
    }

    public static String randSecondId(long time, int size, int id, IFormatType formatType) {
        StringBuilder stringBuilder = randSecondBuilder(time, size, formatType);
        appendFormat(stringBuilder, formatType, id, 0, 8);
        return stringBuilder.toString();
    }

    public static String randHashId(Object dist) {
        return HelperRandom.randSecondId(System.currentTimeMillis(), 8, dist.hashCode());
    }

    public static int getHashLong(long time) {
        return (int) time ^ (int) (time >>> 32);
    }

    public static long getHashLong(long time, int seqPos) {
        if (seqPos > 0) {
            if (seqPos < 8) {
                seqPos *= 2;
                int hash = (int) (time >> (seqPos + 16));
                hash <<= time % (32 - seqPos - seqPos + 1);
                time ^= hash;

            } else {
                return getShortHashLong(time, seqPos);
            }
        }

        return time;
    }

    public static long getShortHashLong(long time, int seqPos) {
        if (seqPos > 0 && seqPos < 16) {
            seqPos *= 2;
            int pos = 32 - seqPos;
            long hash = 0;
            while (pos >= 0) {
                hash ^= (time >>> pos);
                pos -= seqPos;
                if (pos < 0) {
                    hash ^= (time << nextInt(-pos + 1));
                }
            }

            return hash;
        }

        return time;
    }

    public static int getReverseInt(int time) {
        time = (((time & 0xaaaaaaaa) >> 1) | ((time & 0x55555555) << 1));
        time = (((time & 0xcccccccc) >> 2) | ((time & 0x33333333) << 2));
        time = (((time & 0xf0f0f0f0) >> 4) | ((time & 0x0f0f0f0f) << 4));
        time = (((time & 0xff00ff00) >> 8) | ((time & 0x00ff00ff) << 8));
        return ((time >> 16) | (time << 16));
    }

    public static IFormatType newFormatType(final char[] fChars) {
        final int charsLen = fChars.length;
        int len;
        {
            len = 0;
            int i = Integer.MAX_VALUE;
            while (i > 0) {
                len++;
                i /= charsLen;
            }
        }

        final int intLen = len;

        {
            len = 0;
            long l = Long.MAX_VALUE;
            while (l > 0) {
                len++;
                l /= charsLen;
            }
        }

        final int longLen = len;

        return new IFormatType() {
            @Override
            public int intLen() {
                return intLen;
            }

            @Override
            public char[] charsForInt(int i) {
                return charsForLong(i);
            }

            @Override
            public int longLen() {
                return longLen;
            }

            @Override
            public char[] charsForLong(long l) {
                char[] buffer = new char[l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE ? intLen() : longLen()];
                int i = 0;
                int ci;
                while (l != 0) {
                    ci = (int) (l % charsLen);
                    if (ci < 0) {
                        ci = -ci;
                    }

                    buffer[i] = fChars[ci];
                    l /= charsLen;
                    i++;
                }

                return Arrays.copyOfRange(buffer, 0, i);
            }
        };
    }

    public enum FormatType implements IFormatType {

        NUMBER {
            @Override
            public int intLen() {
                return 10;
            }

            @Override
            public char[] charsForInt(int i) {
                return String.valueOf(i < 0 ? -i : i).toCharArray();
            }

            @Override
            public int longLen() {
                return 19;
            }

            @Override
            public char[] charsForLong(long l) {
                return String.valueOf(l < 0 ? -l : l).toCharArray();
            }
        },

        HEX {
            @Override
            public int intLen() {
                return 8;
            }

            @Override
            public char[] charsForInt(int i) {
                return Integer.toHexString(i).toCharArray();
            }

            @Override
            public int longLen() {
                return 16;
            }

            @Override
            public char[] charsForLong(long l) {
                return Long.toHexString(l).toCharArray();
            }
        };

        public static IFormatType DIG_LETTER = newFormatType(DIG_LETTER_CHARS);

        public static IFormatType DIG_M_LETTER = newFormatType(DIG_LETTER_M_CHARS);

    }

    public interface IFormatType {

        public int intLen();

        public char[] charsForInt(int i);

        public int longLen();

        public char[] charsForLong(long l);

    }

    public static class RandomPool<T> {

        private List<RandomPoolElement<T>> elements = new ArrayList<RandomPoolElement<T>>();

        private float[] probabilities;

        public List<RandomPoolElement<T>> getElements() {
            return elements;
        }

        public float[] getProbabilities() {
            if (probabilities == null) {
                float total = 0;
                for (RandomPoolElement<T> element : elements) {
                    total += element.rare;
                }

                int last = elements.size();
                if (last < 1) {
                    return null;
                }

                probabilities = new float[last];
                if (total != 0) {
                    for (last--; last >= 0; last--) {
                        probabilities[last] = elements.get(last).rare / total;
                    }
                }
            }

            return probabilities;
        }

        public int size() {
            return elements.size();
        }

        public void add(RandomPoolElement<T> element) {
            elements.add(element);
            probabilities = null;
        }

        public void add(T element, float rare) {
            add(new RandomPoolElement<T>(element, rare));
        }

        public boolean remove(RandomPoolElement<T> element) {
            if (elements.remove(element)) {
                probabilities = null;
                return true;
            }

            return false;
        }

        public RandomPoolElement<T> removeElement(T element) {
            Iterator<RandomPoolElement<T>> iterator = elements.iterator();
            while (iterator.hasNext()) {
                RandomPoolElement<T> poolElement = iterator.next();
                if (KernelObject.equals(element, poolElement.element)) {
                    iterator.remove();
                    probabilities = null;
                    return poolElement;
                }
            }

            return null;
        }

        public int randIndex() {
            float[] probabilities = getProbabilities();
            if (probabilities == null) {
                return -1;
            }

            int length = probabilities.length;
            return length == 0 ? -1 : length == 1 ? 0 : HelperRandom.randIndex(probabilities);
        }

        public T randElement() {
            int index = randIndex();
            return index < 0 ? null : elements.get(index).element;
        }

        public T randElement(FilterTemplate<T> filterTemplate) {
            if (filterTemplate == null) {
                return randElement();
            }

            try {
                float[] probabilities = getProbabilities();
                if (probabilities != null) {
                    float totalProbabilities = 0;
                    int size = elements.size();
                    for (int i = 0; i < size; i++) {
                        RandomPoolElement<T> element = elements.get(i);
                        if (filterTemplate.doWith(element.element)) {
                            totalProbabilities += probabilities[i];
                        }
                    }

                    if (totalProbabilities == 1) {
                        return randElement();

                    } else {
                        float total = RANDOM.nextFloat() * totalProbabilities;
                        int index = probabilities.length;
                        while (index-- > 0) {
                            if (filterTemplate.doWith(elements.get(index).element) && (total -= probabilities[index]) <= 0) {
                                break;
                            }
                        }

                        return elements.get(index).element;
                    }
                }

            } catch (BreakException e) {
            }

            return null;
        }
    }

    public static class RandomPoolElement<T> {

        public T element;

        public float rare;

        public RandomPoolElement() {
        }

        public RandomPoolElement(T element, float rare) {
            this.element = element;
            this.rare = rare;
        }
    }
}
