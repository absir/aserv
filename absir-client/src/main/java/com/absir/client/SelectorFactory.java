/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月19日 上午11:48:47
 */
package com.absir.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SelectorFactory {

    public static final int DEFAULT_MAX_SELECTORS = 20;

    protected static final Logger LOGGER = LoggerFactory.getLogger(SelectorFactory.class);
    private static final Queue<Selector> selectors = new ConcurrentLinkedQueue<Selector>();
    private static final AtomicInteger poolSize = new AtomicInteger();
    private static final AtomicInteger missesCounter = new AtomicInteger();
    private static volatile int maxSelectors = DEFAULT_MAX_SELECTORS;

    public final static int getMaxSelectors() {
        return maxSelectors;
    }

    public static void setMaxSelectors(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size < 0");
        }

        missesCounter.set(0);
        maxSelectors = size;
    }

    public final static Selector openSelector() {
        try {
            return Selector.open();

        } catch (IOException e) {
            LOGGER.warn("SelectorFactory. Can not create a selector", e);
        }

        return null;
    }

    public final static Selector getSelector() {
        Selector selector = (Selector) selectors.poll();
        if (selector != null) {
            poolSize.decrementAndGet();

        } else {
            selector = openSelector();

            int missesCount = missesCounter.incrementAndGet();
            if (missesCount % 10000 == 0) {
                LOGGER.warn("SelectorFactory. Pool encounters a lot of misses {0}. Increase default {1} pool size",
                        new Object[]{Integer.valueOf(missesCount), Integer.valueOf(maxSelectors)});
            }
        }

        return selector;
    }

    public final static void returnSelector(Selector s) {
        if (poolSize.getAndIncrement() < maxSelectors) {
            selectors.offer(s);

        } else {
            poolSize.decrementAndGet();
            closeSelector(s);
        }
    }

    public final static void selectNowAndReturnSelector(Selector s) {
        try {
            s.selectNow();
            returnSelector(s);

        } catch (IOException e) {
            LOGGER.warn("Unexpected problem when releasing temporary Selector", e);
            closeSelector(s);
        }
    }

    private final static void closeSelector(Selector s) {
        try {
            s.close();

        } catch (IOException ignored) {
        }
    }
}
