package com.absir.core.test;

import com.absir.aserv.system.domain.DSequence;
import com.absir.aserv.system.helper.HelperString;
import com.absir.client.helper.HelperJson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(value = JUnit4.class)
public class HelperTest {

    @Test
    public void test() throws InterruptedException, IOException {
        System.out.println(HelperJson.encode(HelperString.split("a=b&c=d", "=&")));
        DSequence sequence = new DSequence();
        System.out.println(sequence.getNextHexId());
    }

}
