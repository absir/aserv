/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-25 下午3:39:39
 */
package com.absir.aserv.configure.xls;

import org.apache.poi.hssf.usermodel.HSSFCell;

public class XlsCellValue extends XlsCellBase {

    private String value;

    public XlsCellValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void writeHssfCell(HSSFCell hssfCell) {
        hssfCell.setCellValue(value);
    }
}
