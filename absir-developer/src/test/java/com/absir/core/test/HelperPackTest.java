package com.absir.core.test;

import com.absir.data.helper.HelperDataFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Arrays;

@RunWith(value = JUnit4.class)
public class HelperPackTest {

    public static class Hello2 {

        public int id; // optional
        public long id2; // optional


        public void setId(int id) {
            this.id = id;
        }
    }

    @Test
    public void test() throws InterruptedException, IOException {

        Hello2 hello2 = new Hello2();

        System.out.println(HelperDataFormat.JSON.writeAsStringArray(hello2));

        byte[] bytes = HelperDataFormat.PACK.writeAsBytes(hello2);
        System.out.println(Arrays.toString(bytes));

        hello2.setId(2);
        System.out.println(HelperDataFormat.JSON.writeAsStringArray(hello2));

        bytes = HelperDataFormat.PACK.writeAsBytes(hello2);
        System.out.println(Arrays.toString(bytes));

        Object res = HelperDataFormat.PACK.read(bytes, Hello2.class);
        System.out.println(HelperDataFormat.JSON.writeAsStringArray(res));
    }


}
