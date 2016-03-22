/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-9-27 下午4:20:12
 */
package com.absir.aserv.configure.xls;

import com.absir.aserv.configure.xls.value.XaEnum;
import com.absir.aserv.configure.xls.value.XaIgnore;
import com.absir.aserv.configure.xls.value.XaParam;
import com.absir.aserv.configure.xls.value.XaReferenced;
import com.absir.aserv.system.helper.HelperAccessor;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang.ObjectTemplate;
import com.absir.core.kernel.KernelString;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class XlsAccessorBean extends XlsAccessor {

    protected Class<?> beanClass;

    protected List<XlsAccessor> accessors;

    public XlsAccessorBean(Field field, Class<?> cls, Class<?> beanClass) {
        super(field, cls, beanClass);
        this.beanClass = beanClass;
    }

    public XlsAccessorBean(Field field, Class<?> cls, Class<?> beanClass, XlsBase xlsBase) {
        this(field, cls, beanClass);
        if (!xlsBase.is(beanClass)) {
            XaReferenced xaReferenced = field.getAnnotation(XaReferenced.class);
            if (xaReferenced == null || xaReferenced.value()) {
                accessors = getXlsAccessors(beanClass, xlsBase);
            }
        }
    }

    public List<XlsAccessor> getAccessors() {
        return accessors;
    }

    protected List<XlsAccessor> getXlsAccessors(final Class<?> beanClass, final XlsBase xlsBase) {
        final List<XlsAccessor> xlsAccessors = new ArrayList<XlsAccessor>();
        for (Field field : HelperAccessor.getFields(beanClass, XaIgnore.class)) {
            Class<?> type = field.getType();
            if (type == Serializable.class || xlsBase.is(type)) {
                XaEnum xaEnum = field.getAnnotation(XaEnum.class);
                if (xaEnum == null) {
                    xlsAccessors.add(new XlsAccessor(field, beanClass, beanClass));

                } else {
                    xlsAccessors.add(new XlsAccessorEnum(field, beanClass, beanClass, xaEnum.value(), xaEnum.key()));
                }

            } else {
                if (type.isArray()) {
                    Class<?> componentType = type.getComponentType();
                    if ((field.getAnnotation(XaParam.class) != null && KernelClass.isBasicClass(componentType)) || XlsBase.class.isAssignableFrom(componentType)) {
                        xlsAccessors.add(new XlsAccessorParam(field, beanClass, componentType));

                    } else {
                        xlsAccessors.add(new XlsAccessorArray(field, beanClass, componentType, xlsBase));
                    }

                } else if (Collection.class.isAssignableFrom(type)) {
                    xlsAccessors.add(new XlsAccessorCollection(field, beanClass, KernelClass.componentClass(field.getGenericType()), xlsBase));

                } else if (Map.class.isAssignableFrom(type)) {
                    Type[] types = KernelClass.typeArguments(field.getGenericType());
                    Class<?> componentType = KernelClass.rawClass(types[0]);
                    if (field.getAnnotation(XaParam.class) != null && (KernelClass.isBasicClass(componentType) || XlsBase.class.isAssignableFrom(componentType))) {
                        xlsAccessors.add(new XlsAccessorParamMap(field, beanClass, componentType, KernelClass.rawClass(types[1])));

                    } else {
                        xlsAccessors.add(new XlsAccessorMap(field, beanClass, componentType, KernelClass.rawClass(types[1]), xlsBase));
                    }

                } else {
                    xlsAccessors.add(new XlsAccessorBean(field, beanClass, type, xlsBase));
                }
            }
        }

        return xlsAccessors.size() == 0 ? null : xlsAccessors;
    }

    @Override
    public boolean isMulti() {
        if (accessors != null) {
            for (XlsAccessor accessor : accessors) {
                if (accessor.isMulti()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int getColumnCount() {
        if (accessors == null) {
            return 1;
        }

        int column = 0;
        for (XlsAccessor accessor : accessors) {
            column += accessor.getColumnCount();
        }

        return column;
    }

    @Override
    public int readHssfSheet(HSSFSheet hssfSheet, List<Object> cells, int firstRow, int firstColumn, int lastRow) {
        //添加过滤注释行
        String cellValue = XlsAccessorUtils.getCellValue(hssfSheet.getRow(firstRow).getCell(firstColumn));
        if (cellValue != null && cellValue.startsWith("#")) {
            return firstRow + 1;
        }

        if (accessors != null) {
            int iColumn = firstColumn;
            for (XlsAccessor accessor : accessors) {
                if (!accessor.isMulti()) {
                    String value = XlsAccessorUtils.getCellValue(hssfSheet.getRow(firstRow).getCell(iColumn));
                    for (int i = firstRow + 1; i < lastRow; i++) {
                        HSSFRow row = hssfSheet.getRow(i);
                        String next = row == null ? null : XlsAccessorUtils.getCellValue(row.getCell(iColumn));
                        if (!(KernelString.isEmpty(next) || next.equals(value))) {
                            lastRow = i;
                        }
                    }
                }

                iColumn += accessor.getColumnCount();
            }
        }

        return readHssfSheet(hssfSheet, cells, accessors, firstRow, firstColumn, lastRow);
    }

    protected int readHssfSheet(HSSFSheet hssfSheet, List<Object> cells, List<XlsAccessor> accessors, int firstRow, int firstColumn, int lastRow) {
        if (accessors == null) {
            return super.readHssfSheet(hssfSheet, cells, firstRow, firstColumn, lastRow);

        } else {
            List<Object> list = new ArrayList<Object>();
            int iColumn = firstColumn;
            for (XlsAccessor accessor : accessors) {
                accessor.readHssfSheet(hssfSheet, list, firstRow, iColumn, lastRow);
                iColumn += accessor.getColumnCount();
            }

            cells.add(list);
            return lastRow;
        }
    }

    @Override
    public void setObject(Object obj, Object cell, XlsBase xlsBase, ObjectTemplate<Boolean> empty) {
        getAccessor().set(obj, readObject(beanClass, cell, accessors, xlsBase, empty));
    }

    protected Object readObject(Class<?> beanClass, Object cell, List<XlsAccessor> accessors, XlsBase xlsBase, ObjectTemplate<Boolean> empty) {
        if (accessors == null) {
            HSSFCell hssfCell = (HSSFCell) cell;
            if (empty != null && empty.object) {
                if (!KernelString.isEmpty(XlsAccessorUtils.getCellValue(hssfCell))) {
                    empty.object = false;
                }
            }

            return xlsBase.read((HSSFCell) cell, beanClass);

        } else {
            Object bean = KernelClass.newInstance(beanClass);
            List<?> cells = (List<?>) cell;
            int size = accessors.size();
            for (int i = 0; i < size; i++) {
                accessors.get(i).setObject(bean, cells.get(i), xlsBase, empty);
            }

            return bean;
        }
    }

    @Override
    public XlsCell getHeader() {
        if (accessors != null) {
            XlsCell xlsCell = new XlsCell();
            List<XlsCell> cells = new ArrayList<XlsCell>();
            for (XlsAccessor accessor : accessors) {
                cells.add(accessor.getHeader());
            }

            xlsCell.addColumnList(cells);

            return xlsCell;
        }

        return super.getHeader();
    }

    @Override
    public void writeXlsCells(List<XlsCell> xlsCells, Object obj, XlsBase xlsBase) {
        if (obj != null) {
            obj = getAccessor().get(obj);
        }

        writeXlsCells(xlsCells, obj, accessors, xlsBase);
    }

    protected void writeXlsCells(List<XlsCell> xlsCells, Object obj, List<XlsAccessor> accessors, XlsBase xlsBase) {
        if (obj == null) {
            super.writeXlsCells(xlsCells, obj, xlsBase);

        } else {
            if (accessors == null) {
                xlsCells.add(new XlsCellObject(obj, xlsBase));

            } else {
                XlsCell xlsCell = new XlsCell();
                List<XlsCell> cells = xlsCell.addColumnList(null);
                for (XlsAccessor accessor : accessors) {
                    accessor.writeXlsCells(cells, obj, xlsBase);
                }

                xlsCells.add(xlsCell);
            }
        }
    }

}
