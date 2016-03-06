/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-24 下午6:21:25
 */
package com.absir.aserv.configure.xls;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.base.Base;
import com.absir.core.helper.HelperFile;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "unchecked" })
public class XlsBase extends Base<Serializable> {

	/** id */
	@JaLang("编号")
	protected Serializable id;

	/**
	 * 初始化
	 */
	protected void initializing() {
	}

	/**
	 * @return
	 */
	public Serializable getId() {
		return id;
	}

	/**
	 * @param cls
	 * @return
	 */
	protected boolean is(Class<?> cls) {
		return DynaBinderUtils.is(cls) || XlsBase.class.isAssignableFrom(cls);
	}

	/** XLS_DIR */
	public static String XLS_DIR = "xls/";

	/** XLS_SUFFIX */
	public static String XLS_SUFFIX = ".xls";

	/**
	 * @param workbook
	 * @return
	 * @throws IOException
	 */
	protected HSSFWorkbook getHssfWorkbook(String workbook) throws IOException {
		return new HSSFWorkbook(HelperFile.openInputStream(new File(BeanFactoryUtils.getBeanConfig().getResourcePath() + XLS_DIR
				+ workbook + XLS_SUFFIX)));
	}

	/**
	 * @param obj
	 * @param toClass
	 * @return
	 */
	protected <T> T read(HSSFCell hssfCell, Class<T> toClass) {
		Object value = XlsAccessorUtils.getCellObject(hssfCell);
		if (XlsBase.class.isAssignableFrom(toClass)) {
			return (T) XlsUtils.findXlsBean((Class<? extends XlsBase>) toClass, value);
		}

		return DynaBinderUtils.to(value, toClass);
	}

	/**
	 * @param hssfCell
	 * @param obj
	 */
	protected void write(HSSFCell hssfCell, Object obj) {
		if (obj == null) {
			return;
		}

		if (obj instanceof XlsBase) {
			obj = ((XlsBase) obj).getId();
		}

		hssfCell.setCellValue(DynaBinderUtils.to(obj, String.class));
	}
}
