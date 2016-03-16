/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-27 上午10:11:22
 */
package com.absir.aserv.configure.xls;

import org.apache.poi.hssf.usermodel.HSSFCell;

import java.util.List;

public class XlsCellMerged extends XlsCell {

    private int basicRow;

    private int basicColumn;

    private XlsCell xlsCell;

    public XlsCellMerged(XlsCell xlsCell) {
        this.basicRow = xlsCell.getBasicRow();
        this.basicColumn = xlsCell.getBasicColumn();
        this.xlsCell = xlsCell;
        if (xlsCell.getChildren() != null) {
            for (List<XlsCell> cells : xlsCell.getChildren()) {
                addColumnCells(cells);
            }
        }
    }

    public int getBasicRow() {
        return basicRow;
    }

    public void setBasicRow(int basicRow) {
        this.basicRow = basicRow;
    }

    public int getBasicColumn() {
        return basicColumn;
    }

    public void setBasicColumn(int basicColumn) {
        this.basicColumn = basicColumn;
    }

    public void wirteHssfCell(HSSFCell hssfCell) {
        xlsCell.wirteHssfCell(hssfCell);
    }
}
