package com.absir.proto.test;

import com.absir.code.ThriftJavaMerger;
import com.absir.core.helper.HelperFileName;
import com.absir.core.helper.HelperIO;
import com.facebook.swift.generator.SwiftGenerator;
import com.facebook.swift.generator.SwiftGeneratorConfig;
import com.facebook.swift.generator.SwiftGeneratorTweak;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by absir on 2016/12/6.
 */
@RunWith(value = JUnit4.class)
public class GenerateThriftTest {

    protected void doShell(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        System.out.println(HelperIO.toString(process.getInputStream()));
        System.err.println(HelperIO.toString(process.getErrorStream()));
    }

    @Test
    public void test() throws Exception {
        String classPath = HelperFileName.getClassPath(getClass());
        System.out.println(classPath);

//        doShell("pwd");
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("java -jar /Developer/jar/swift-generator-cli-0.7.0-standalone.jar");
//        sb.append(" target/test-classes/Thrift/HelloWorld.thrift");
//        sb.append(" -use_java_namespace");
//        sb.append(" -out " + classPath);

        //doShell(sb.toString());

        //java-regular
        //java-immutable
        //java-ctor

        final SwiftGeneratorConfig config = SwiftGeneratorConfig.builder()
                .inputBase(new URI("file://" + classPath + "Thrift/"))
                .outputFolder(new File(classPath + "Thrift/"))
                .generateIncludedCode(true)
                .codeFlavor("java-regular")
//                .addTweak(SwiftGeneratorTweak.ADD_CLOSEABLE_INTERFACE)
                .addTweak(SwiftGeneratorTweak.EXTEND_RUNTIME_EXCEPTION)
                .addTweak(SwiftGeneratorTweak.ADD_THRIFT_EXCEPTION)
                .addTweak(SwiftGeneratorTweak.USE_PLAIN_JAVA_NAMESPACE)
                .build();

        final SwiftGenerator generator = new SwiftGenerator(config);
        List<URI> uriList = new ArrayList<URI>();
        uriList.add(new URI("HelloWorld.thrift"));
        generator.parse(uriList);

        ThriftJavaMerger javaMerger = new ThriftJavaMerger();
        javaMerger.mergeBaseDir(new File(classPath + "/thrift"), new File(classPath + "/thrift2"));

        //javaMerger.mergeBaseDir(new File("C:\\Users\\absir\\Desktop\\G2"), new File("C:\\Users\\absir\\Desktop\\G3"));
    }

}
