/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月7日 下午2:08:57
 */
package com.absir.io.test;

import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelLang;
import com.absir.core.util.UtilPipedStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;

@RunWith(value = JUnit4.class)
public class UtilPipedStreamTest {

    @Test
    public void test() throws InterruptedException, IOException {
        testNextStream();
        //testPipeStream();
        //Thread.sleep(1000);
    }

    protected void testNextStream() {
        //UtilPipedStream pipedStream = new UtilPipedStream(100000);
        //final UtilPipedStream.NextOutputStream outputStream = pipedStream.createNextOutputStream(1);
        final UtilPipedStream.OutInputStream outputStream = new UtilPipedStream.OutInputStream(3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HelperIO.doWithReadLine(outputStream, new KernelLang.CallbackBreak<String>() {
                        @Override
                        public void doWith(String template) throws KernelLang.BreakException {
                            System.out.println(template);
                        }
                    });

                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = outputStream.read(buffer, 0, 1024)) > 0) {
                        System.out.println(new String(buffer, 0, len));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        StringBuilder stringBuilder = new StringBuilder();
        PrintWriter writer = new PrintWriter(new UtilPipedStream.WrapOutStream(outputStream));
        for (int i = 0; i < 10; i++) {
            stringBuilder.append(i);
            writer.println(i + " => " + stringBuilder);
            writer.flush();
        }

        try {
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void testPipeStream() throws IOException {
        final PipedInputStream pipedInputStream = new PipedInputStream();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    byte[] buffer = new byte[1024];
//                    int len = 0;
//                    while ((len = pipedInputStream.read(buffer, 0, 1024)) > 0) {
//                        System.out.println(new String(buffer, 0, len));
//                    }

                    HelperIO.doWithReadLine(pipedInputStream, new KernelLang.CallbackBreak<String>() {
                        @Override
                        public void doWith(String template) throws KernelLang.BreakException {
                            System.out.println(template);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        PrintWriter writer = new PrintWriter(new PipedOutputStream(pipedInputStream));
        for (int i = 0; i < 10; i++) {
            writer.println("PipedInputStream " + i);
            writer.flush();
        }
    }

}
