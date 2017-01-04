/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-8 下午4:13:43
 */
package com.absir.system.test.bean;

import com.absir.core.base.IBase;
import com.absir.system.test.AbstractTest;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;

public class TestTreeMap extends AbstractTest {

    @Test
    public void main() throws IOException {
//        HelperIO.execute("cat /Developer/test/1.sh");
//        HelperIO.executeArray("ls -la", "/Developer/aserv/achieve-site/webResources/root/protected/maintenance/scripts");
    }

    public static class TreeKey implements IBase<Serializable> {

        public void test() {
            //Element element;
            //element.parent().append("<<div class=\"check-box\">>")
        }

        @Override
        public String getId() {
            return "123";
        }
    }

}
