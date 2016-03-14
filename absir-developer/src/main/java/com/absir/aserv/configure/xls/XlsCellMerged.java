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

/**
 * @author absir
 *
 */
public class XlsCellMerged extends XlsCell {

    /**
     * row
     */
    private int basicRow;

    /**
     * column
     */
    private int basicColumn;

    /**
     * xlsCell
     */
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

    /**
     * @return the basicRow
     */
    public int getBasicRow() {
        return basicRow;
    }

    /**
     * @param basicRow the basicRow to set
     */
    public void setBasicRow(int basicRow) {
        this.basicRow = basicRow;
    }

    /**
     * @return the basicColumn
     */
    public int getBasicColumn() {
        return basicColumn;
    }

    /**
     * @param basicColumn the basicColumn to set
     */
    public void setBasicColumn(int basicColumn) {
        this.basicColumn = basicColumn;
    }

    /**
     * @param hssfCell
     */
    public void wirteHssfCell(HSSFCell hssfCell) {
        xlsCell.wirteHssfCell(hssfCell);
    }
}
