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

/**
 * @author absir
 */
public class HelperRandom {

    /**
     * RANDOM
     */
    public static final Random RANDOM = new Random(new Date().getTime());

    /**
     * @param max
     * @return
     */
    public static int nextInt(int max) {
        return RANDOM.nextInt(max);
    }

    /**
     * @param min
     * @param max
     * @return
     */
    public static int nextInt(int min, int max) {
        max -= min;
        max = max > 0 ? RANDOM.nextInt(max) : 0;
        return min + max;
    }

    /**
     * @param rnd
     * @param began
     * @param end
     * @return
     */
    public static int randInt(int rnd, int began, int end) {
        if (rnd < began) {
            rnd = began;

        } else if (rnd > end) {
            rnd = end;
        }

        return rnd;
    }

    /**
     * @param min
     * @param max
     * @param began
     * @param end
     * @return
     */
    public static int randInt(int min, int max, int began, int end) {
        int rnd = nextInt(min, max);
        return randInt(rnd, began, end);
    }

    /**
     * @param collection
     * @return
     */
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

    /**
     * @param collection
     * @return
     */
    public static <T> T randList(List<? extends T> collection) {
        int size = collection.size();
        return collection.remove(nextInt(size));
    }

    /**
     * @param rares
     * @return
     */
    public static float getTotal(float[] rares) {
        float total = 0;
        for (float rare : rares) {
            total += rare;
        }

        return total;
    }

    /**
     * @param rares
     * @return
     */
    public static float getTotal(Collection<Float> rares) {
        float total = 0;
        for (float rare : rares) {
            total += rare;
        }

        return total;
    }

    /**
     * @param rares
     * @return
     */
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

    /**
     * @param rares
     * @return
     */
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

    /**
     * @param probabilities
     * @return
     */
    public static int randIndex(float[] probabilities) {
        float total = RANDOM.nextFloat();
        int i = probabilities.length;
        while (i-- > 0 && (total -= probabilities[i]) > 0)
            ;
        return i;
    }

    /**
     * @param probabilities
     * @param total
     * @return
     */
    public static int randIndex(float[] probabilities, float total) {
        total *= RANDOM.nextFloat();
        int i = probabilities.length;
        while (i-- > 0 && (total -= probabilities[i]) > 0)
            ;
        return i;
    }

    /**
     * @param probabilities
     * @param total
     * @return
     */
    public static int randIndex(List<Float> probabilities, float total) {
        total *= RANDOM.nextFloat();
        int i = probabilities.size();
        while (i-- > 0 && (total -= probabilities.get(i)) > 0)
            ;
        return i;
    }

    public static class RandomPool<T> {

        /**
         * elements
         */
        private List<RandomPoolElement<T>> elements = new ArrayList<RandomPoolElement<T>>();

        /**
         * probabilities
         */
        private float[] probabilities;

        /**
         * @return the elements
         */
        public List<RandomPoolElement<T>> getElements() {
            return elements;
        }

        /**
         * @return the probabilities
         */
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

        /**
         * @return
         */
        public int size() {
            return elements.size();
        }

        /**
         * @param element
         */
        public void add(RandomPoolElement<T> element) {
            elements.add(element);
            probabilities = null;
        }

        /**
         * @param element
         * @param rare
         */
        public void add(T element, float rare) {
            add(new RandomPoolElement<T>(element, rare));
        }

        /**
         * @param element
         * @return
         */
        public boolean remove(RandomPoolElement<T> element) {
            if (elements.remove(element)) {
                probabilities = null;
                return true;
            }

            return false;
        }

        /**
         * @param element
         */
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

        /**
         * @return
         */
        public int randIndex() {
            float[] probabilities = getProbabilities();
            return probabilities == null ? -1 : HelperRandom.randIndex(probabilities);
        }

        /**
         * @return
         */
        public T randElement() {
            int index = randIndex();
            return index < 0 ? null : elements.get(index).element;
        }

        /**
         * @param filterTemplate
         * @return
         */
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

        /**
         * element
         */
        public T element;

        /**
         * rare
         */
        public float rare;

        /**
         *
         */
        public RandomPoolElement() {

        }

        /**
         * @param element
         * @param rare
         */
        public RandomPoolElement(T element, float rare) {
            this.element = element;
            this.rare = rare;
        }
    }

    /**
     * @param b
     * @param e
     * @return
     */
    public static Color randColor(int b, int e) {
        return randColor(b, e, b);
    }

    /**
     * @param b
     * @param e
     * @param a
     * @return
     */
    public static Color randColor(int b, int e, int a) {
        return randColor(b, e, b, e, b, e, a, e);
    }

    /**
     * @param rb
     * @param re
     * @param gb
     * @param ge
     * @param bb
     * @param be
     * @param ab
     * @param ae
     * @return
     */
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

    /**
     * @param size
     * @return
     */
    public static String randChars(int size) {
        return randChars(size, 0);
    }

    /**
     * 0x01 has number | no number 0x02 has char | no char 0x03 UPPER CHAR |
     * lower char
     *
     * @param size
     * @param type
     * @return
     */
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

    /**
     * SECOND_SIZE
     */
    private static final int SECOND_SIZE = 3;

    /**
     * @return
     */
    public static String randSecondId() {
        return randSecondId(SECOND_SIZE);
    }

    /**
     * @param size
     * @return
     */
    public static String randSecondId(int size) {
        return randSecondId(System.currentTimeMillis(), size);
    }

    /**
     * @param time
     * @param size
     * @return
     */
    public static String randSecondId(long time, int size) {
        return randSecondBuidler(time, size).toString();
    }

    /**
     * @param time
     * @param size
     * @return
     */
    public static StringBuilder randSecondBuidler(long time, int size) {
        StringBuilder stringBuilder = new StringBuilder();
        fillFormate(stringBuilder, 16, Long.toHexString(time).toCharArray());
        randAppendFormate(stringBuilder, size);
        return stringBuilder;
    }

    /**
     * @param time
     * @param size
     * @param id
     * @return
     */
    public static String randSecondId(long time, int size, int id) {
        StringBuilder stringBuilder = randSecondBuidler(time, size);
        fillFormate(stringBuilder, 8, Integer.toHexString(id).toCharArray());
        return stringBuilder.toString();
    }

    /**
     * @param size
     * @param chars
     * @return
     */
    public static char[] fillFormate(int size, char[] chars) {
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

    /**
     * @param stringBuilder
     * @param size
     * @param chars
     * @return
     */
    public static int fillFormate(StringBuilder stringBuilder, int size, char[] chars) {
        return fillFormate(stringBuilder, size, chars, -1);
    }

    /**
     * @param stringBuilder
     * @param size
     * @param chars
     * @param offset
     * @return
     */
    public static int fillFormate(StringBuilder stringBuilder, int size, char[] chars, int offset) {
        int length = chars.length;
        if (length < size) {
            size = size <= 8 ? 8 : 16;
            while (length < size) {
                length++;
                stringBuilder.append('0');
            }

            stringBuilder.append(chars);

        } else {
            stringBuilder.append(chars, offset < 0 ? (length - size) : offset, size);
        }

        return length;
    }

    /**
     * @param stringBuilder
     * @param size
     */
    public static void randAppendFormate(StringBuilder stringBuilder, int size) {
        while (size > 0) {
            char[] chars = size > 8 ? Long.toHexString(RANDOM.nextLong()).toCharArray() : Integer.toHexString(RANDOM.nextInt())
                    .toCharArray();
            size -= fillFormate(stringBuilder, size, chars);
        }
    }

    /**
     * @param dist
     * @return
     */
    public static String randHashId(Object dist) {
        return HelperRandom.randSecondId(System.currentTimeMillis(), 8, dist.hashCode());
    }

    /**
     * @param time
     * @return
     */
    public static int getHashLong(long time) {
        return (int) time ^ (int) (time >>> 32);
    }

    /**
     * @param time
     * @return
     */
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

    /**
     * @param time
     * @param seqPos
     * @return
     */
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

    /**
     * @param time
     * @return
     */
    public static int getReverseInt(int time) {
        time = (((time & 0xaaaaaaaa) >> 1) | ((time & 0x55555555) << 1));
        time = (((time & 0xcccccccc) >> 2) | ((time & 0x33333333) << 2));
        time = (((time & 0xf0f0f0f0) >> 4) | ((time & 0x0f0f0f0f) << 4));
        time = (((time & 0xff00ff00) >> 8) | ((time & 0x00ff00ff) << 8));
        return ((time >> 16) | (time << 16));
    }

    /**
     * @param time
     * @return
     */
    public static String randSequenceId(long time) {
        time = getHashLong(time, 4);
        StringBuilder stringBuilder = new StringBuilder();
        fillFormate(stringBuilder, 12, Long.toHexString(time).toCharArray());
        HelperRandom.randAppendFormate(stringBuilder, 4);
        return stringBuilder.toString();
    }

    /**
     * @param time
     * @return
     */
    public static String randSequenceNumberId(long time) {
        time = getHashLong(time, 6);
        StringBuilder stringBuilder = new StringBuilder();
        fillFormate(stringBuilder, 12, String.valueOf(time).toCharArray());
        HelperRandom.randAppendNumberFormate(stringBuilder, 4);
        return stringBuilder.toString();
    }

    /**
     * @param time
     * @return
     */
    public static String randSequenceShortNumberId(long time) {
        time = getHashLong(time, 9);
        StringBuilder stringBuilder = new StringBuilder();
        fillFormate(stringBuilder, 6, Long.toHexString(time).toCharArray(), 0);
        HelperRandom.randAppendNumberFormate(stringBuilder, 2);
        return stringBuilder.toString();
    }

    /**
     * @param stringBuilder
     * @param size
     */
    public static void randAppendNumberFormate(StringBuilder stringBuilder, int size) {
        while (size > 0) {
            char[] chars = size > 8 ? String.valueOf(RANDOM.nextLong()).toCharArray() : String.valueOf(RANDOM.nextInt())
                    .toCharArray();
            size -= fillFormate(stringBuilder, size, chars);
        }
    }
}
