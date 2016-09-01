/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月18日 下午6:11:48
 */
package com.absir.core.test;

import com.absir.core.kernel.KernelClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@RunWith(value = JUnit4.class)
public class MapTest {

    public interface IMap {

    }

    @Test
    public void test() throws InterruptedException {
//        final Map<Object, Object> map = new HashMap<Object, Object>();
//        final Random random = new Random(new Date().getTime());
//        final AtomicInteger cnt = new AtomicInteger(0);
//        for (int i = 0; i < 0; i++) {
//            new Thread() {
//
//                public void run() {
//                    for (int i = 0; i < 1000; i++) {
//                        System.out.println("map == " + map.size() + " : " + cnt.incrementAndGet());
//                        map.put(random.nextInt(100), random.nextInt(100));
//                        map.remove(random.nextInt(100));
//                    }
//
//                }
//
//                ;
//
//            }.start();
//        }
//
//        Thread.sleep(30000);


        Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{IMap.class}, new InvocationHandler() {
            @Override
            public java.lang.Object invoke(java.lang.Object proxy, Method method, java.lang.Object[] args) throws Throwable {
                return null;
            }
        });

        System.out.println(proxy.getClass());

        Constructor constructor = proxy.getClass().getDeclaredConstructors()[0];
        System.out.println(constructor);



        Object proxy1 = KernelClass.declaredNew(proxy.getClass());


        System.out.println(proxy);
        System.out.println(proxy1);
    }

}
