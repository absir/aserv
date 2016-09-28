package com.absir.core.test;

import com.absir.core.util.UtilAccessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 16/9/27.
 */
@RunWith(value = JUnit4.class)
public class KernelAccessorTest {

    public static class Dto {

        public String name;

    }

    public Map<String, Dto> map;


    @Test
    public void test() {
        UtilAccessor.Accessor accessor1 = UtilAccessor.getAccessorCls(getClass(), "map.a.name");
        UtilAccessor.Accessor accessor2 = UtilAccessor.getAccessorObj(this, "map.b.name");
        UtilAccessor.Accessor accessor3 = UtilAccessor.getAccessorObj(this, "map.c.name");

        this.map = new HashMap<String, Dto>();
        Dto dto = new Dto();
        dto.name = "123";

        Dto dto2 = new Dto();
        dto2.name = "222";

        Dto dto3 = new Dto();
        dto3.name = "333";

        this.map.put("a", dto);
        this.map.put("b", dto2);
        this.map.put("b", dto3);

        System.out.println("KernelAccessorTest " + accessor1.get(this));
        System.out.println("KernelAccessorTest " + accessor2.get(this));

    }
}
