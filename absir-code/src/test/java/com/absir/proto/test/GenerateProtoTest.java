package com.absir.proto.test;

import com.absir.code.ProtoJavaMerger;
import com.absir.core.helper.HelperFileName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

/**
 * Created by absir on 2016/12/6.
 */
@RunWith(value = JUnit4.class)
public class GenerateProtoTest {

    @Test
    public void test() throws Exception {
        ProtoJavaMerger javaMerger = new ProtoJavaMerger();
        javaMerger.toString();

        String classPath = HelperFileName.getClassPath(getClass());
        System.out.println(classPath);

        /****provide jprotobuf***/
        //ProtobufIDLProxy.generateSource(new File(classPath + "/Proto/Platform.proto"), new File(classPath + "/G2"));
        javaMerger.mergeBaseDir(new File(classPath + "/G2"), new File(classPath + "/G3"));

        //javaMerger.mergeBaseDir(new File("C:\\Users\\absir\\Desktop\\G2"), new File("C:\\Users\\absir\\Desktop\\G3"));
    }

}
