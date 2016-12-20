package com.absir.developer;

import com.absir.code.ThriftJavaMerger;
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
public class GenerateThriftBase {

    @Test
    public void test() throws Exception {
        String classPath = HelperFileName.getClassPath(getClass());
        System.out.println(classPath);
        HelperIO.execute("pwd");
        HelperIO.execute("rm -rf ./target/test-classes/thrift/gen-java");
        HelperIO.execute("thrift --gen java ./target/test-classes/thrift/tbase.thrift");
        HelperIO.execute("mv -f gen-java ./target/test-classes/thrift/");
        ThriftJavaMerger javaMerger = new ThriftJavaMerger();
        javaMerger.mergeBaseDir(new File(classPath + "/thrift/gen-java"), new File(classPath + "../../src/main/java"));
    }

}
