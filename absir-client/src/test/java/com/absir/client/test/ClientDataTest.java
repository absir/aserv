/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月23日 下午5:45:07
 */
package com.absir.client.test;

import com.absir.client.SocketAdapter;
import com.absir.core.helper.HelperIO;
import com.absir.data.helper.HelperDataFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

@RunWith(value = JUnit4.class)
public class ClientDataTest {

    @Test
    public void test() throws IOException {
        System.out.println(SocketAdapter.HUMAN_FLAG);
        System.out.println(SocketAdapter.URI_DICT_FLAG);

        final PipedInputStream inputStream = new PipedInputStream();
        final PipedOutputStream outputStream = new PipedOutputStream();
        inputStream.connect(outputStream);


        new Thread() {
            @Override
            public void run() {
                try {
                    //HelperDataFormat.JSON_MAPPER.writeValue(outputStream, "aaaaaa");

                    HelperDataFormat.JSON.writeArray(outputStream, new String[]{"test", "test2"});

                    HelperDataFormat.PACK.writeArray(outputStream, new String[]{"test", "test2"});

                    outputStream.flush();
                    outputStream.write("abc".getBytes());

                    outputStream.flush();
                    //outputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        //String ddd = HelperDataFormat.JSON_MAPPER.readValue(inputStream, String.class);
        //System.out.println("ddd = " + ddd);
        {
            Object[] names = HelperDataFormat.JSON.readArray(inputStream, String.class, String.class);
            System.out.println(HelperDataFormat.JSON.writeAsStringArray(names));
        }
        System.out.println("1233");

        {
            Object[] names = HelperDataFormat.PACK.readArray(inputStream, String.class, String.class);
            System.out.println(HelperDataFormat.JSON.writeAsStringArray(names));
        }

        System.out.println("4444");

        System.out.println(HelperIO.toString(inputStream));
    }

}
