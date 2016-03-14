/**
 * Copyright 2015 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2015年11月5日 下午5:06:27
 */
package com.absir.proto.test;

import com.absir.code.ProtoJavaMerger;
import com.absir.core.helper.HelperFileName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author absir
 */
@RunWith(value = JUnit4.class)
public class UtilProtoJavaMergerTest {

    @Test
    public void test() throws Exception {
        ProtoJavaMerger javaMerger = new ProtoJavaMerger();

        javaMerger.toString();

        String classPath = HelperFileName.getClassPath(getClass());
        System.out.println(classPath);
        // javaMerger.mergeBaseDir(new File(classPath + "/G2"), new
        // File(classPath + "/G3"));

        //javaMerger.mergeBaseDir(new File("C:\\Users\\absir\\Desktop\\G2"), new File("C:\\Users\\absir\\Desktop\\G3"));
    }

}
