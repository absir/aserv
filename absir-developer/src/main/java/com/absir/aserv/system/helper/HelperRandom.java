/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-28 下午4:49:18
 */
package com.absir.aserv.system.helper;

import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.FilterTemplate;
import com.absir.core.kernel.KernelObject;

import java.awt.*;
import java.util.*;
import java.util.List;

public class HelperRandom {

    public static final Random RANDOM = new Random(new Date().getTime());

    private static final int SECEND_SIZE = 3;

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

    public static String randSecendId() {
        return randSecendId(SECEND_SIZE);
    }

    public static String randSecendId(int size) {
        return randSecendId(System.currentTimeMillis(), size);
    }

    public static String randSecendId(long time, int size) {
        return randSecendBuidler(time, size).toString();
    }

    public static String randSecendId(long time, int size, int id) {
        return randSecendBuidler(time, size).append(randFormate(8, Integer.toHexString(id).toCharArray())).toString();
    }

    public static StringBuilder randSecendBuidler(long time, int size) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(randFormate(16, Long.toHexString(time).toCharArray()));
        randAppendFormate(stringBuilder, size);
        return stringBuilder;
    }

    public static String randSecendBuidler(int time, int size, int id) {
        return randSecendBuidler(time, size).append(randFormate(8, Integer.toHexString(id).toCharArray())).toString();
    }

    public static StringBuilder randSecendBuidler(int time, int size) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(randFormate(8, Integer.toHexString(time).toCharArray()));
        randAppendFormate(stringBuilder, size);
        return stringBuilder;
    }

    public static char[] randFormate(int size, char[] chars) {
        int length = chars.length;
        if (length >= size) {
            return chars;

        } else {
            char[] buff = new char[size];
            int start = size - length;
            Arrays.fill(buff, 0, start, '0');
            for (length = start; start < size; start++) {
                buff[start] = chars[start - length];
            }

            return buff;
        }
    }

    public static void randAppendFormate(StringBuilder stringBuilder, int size) {
        while (size > 0) {
            char[] chars = size > 8 ? Long.toHexString(RANDOM.nextLong()).toCharArray() : Integer.toHexString(RANDOM.nextInt())
                    .toCharArray();
            int length = chars.length;
            if (length < size) {
                if (size > 8) {
                    if (length < 16) {
                        length = 16;
                        chars = randFormate(length, chars);
                    }

                } else {
                    if (length < 8) {
                        length = 8;
                        chars = randFormate(length, chars);
                    }
                }
            }

            if (size >= length) {
                stringBuilder.append(chars);

            } else {
                stringBuilder.append(chars, length - size, size);
            }

            size -= length;
        }
    }

    public static String randHashId(Object dist) {
        return HelperRandom.randSecendId(System.currentTimeMillis(), 8, dist.hashCode());
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

    public static String randSequenceId(long time) {
        time = getHashLong(time, 4);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(randFormate(12, Long.toHexString(time).toCharArray()), 0, 12);
        HelperRandom.randAppendFormate(stringBuilder, 4);
        return stringBuilder.toString();
    }

    public static String randSequenceNumberId(long time) {
        time = getHashLong(time, 6);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(randFormate(12, String.valueOf(time).toCharArray()), 0, 12);
        HelperRandom.randAppendNumberFormate(stringBuilder, 4);
        return stringBuilder.toString();
    }

    public static String randSequenceShortNumberId(long time) {
        time = getHashLong(time, 9);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(randFormate(6, String.valueOf(time).toCharArray()), 0, 6);
        HelperRandom.randAppendNumberFormate(stringBuilder, 2);
        return stringBuilder.toString();
    }

    public static void randAppendNumberFormate(StringBuilder stringBuilder, int size) {
        while (size > 0) {
            char[] chars = size > 8 ? String.valueOf(RANDOM.nextLong()).toCharArray() : String.valueOf(RANDOM.nextInt())
                    .toCharArray();
            int length = chars.length;
            if (length < size) {
                if (size > 8) {
                    if (length < 16) {
                        length = 16;
                        chars = randFormate(length, chars);
                    }

                } else {
                    if (length < 8) {
                        length = 8;
                        chars = randFormate(length, chars);
                    }
                }
            }

            if (size >= length) {
                stringBuilder.append(chars);

            } else {
                stringBuilder.append(chars, length - size, size);
            }

            size -= length;
        }
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
            return probabilities == null ? -1 : HelperRandom.randIndex(probabilities);
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
