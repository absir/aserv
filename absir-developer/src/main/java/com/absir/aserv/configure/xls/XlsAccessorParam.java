/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-17 下午1:23:18
 */
package com.absir.aserv.configure.xls;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;

import com.absir.aserv.system.helper.HelperString;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelLang.ObjectTemplate;
import com.absir.core.kernel.KernelString;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class XlsAccessorParam extends XlsAccessor {

	/** xlsClass */
	Class<? extends XlsBase> xlsClass;

	/**
	 * @param field
	 * @param beanClass
	 * @param xlsBase
	 */
	public XlsAccessorParam(Field field, Class<?> cls, Class<?> beanClass) {
		super(field, cls, beanClass);
		if (XlsBase.class.isAssignableFrom(beanClass)) {
			xlsClass = (Class<? extends XlsBase>) beanClass;
		}
	}

	/**
	 * @param value
	 * @return
	 */
	protected Object getParamValues(String value) {
		if (KernelString.isEmpty(value)) {
			return null;
		}

		String[] params = HelperString.split(value, ';');
		if (params.length == 1) {
			params = HelperString.split(value, ',');
		}

		Object paramsValue = params;
		if (xlsClass != null) {
			XlsDao xlsDao = XlsUtils.getXlsDao(xlsClass);
			int length = params.length;
			List<Object> values = new ArrayList<Object>(length);
			for (int i = 0; i < length; i++) {
				Object obj = xlsDao.find(params[i]);
				if (obj != null) {
					values.add(obj);
				}
			}

			paramsValue = values;
		}

		return DynaBinder.INSTANCE.bind(paramsValue, null, getField().getGenericType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.configure.xls.XlsAccessorUtils.XlsAccessorBean#
	 * setObject(java.lang.Object, java.lang.Object,
	 * com.absir.aserv.configure.xls.XlsBase,
	 * com.absir.core.kernel.KernelLang.ObjectTemplate)
	 */
	@Override
	public void setObject(Object obj, Object cell, XlsBase xlsBase, ObjectTemplate<Boolean> empty) {
		HSSFCell hssfCell = (HSSFCell) cell;
		if (empty != null && empty.object) {
			if (!KernelString.isEmpty(XlsAccessorUtils.getCellValue(hssfCell))) {
				empty.object = false;
			}
		}

		getAccessor().set(obj, getParamValues(XlsAccessorUtils.getCellValue(hssfCell)));
	}

	/**
	 * @param value
	 * @return
	 */
	protected String getValueParams(Object value) {
		if (value == null) {
			return null;
		}

		Object[] params = DynaBinder.to(value, Object[].class);
		if (xlsClass != null) {
			int length = params.length;
			Object[] values = new Object[length];
			for (int i = 0; i < length; i++) {
				values[i] = ((XlsBase) params[i]).getId();
			}

			params = values;
		}

		return KernelString.implode(params, ',');
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.configure.xls.XlsAccessor#writeXlsCells(java.util
	 * .List, java.lang.Object, com.absir.aserv.configure.xls.XlsBase)
	 */
	@Override
	public void writeXlsCells(List<XlsCell> xlsCells, Object obj, XlsBase xlsBase) {
		xlsCells.add(obj == null ? new XlsCellBase() : new XlsCellObject(getValueParams(getAccessor().get(obj)), xlsBase));
	}
}
