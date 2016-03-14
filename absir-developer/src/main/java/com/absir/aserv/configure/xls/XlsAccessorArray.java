/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-27 下午4:23:16
 */
package com.absir.aserv.configure.xls;

import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelArray.ArrayAccessor;
import com.absir.core.kernel.KernelLang.ObjectTemplate;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author absir
 *
 */
public class XlsAccessorArray extends XlsAccessorBean {
    /**
     * @param field
     * @param beanClass
     * @param xlsBase
     */
    public XlsAccessorArray(Field field, Class<?> cls, Class<?> beanClass, XlsBase xlsBase) {
        super(field, cls, beanClass, xlsBase);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.configure.xls.XlsAccessor#isMulti()
     */
    @Override
    public boolean isMulti() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.configure.xls.XlsAccessor#readHssfSheet(org.apache
     * .poi.hssf.usermodel.HSSFSheet, java.util.List, int, int, int)
     */
    @Override
    public int readHssfSheet(HSSFSheet hssfSheet, List<Object> cells, int firstRow, int firstColumn, int lastRow) {
        List<Object> list = new ArrayList<Object>();
        while (firstRow < lastRow) {
            firstRow = super.readHssfSheet(hssfSheet, list, firstRow, firstColumn, lastRow);
        }

        cells.add(list);
        return lastRow;
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
        List<?> cells = (List<?>) cell;
        int size = cells.size();
        List<Object> beanList = new ArrayList<Object>(size);
        for (int i = 0; i < size; i++) {
            ObjectTemplate<Boolean> isEmpty = new ObjectTemplate<Boolean>(true);
            Object element = readObject(beanClass, cells.get(i), accessors, xlsBase, isEmpty);
            if (element != null && !isEmpty.object) {
                beanList.add(element);
            }
        }

        if (empty != null && empty.object) {
            if (beanList.size() > 0) {
                empty.object = false;
            }
        }

        getAccessor().set(obj, DynaBinder.INSTANCE.bind(beanList, null, getField().getGenericType()));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.configure.xls.XlsAccessor#writeXlsCells(java.util
     * .List, java.lang.Object, com.absir.aserv.configure.xls.XlsBase)
     */
    @Override
    public void writeXlsCells(List<XlsCell> xlsCells, Object obj, XlsBase xlsBase) {
        if (obj != null) {
            obj = getAccessor().get(obj);
        }

        XlsCell xlsCell = new XlsCell();
        xlsCells.add(xlsCell);
        if (obj == null) {
            super.writeXlsCells(xlsCell.addColumnList(null), null, accessors, xlsBase);

        } else {
            int length = Array.getLength(obj);
            if (length == 0) {
                super.writeXlsCells(xlsCell.addColumnList(null), null, accessors, xlsBase);

            } else {
                ArrayAccessor arrayAccessor = KernelArray.forComponentType(beanClass);
                for (int i = 0; i < length; i++) {
                    super.writeXlsCells(xlsCell.addColumnList(null), arrayAccessor.get(obj, i), accessors, xlsBase);
                }
            }
        }
    }
}
