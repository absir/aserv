package com.absir.data.test;

import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperFileName;
import com.absir.system.test.AbstractTest;
import com.baidu.bjf.remoting.protobuf.ProtobufIDLProxy;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;

/**
 * Created by absir on 16/3/6.
 */
public class SiteGeneratorTest extends AbstractTest {

    @Test
    public void test() throws Exception {
        String classPath = HelperFileName.getClassPath(getClass());
        //IDLProxyObject proxy = ProtobufIDLProxy.createSingle(HelperFile.readFileToString(new File(classPath + "Site.proto")));
        InputStream fis = HelperFile.openInputStream(new File(classPath + "Site.proto"));
        ProtobufIDLProxy.generateSource(fis, new File(classPath));
    }
}
