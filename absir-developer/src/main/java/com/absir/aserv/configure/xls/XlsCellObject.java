/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-25 下午3:42:17
 */
package com.absir.aserv.configure.xls;

import org.apache.poi.hssf.usermodel.HSSFCell;

import com.absir.core.base.IBase;

/**
 * @author absir
 * 
 */
public class XlsCellObject extends XlsCellBase {

	/** obj */
	private Object obj;

	/** xlsBase */
	private XlsBase xlsBase;

	/**
	 * @param obj
	 * @param xlsBase
	 */
	public XlsCellObject(Object obj, XlsBase xlsBase) {
		this.obj = obj;
		this.xlsBase = xlsBase;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.configure.xls.XlsCell#wirteHssfCell(org.apache.poi.
	 * hssf.usermodel.HSSFCell)
	 */
	@Override
	public void wirteHssfCell(HSSFCell hssfCell) {
		if (obj != null) {
			if (obj instanceof IBase) {
				obj = ((IBase<?>) obj).getId();
			}

			if (obj != null) {
				if (obj.getClass() == Boolean.class) {
					obj = (Boolean) obj ? "1" : "0";
				}

				xlsBase.write(hssfCell, obj.toString());
			}
		}
	}
}
