/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月23日 下午5:45:07
 */
package com.absir.client.test;

import com.absir.core.util.UtilAtom;
import com.absir.core.util.UtilContext;
import com.absir.core.util.UtilPipedStream;
import com.absir.data.helper.HelperDataFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(value = JUnit4.class)
public class ClientDataTest {

    private static String[] names;


    static {
        int length = 100;
        names = new String[length];
        for (int i = 0; i < length; i++) {
            names[i] = "abc";
        }
    }

    @Test
    public void test() throws IOException {
        final UtilPipedStream.OutInputStream inputStream = new UtilPipedStream.OutInputStream();
        final UtilPipedStream.WrapOutStream outputStream = new UtilPipedStream.WrapOutStream(inputStream);

//        final PipedInputStream inputStream = new PipedInputStream();
//        final PipedOutputStream outputStream = new PipedOutputStream();
//        outputStream.connect(inputStream);


        final String test = "abc";


        final UtilAtom atom = new UtilAtom();

        atom.increment();
        UtilContext.getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(200);
                    //outputStream.write(KernelByte.getVarintsLength(1));
                    for (int i = 0; i < 1; i++) {
                        //outputStream.write(test.getBytes());
                        HelperDataFormat.PACK.write(outputStream, names);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    atom.decrement();
                    UtilPipedStream.closeCloseable(outputStream);
                }
            }
        });

        atom.increment();
        UtilContext.getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //int code = KernelByte.getVarintsLength(inputStream);
                    //System.out.println("code = " + code);
//                    int l = test.length();
//                    byte[] buffer = new byte[l];
//                    int len;
//                    while ((len = inputStream.read(buffer, 0, l)) > 0) {
//                        System.out.print(new String(buffer, 0, len));
//                    }


                    for (int i = 0; i < 1; i++) {
                        //outputStream.write(test.getBytes());
                        String[] vals = HelperDataFormat.PACK.read(inputStream, String[].class);
                        System.out.println("vals = " + HelperDataFormat.JSON.writeAsStringArray(vals));
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    atom.decrement();
                    //UtilPipedStream.closeCloseable(inputStream);
                }
            }
        });

        atom.await();
    }

}
