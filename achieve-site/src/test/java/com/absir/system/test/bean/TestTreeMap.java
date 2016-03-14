/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-8 下午4:13:43
 */
package com.absir.system.test.bean;

import com.absir.client.helper.HelperEncrypt;
import com.absir.core.base.IBase;
import com.absir.system.test.AbstractTest;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author absir
 *
 */
public class TestTreeMap extends AbstractTest {

    @Test
    public void main() throws IOException {
        String str = HelperEncrypt.aesEncryptBase64("abc", "123");
        System.out.println(str);
        str = HelperEncrypt.aesDecryptBase64(str, "123");
        System.out.println(str);
    }

    public static class TreeKey implements IBase<Serializable> {

        public void test() {

        }

        /*
         * (non-Javadoc)
         *
         * @see com.absir.aserv.system.bean.proxy.JiBase#getId()
         */
        @Override
        public String getId() {
            return "123";
        }
    }

}
