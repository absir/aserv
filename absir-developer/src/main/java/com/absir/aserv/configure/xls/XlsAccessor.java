/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-24 下午4:56:45
 */
package com.absir.aserv.configure.xls;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.core.kernel.KernelLang.ObjectTemplate;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAccessor;
import com.absir.core.util.UtilAccessor.Accessor;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author absir
 *
 */
public class XlsAccessor {

    /**
     * field
     */
    private Field field;

    /**
     * accessor
     */
    private Accessor accessor;

    /**
     * @param field
     * @param cls
     * @param beanClass
     */
    public XlsAccessor(Field field, Class<?> cls, Class<?> beanClass) {
        if (field != null) {
            this.field = field;
            this.accessor = UtilAccessor.getAccessor(cls, field);
        }
    }

    /**
     * @return
     */
    public Field getField() {
        return field;
    }

    /**
     * @return
     */
    public Accessor getAccessor() {
        return accessor;
    }

    /**
     * @return
     */
    public boolean isMulti() {
        return false;
    }

    /**
     * @return
     */
    public int getColumnCount() {
        return 1;
    }

    /**
     * @param hssfSheet
     * @param cells
     * @param firstRow
     * @param firstColumn
     * @param lastRow
     * @return
     */
    public int readHssfSheet(HSSFSheet hssfSheet, List<Object> cells, int firstRow, int firstColumn, int lastRow) {
        HSSFRow row = hssfSheet.getRow(firstRow);
        cells.add(row == null ? null : row.getCell(firstColumn));
        return firstRow + 1;
    }

    /**
     * @param obj
     * @param cell
     * @param xlsBase
     * @param empty
     */
    public void setObject(Object obj, Object cell, XlsBase xlsBase, ObjectTemplate<Boolean> empty) {
        HSSFCell hssfCell = (HSSFCell) cell;
        if (empty != null && empty.object) {
            if (!KernelString.isEmpty(XlsAccessorUtils.getCellValue(hssfCell))) {
                empty.object = false;
            }
        }

        accessor.set(obj, xlsBase.read(hssfCell, field.getType()));
    }

    /**
     * @return
     */
    public XlsCell getHeader() {
        if (field == null) {
            return new XlsCell();
        }

        String name = field.getName();
        JaLang jaLang = field.getAnnotation(JaLang.class);
        return new XlsCellValue(jaLang == null || "".equals(jaLang.value()) ? field.getName() : jaLang.value() + "(" + name + ")");
    }

    /**
     * @param xlsCells
     * @param obj
     * @param xlsBase
     */
    public void writeXlsCells(List<XlsCell> xlsCells, Object obj, XlsBase xlsBase) {
        xlsCells.add(obj == null ? new XlsCellBase() : new XlsCellObject(accessor.get(obj), xlsBase));
    }
}
