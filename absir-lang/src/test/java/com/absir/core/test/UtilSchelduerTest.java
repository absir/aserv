/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月7日 下午2:08:57
 */
package com.absir.core.test;

import com.absir.core.util.UtilSchelduer;
import com.absir.core.util.UtilSchelduer.NextRunnable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

@RunWith(value = JUnit4.class)
public class UtilSchelduerTest {

    UtilSchelduer<NextRunnable> schelduer = new UtilSchelduer<UtilSchelduer.NextRunnable>();

    @Test
    public void test() throws InterruptedException {
        schelduer.start();
        long time = System.currentTimeMillis();
        schelduer.addRunnables(createNextRunnable(time + 3000, "delay 3s out"));
        schelduer.addRunnables(createNextRunnable(time + 2000, "delay 2s out"));
        schelduer.addRunnables(createNextRunnable(time + 1000, "delay 1s out"));
        schelduer.addRunnables(createNextRunnable(time + 6000, "delay 6s out"));
        schelduer.addRunnables(createNextRunnable(time + 2000, "delay 2s out"));
        schelduer.addRunnables(createNextRunnable(time + 1000, "delay 1s out"));
        schelduer.addRunnables(createNextRunnable(time + 3000, "delay 3s out"));
        schelduer.addRunnables(createNextRunnable(time + 2000, "delay 2s out"));
        schelduer.addRunnables(createNextRunnable(time + 1000, "delay 1s out"));
        Thread.sleep(1000);
        time = System.currentTimeMillis();
        schelduer.addRunnables(createNextRunnable(time + 3000, "delay 4s out"));
        schelduer.addRunnables(createNextRunnable(time + 3000, "delay 4s out"));
        schelduer.addRunnables(createNextRunnable(time + 2000, "delay 3s out"));
        schelduer.addRunnables(createNextRunnable(time + 1000, "delay 2s out"));
        schelduer.addRunnables(createNextRunnable(time + 2000, "delay 3s out"));
        schelduer.addRunnables(createNextRunnable(time + 1000, "delay 2s out"));
        schelduer.addRunnables(createNextRunnable(time + 3000, "delay 4s out"));
        schelduer.addRunnables(createNextRunnable(time + 2000, "delay 3s out"));
        schelduer.addRunnables(createNextRunnable(time + 1000, "delay 2s out"));
        Thread.sleep(1000);
        time = System.currentTimeMillis();
        schelduer.addRunnables(createNextRunnable(time + 1000, "delay 3s out"));
        schelduer.addRunnables(createNextRunnable(time + 1000, "delay 3s out"));
        schelduer.addRunnables(createNextRunnable(time + 1000, "delay 3s out"));
        Thread.sleep(1000);
        schelduer.addRunnables(createNextRunnable(time + 1000, "delay 4s out"));
        schelduer.addRunnables(createNextRunnable(time + 1000, "delay 4s out"));
        schelduer.addRunnables(createNextRunnable(time + 1000, "delay 4s out"));
        schelduer.addRunnables(createNextRunnable(time + 1000, "delay 4s out"));
        Thread.sleep(5000);
        schelduer.stopNow();
    }

    protected NextRunnable createNextRunnable(final long nextTime, final String capition) {
        return new NextRunnable() {

            @Override
            public int getOrder() {
                return (int) (getNextTime() >> 10);
            }

            @Override
            public void start(Date date) {
            }

            @Override
            public long getNextTime() {
                return nextTime;
            }

            @Override
            public void run(Date date) {
                System.out.println(capition + " => " + System.currentTimeMillis());
            }
        };
    }

}
