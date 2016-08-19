package com.absir.core.test;

import com.absir.core.kernel.KernelByte;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelReflect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

@RunWith(value = JUnit4.class)
public class KernelClassTest {

    public List<JUnit4> a1;

    public Map<String, JUnit4> a2;

    @Test
    public void test() {
        InetAddress ia = null;
        try {
            ia = ia.getLocalHost();

            String localname = ia.getHostName();
            String localip = ia.getHostAddress();
            System.out.println("本机名称是：" + localname);
            System.out.println("本机的ip是 ：" + localip);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Field a1Field = KernelReflect.field(KernelClassTest.class, "a1");
        for (Class<?> cls : KernelClass.componentClasses(a1Field.getGenericType())) {
            System.out.println(cls);
        }

        Field a2Field = KernelReflect.field(KernelClassTest.class, "a2");
        for (Class<?> cls : KernelClass.componentClasses(a2Field.getGenericType())) {
            System.out.println(cls);
        }

        System.out.println(KernelByte.VARINTS_1_LENGTH);
        System.out.println(KernelByte.VARINTS_2_LENGTH);
        System.out.println(KernelByte.VARINTS_3_LENGTH);
        System.out.println(KernelByte.VARINTS_4_LENGTH);

        testVarints(KernelByte.VARINTS_4_LENGTH);
        testVarints(KernelByte.VARINTS_4_LENGTH - 1);
        testVarints(KernelByte.VARINTS_4_LENGTH + 1);

        testVarints(0);
        testVarints(-100);
        testVarints(65535);
        testVarints(1024);

        testVarints(Integer.MAX_VALUE);

        testVarints(KernelByte.VARINTS_1_LENGTH);
        testVarints(KernelByte.VARINTS_2_LENGTH);
        testVarints(KernelByte.VARINTS_3_LENGTH);
        testVarints(KernelByte.VARINTS_4_LENGTH);

        testVarints(KernelByte.VARINTS_1_LENGTH + 1);
        testVarints(KernelByte.VARINTS_2_LENGTH + 1);
        testVarints(KernelByte.VARINTS_3_LENGTH + 1);
        testVarints(KernelByte.VARINTS_4_LENGTH + 1);

        testVarints(KernelByte.VARINTS_1_LENGTH - 1);
        testVarints(KernelByte.VARINTS_2_LENGTH - 1);
        testVarints(KernelByte.VARINTS_3_LENGTH - 1);
        testVarints(KernelByte.VARINTS_4_LENGTH - 1);
    }

    public void testVarints(int varints) {
        int len = KernelByte.getVarintsLength(varints);
        byte[] buffer = new byte[len];
        KernelByte.setVarintsLength(buffer, 0, varints);
        int readVarints = KernelByte.getVarintsLength(buffer, 0);
        System.out.println("testVarints[" + varints + "] len:" + len + " read:" + readVarints);

    }
}
