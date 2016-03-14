/**
 * Copyright 2015 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2015年10月7日 下午2:08:57
 */
package com.absir.core.test;

import com.absir.core.util.UtilSchelduer;
import com.absir.core.util.UtilSchelduer.NextRunable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

/**
 * @author absir
 */
@RunWith(value = JUnit4.class)
public class UtilSchelduerTest {

    UtilSchelduer<NextRunable> schelduer = new UtilSchelduer<UtilSchelduer.NextRunable>();

    @Test
    public void test() throws InterruptedException {
        schelduer.start();
        long time = System.currentTimeMillis();
        schelduer.addRunables(createNextRunable(time + 3000, "delay 3s out"));
        schelduer.addRunables(createNextRunable(time + 2000, "delay 2s out"));
        schelduer.addRunables(createNextRunable(time + 1000, "delay 1s out"));
        schelduer.addRunables(createNextRunable(time + 6000, "delay 6s out"));
        schelduer.addRunables(createNextRunable(time + 2000, "delay 2s out"));
        schelduer.addRunables(createNextRunable(time + 1000, "delay 1s out"));
        schelduer.addRunables(createNextRunable(time + 3000, "delay 3s out"));
        schelduer.addRunables(createNextRunable(time + 2000, "delay 2s out"));
        schelduer.addRunables(createNextRunable(time + 1000, "delay 1s out"));
        Thread.sleep(1000);
        time = System.currentTimeMillis();
        schelduer.addRunables(createNextRunable(time + 3000, "delay 4s out"));
        schelduer.addRunables(createNextRunable(time + 3000, "delay 4s out"));
        schelduer.addRunables(createNextRunable(time + 2000, "delay 3s out"));
        schelduer.addRunables(createNextRunable(time + 1000, "delay 2s out"));
        schelduer.addRunables(createNextRunable(time + 2000, "delay 3s out"));
        schelduer.addRunables(createNextRunable(time + 1000, "delay 2s out"));
        schelduer.addRunables(createNextRunable(time + 3000, "delay 4s out"));
        schelduer.addRunables(createNextRunable(time + 2000, "delay 3s out"));
        schelduer.addRunables(createNextRunable(time + 1000, "delay 2s out"));
        Thread.sleep(1000);
        time = System.currentTimeMillis();
        schelduer.addRunables(createNextRunable(time + 1000, "delay 3s out"));
        schelduer.addRunables(createNextRunable(time + 1000, "delay 3s out"));
        schelduer.addRunables(createNextRunable(time + 1000, "delay 3s out"));
        Thread.sleep(1000);
        schelduer.addRunables(createNextRunable(time + 1000, "delay 4s out"));
        schelduer.addRunables(createNextRunable(time + 1000, "delay 4s out"));
        schelduer.addRunables(createNextRunable(time + 1000, "delay 4s out"));
        schelduer.addRunables(createNextRunable(time + 1000, "delay 4s out"));
        Thread.sleep(5000);
        schelduer.stopNow();
    }

    protected NextRunable createNextRunable(final long nextTime, final String capition) {
        return new NextRunable() {

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
