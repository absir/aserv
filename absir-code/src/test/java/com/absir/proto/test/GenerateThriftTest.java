package com.absir.proto.test;

import com.absir.code.ProtoJavaMerger;
import com.absir.core.helper.HelperFileName;
import com.absir.core.helper.HelperIO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

/**
 * Created by absir on 2016/12/6.
 */
@RunWith(value = JUnit4.class)
public class GenerateThriftTest {

    @Test
    public void test() throws Exception {
        String classPath = HelperFileName.getClassPath(getClass());
        System.out.println(classPath);

        Process process = Runtime.getRuntime().exec("pwd");
        System.out.println(HelperIO.toString(process.getInputStream()));
        System.err.println(HelperIO.toString(process.getErrorStream()));

        process = Runtime.getRuntime().exec("java -jar /Developer/jar/swift-generator-cli-0.7.0-standalone.jar target/test-classes/Thrift/HelloWorld.thrift");
        System.out.println(HelperIO.toString(process.getInputStream()));
        System.err.println(HelperIO.toString(process.getErrorStream()));

        ProtoJavaMerger javaMerger = new ProtoJavaMerger();
        javaMerger.mergeBaseDir(new File(classPath + "/thrift"), new File(classPath + "/thrift2"));

        //javaMerger.mergeBaseDir(new File("C:\\Users\\absir\\Desktop\\G2"), new File("C:\\Users\\absir\\Desktop\\G3"));
    }

}
