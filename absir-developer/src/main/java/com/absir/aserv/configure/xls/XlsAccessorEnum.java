/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月17日 上午10:31:59
 */
package com.absir.aserv.configure.xls;

import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelLang.ObjectTemplate;
import com.absir.core.kernel.KernelString;
import org.apache.poi.hssf.usermodel.HSSFCell;

import java.lang.reflect.Field;

public class XlsAccessorEnum extends XlsAccessor {

    protected String[] values;

    protected String[] keys;

    public XlsAccessorEnum(Field field, Class<?> cls, Class<?> beanClass, String[] value, String[] key) {
        super(field, cls, beanClass);
        if (value != null && value.length > 1) {
            values = value;
            if (key != null && key.length > 1) {
                keys = key;
            }
        }
    }

    @Override
    public void setObject(Object obj, Object cell, XlsBase xlsBase, ObjectTemplate<Boolean> empty) {
        if (values == null) {
            super.setObject(obj, cell, xlsBase, empty);

        } else {
            HSSFCell hssfCell = (HSSFCell) cell;
            if (empty != null && empty.object) {
                if (!KernelString.isEmpty(XlsAccessorUtils.getCellValue(hssfCell))) {
                    empty.object = false;
                }
            }

            String value = xlsBase.read(hssfCell, getField(), String.class);
            int index = KernelArray.index(values, value);
            Object key = index < 0 ? value : keys == null ? index : keys[index];
            getAccessor().set(obj, DynaBinder.INSTANCE.bind(key, null, getField().getGenericType()));
        }
    }
}
