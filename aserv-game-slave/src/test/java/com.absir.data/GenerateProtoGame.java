package com.absir.data;

import com.absir.code.ProtoJavaMerger;
import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperFileName;
import com.baidu.bjf.remoting.protobuf.ProtobufIDLProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.InputStream;

/**
 * Created by absir on 16/3/18.
 */
@RunWith(JUnit4.class)
public class GenerateProtoGame {

    @Test
    public void test() throws Exception {
        String classPath = HelperFileName.getClassPath(getClass());
        System.out.println("classPath = " + classPath);
        InputStream inputStream = HelperFile.openInputStream(new File(classPath + "game.proto"));
        File pojoFile = new File(classPath + "G2/Protocol");
        HelperFile.write(new File(classPath + "G2/Protocol/README.md"), "");
        ProtobufIDLProxy.generateSource(inputStream, pojoFile);
        ProtoJavaMerger javaMerger = new ProtoJavaMerger();
        javaMerger.mergeBaseDir(pojoFile, new File(classPath + "../../src/main/java/"));
    }
}
