/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年4月17日 上午10:31:59
 */
package com.absir.aserv.configure.xls;

import java.lang.reflect.Field;

import org.apache.poi.hssf.usermodel.HSSFCell;

import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelLang.ObjectTemplate;
import com.absir.core.kernel.KernelString;

/**
 * @author absir
 *
 */
public class XlsAccessorEnum extends XlsAccessor {

	/** values */
	protected String[] values;

	/** keys */
	protected String[] keys;

	/**
	 * @param field
	 * @param beanClass
	 * @param value
	 * @param key
	 */
	public XlsAccessorEnum(Field field, Class<?> cls, Class<?> beanClass, String[] value, String[] key) {
		super(field, cls, beanClass);
		if (value != null && value.length > 1) {
			values = value;
			if (key != null && key.length > 1) {
				keys = key;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.configure.xls.XlsAccessor#setObject(java.lang.Object,
	 * java.lang.Object, com.absir.aserv.configure.xls.XlsBase,
	 * com.absir.core.kernel.KernelLang.ObjectTemplate)
	 */
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

			String value = xlsBase.read(hssfCell, String.class);
			int index = KernelArray.index(values, value);
			Object key = index < 0 ? value : keys == null ? index : keys[index];
			getAccessor().set(obj, DynaBinder.INSTANCE.bind(key, null, getField().getGenericType()));
		}
	}
}
