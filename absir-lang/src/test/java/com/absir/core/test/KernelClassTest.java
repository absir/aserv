package com.absir.core.test;

import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelReflect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@RunWith(value = JUnit4.class)
public class KernelClassTest {

    public List<JUnit4> a1;

    public Map<String, JUnit4> a2;

    @Test
    public void test() {
        Field a1Field = KernelReflect.field(KernelClassTest.class, "a1");
        for (Class<?> cls : KernelClass.componentClasses(a1Field.getGenericType())) {
            System.out.println(cls);
        }

        Field a2Field = KernelReflect.field(KernelClassTest.class, "a2");
        for (Class<?> cls : KernelClass.componentClasses(a2Field.getGenericType())) {
            System.out.println(cls);
        }
    }
}
