/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月5日 下午5:06:27
 */
package com.absir.proto.test;

import com.absir.data._abp_jprotobuf.ProtobufProxyBasic;
import com.absir.proto.PPlatformFrom;
import com.baidu.bjf.remoting.protobuf.Codec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

@RunWith(value = JUnit4.class)
public class UtilProtoJavaMergerTest {

    @Test
    public void test() throws Exception {
        Codec<PPlatformFrom> codec = ProtobufProxyBasic.create(PPlatformFrom.class, true);
        PPlatformFrom from = new PPlatformFrom();
//        byte[] bytes = codec.encode(from);
        System.out.println(Arrays.toString(codec.encode(from)));

        from.setId2(3333L);

        System.out.println(Arrays.toString(codec.encode(from)));

        from.setId(1);


        System.out.println(Arrays.toString(codec.encode(from)));
    }

}
