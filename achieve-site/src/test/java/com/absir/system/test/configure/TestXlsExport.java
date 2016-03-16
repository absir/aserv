/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-26 下午4:01:49
 */
package com.absir.system.test.configure;

import com.absir.aserv.configure.xls.XlsBase;
import com.absir.aserv.configure.xls.XlsUtils;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.helper.HelperFile;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;
import com.absir.core.kernel.KernelReflect;
import com.absir.system.test.AbstractTest;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

public class TestXlsExport extends AbstractTest {

    public static final String EXPORT_PATH = "/Users/absir/Desktop/";

    private Object[] mapArray = new Object[]{new HashMap<Object, Object>(), new HashMap<Object, Object>()};

    @Test
    public void main() throws IOException {
        Class<?> beanClass = XlsBase.class;
        List<Object> beanList = new ArrayList<Object>();
        beanList.add(newXlsBean(beanClass));
        beanList.add(newXlsBean(beanClass));
        HSSFWorkbook hssfWorkbook = XlsUtils.getWorkbook(beanList, new XlsBase());
        OutputStream outputStream = HelperFile.openOutputStream(new File(EXPORT_PATH + beanClass.getSimpleName() + ".xls"));
        hssfWorkbook.write(outputStream);
    }

    private <T> T newXlsBean(Class<T> beanClass) {
        T bean = KernelClass.newInstance(beanClass);
        initXlsBean(bean, new HashSet<Class<?>>());
        return bean;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initXlsBean(final Object bean, final Set<Class<?>> caches) {
        if (bean == null) {
            return;
        }

        if (bean instanceof Object[]) {
            for (Object o : (Object[]) bean) {
                initXlsBean(o, caches);
            }

        } else if (bean instanceof Collection) {
            for (Object o : (Collection) bean) {
                initXlsBean(o, caches);
            }

        } else if (bean instanceof Map) {
            for (Entry<Object, Object> entry : ((Map<Object, Object>) bean).entrySet()) {
                initXlsBean(entry.getKey(), caches);
                initXlsBean(entry.getValue(), caches);
            }
        } else if (KernelClass.isCustomClass(bean.getClass()) && caches.add(bean.getClass())) {

            KernelReflect.doWithDeclaredFields(bean.getClass(), new CallbackBreak<Field>() {

                @Override
                public void doWith(Field template) throws BreakException {
                    if ((template.getModifiers() & (Modifier.STATIC | Modifier.FINAL | Modifier.TRANSIENT)) == 0) {
                        template.setAccessible(true);
                        if (KernelReflect.get(bean, template) == null) {
                            if (template.getType().isArray() || Collection.class.isAssignableFrom(template.getType())) {
                                KernelReflect.set(bean, template, DynaBinder.INSTANCE.bind(mapArray, null, template.getGenericType()));

                            } else {
                                KernelReflect.set(bean, template, DynaBinder.INSTANCE.bind(mapArray[0], null, template.getGenericType()));
                            }
                        }
                    }

                    initXlsBean(KernelReflect.get(bean, template), caches);
                }
            });
        }
    }
}
