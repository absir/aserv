/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-27 下午4:22:42
 */
package com.absir.aserv.configure.xls;

import com.absir.aserv.lang.LangBundleImpl;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang.ObjectTemplate;
import org.apache.poi.hssf.usermodel.HSSFCell;

import java.io.Serializable;
import java.util.List;

/**
 * @author absir
 *
 */
@SuppressWarnings("unchecked")
public class XlsAccessorContext extends XlsAccessorBean {

    protected String beanName;

    /**
     * isXlsBean
     */
    private boolean isXlsBean;

    /**
     * idType
     */
    private Class<? extends Serializable> idType;

    /**
     * @param beanClass
     * @param xlsBase
     */
    public XlsAccessorContext(Class<?> beanClass, XlsBase xlsBase) {
        super(null, beanClass, beanClass);
        beanName = beanClass.getSimpleName();
        accessors = getXlsAccessors(beanClass, xlsBase);
        if (XlsBase.class.isAssignableFrom(beanClass)) {
            if (XlsBean.class.isAssignableFrom(beanClass)) {
                isXlsBean = true;
                idType = KernelClass.argumentClass(beanClass);

            } else {
                accessors.remove(0);
            }
        }
    }

    /**
     * @return the isXlsBean
     */
    public boolean isXlsBean() {
        return isXlsBean;
    }

    /**
     * @return the idType
     */
    public Class<? extends Serializable> getIdType() {
        return idType;
    }

    /**
     * @param xlsDao
     * @param cell
     * @param xlsBase
     * @param index
     * @return
     */
    public Object newInstance(XlsDao<?, Serializable> xlsDao, Object cell, XlsBase xlsBase, int index) {
        Object bean = null;
        Serializable id = null;
        if (isXlsBean) {
            if (accessors != null && accessors.size() > 0) {
                id = xlsBase.read(((List<HSSFCell>) cell).get(0), idType);
                bean = xlsDao == null ? null : xlsDao.get(id);
            }

        } else {
            id = index;
            bean = xlsDao == null ? null : xlsDao.get(index);
        }

        if (bean == null) {
            bean = KernelClass.newInstance(beanClass);
            if (bean instanceof XlsBase) {
                ((XlsBase) bean).id = id;
                if (LangBundleImpl.ME != null) {
                    bean = LangBundleImpl.ME.getLangProxy(beanName, bean);
                }
            }
        }

        return bean;
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
        if (accessors != null) {
            List<?> cells = (List<?>) cell;
            int size = accessors.size();
            for (int i = isXlsBean ? 1 : 0; i < size; i++) {
                accessors.get(i).setObject(obj, cells.get(i), xlsBase, empty);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.configure.xls.XlsAccessorUtils.XlsAccessorBean#
     * getHeader()
     */
    @Override
    public XlsCell getHeader() {
        XlsCell xlsCell = super.getHeader();
        if (xlsCell.getChildren() != null && xlsCell.getChildren().size() > 0) {
            List<XlsCell> cells = xlsCell.getChildren().get(0);
            int rowCount = 0;
            int rowOffset = 0;
            for (XlsCell cell : cells) {
                int iRowCount = cell.getRowCount();
                if (rowCount < iRowCount) {
                    rowCount = iRowCount;
                    rowOffset++;
                }
            }

            if (rowOffset > 1) {
                int size = cells.size();
                for (int i = 0; i < size; i++) {
                    XlsCell cell = cells.get(i);
                    rowOffset = rowCount - cell.getRowCount();
                    if (rowOffset > 0) {
                        XlsCellMerged cellMerged = new XlsCellMerged(cell);
                        cellMerged.setBasicRow(cellMerged.getBasicRow() + rowOffset);
                        cells.set(i, cellMerged);
                    }
                }
            }
        }

        return xlsCell;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.configure.xls.XlsAccessor#writeXlsCells(java.util
     * .List, java.lang.Object, com.absir.aserv.configure.xls.XlsBase)
     */
    @Override
    public void writeXlsCells(List<XlsCell> xlsCells, Object obj, XlsBase xlsBase) {
        writeXlsCells(xlsCells, obj, accessors, xlsBase);
    }

    /**
     * @param xlsCellHeader
     * @param obj
     * @param xlsBase
     * @param rowCounts
     * @return
     */
    public XlsCell writeXlsCellBean(XlsCell xlsCellHeader, Object obj, XlsBase xlsBase, int[] rowCounts) {
        if (obj == null) {
            super.writeXlsCells(xlsCellHeader.addColumnList(null), obj, xlsBase);

        } else {
            XlsCell xlsCell = null;
            if (accessors == null) {
                xlsCell = new XlsCellObject(obj, xlsBase);

            } else {
                xlsCell = new XlsCell();
                List<XlsCell> cells = xlsCell.addColumnList(null);
                for (XlsAccessor accessor : accessors) {
                    accessor.writeXlsCells(cells, obj, xlsBase);
                }
            }

            int rowCount = rowCounts[0] + xlsCell.getRowCount();
            if (rowCount > rowCounts[1]) {
                return xlsCell;
            }

            rowCounts[0] = rowCount;
            xlsCellHeader.addColumnList(null).add(xlsCell);
        }

        return null;
    }
}
