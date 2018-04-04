package com.absir.tools;

import com.absir.code.ThriftJavaMerger;
import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperFileName;
import com.absir.core.helper.HelperIO;

import java.io.File;

/**
 * Created by absir on 2016/12/6.
 */
public class GenerateThriftPlatform {

    public static void main(String[] args) throws Exception {
        String classPath = HelperFileName.getClassPath(null);
        System.out.println(classPath);
        HelperIO.execute("pwd");
        HelperIO.execute("rm -rf " + classPath + "thrift/gen-java");
        HelperIO.execute("thrift --gen java:generated_annotations=undated " + classPath + "thrift/tplatform.thrift");
        HelperIO.execute("mv -f gen-java " + classPath + "thrift/");
        ThriftJavaMerger javaMerger = new ThriftJavaMerger();
        javaMerger.mergeBaseDir(new File(classPath + "/thrift/gen-java"), new File(classPath + "../../src/main/java"));

        HelperIO.execute("thrift --gen csharp " + classPath + "thrift/tplatform.thrift");
        HelperIO.execute("rm -rf " + classPath + "thrift/gen-csharp");
        HelperIO.execute("mv -f gen-csharp " + classPath + "thrift/");
        //HelperIO.execute("mv -f " + classPath + "gen/gen-csharp " + classPath + "thrift/");
        HelperFile.copyDirectoryOverWrite(new File(classPath + "thrift/gen-csharp"), new File("/Developer/ABFramework/Assets/Application/Protocol"), true, null, false);
    }

}
