package com.absir.developer;

import com.absir.client.helper.HelperJson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tplatform.DServer;

/**
 * Created by absir on 2016/12/6.
 */
@RunWith(value = JUnit4.class)
public class ThriftJson {

    public static class TestBean {

        protected long id;

        protected String name;


        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void test() throws Exception {
        DServer server = new DServer();
        server.setSAddress("123");
        System.out.println(HelperJson.encode(server));

        TestBean test = new TestBean();
        test.setName("123");
        System.out.println(HelperJson.encode(test));
    }

}
