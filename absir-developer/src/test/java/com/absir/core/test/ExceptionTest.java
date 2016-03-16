package com.absir.core.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(value = JUnit4.class)
public class ExceptionTest {

    @Test
    public void test() throws InterruptedException {
        thread();

        Thread.sleep(4000L);
    }

    protected void thread() {
        final Exception e = new Exception();
        new Thread() {

            @Override
            public void run() {
                try {
                    Thread.sleep(3000L);

                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                e.printStackTrace();
            }
        }.start();
    }
}
