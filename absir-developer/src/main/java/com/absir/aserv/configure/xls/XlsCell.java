/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-24 下午2:08:49
 */
package com.absir.aserv.configure.xls;

import com.absir.core.kernel.KernelDyna;
import org.apache.poi.hssf.usermodel.HSSFCell;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"rawtypes"})
public class XlsCell {

    private List<List<XlsCell>> children;

    public static int getLineRowCount(List<XlsCell> xlsCells) {
        int rowCount = 0;
        for (XlsCell xlsCell : xlsCells) {
            int iRowCount = xlsCell.getRowCount();
            if (rowCount < iRowCount) {
                rowCount = iRowCount;
            }
        }

        return rowCount;
    }

    public static int getLineColumnCount(List<XlsCell> xlsCells) {
        int columnCount = 0;
        for (XlsCell xlsCell : xlsCells) {
            columnCount += xlsCell.getColumnCount();
        }

        return columnCount;
    }

    public List<List<XlsCell>> getChildren() {
        return children;
    }

    public void addColumnCells(List<XlsCell> cells) {
        if (children == null) {
            children = new ArrayList<List<XlsCell>>();
        }

        children.add(cells);
    }

    public List<XlsCell> addColumnList(List columns) {
        List<XlsCell> cells = new ArrayList<XlsCell>();
        if (columns != null) {
            for (Object column : columns) {
                if (column instanceof XlsCell) {
                    cells.add((XlsCell) column);

                } else {
                    cells.add(new XlsCellValue(KernelDyna.to(column, String.class)));
                }
            }
        }

        addColumnCells(cells);
        return cells;
    }

    public int getBasicRow() {
        return 0;
    }

    public int getBasicColumn() {
        return 0;
    }

    public void wirteHssfCell(HSSFCell hssfCell) {

    }

    public final int getRowCount() {
        int rowCount = getBasicRow();
        if (children != null) {
            for (List<XlsCell> xlsCells : children) {
                rowCount += getLineRowCount(xlsCells);
            }
        }

        return rowCount;
    }

    public final int getColumnCount() {
        int columnCount = getBasicColumn();
        if (children != null) {
            for (List<XlsCell> xlsCells : children) {
                int iColumnCount = getLineColumnCount(xlsCells);
                if (columnCount < iColumnCount) {
                    columnCount = iColumnCount;
                }
            }
        }

        return columnCount;
    }
}
