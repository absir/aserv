/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月7日 下午3:18:49
 */
package com.absir.aserv.game.service;

import com.absir.aserv.configure.xls.*;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.system.helper.HelperString;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.util.UtilAccessor;
import com.absir.core.util.UtilAccessor.Accessor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("unchecked")
@Base
@Bean
public class CsvService {

    public static final CsvService ME = BeanFactoryUtils.get(CsvService.class);

    public static final byte[] BOM_BYTES = new byte[]{(byte) 239, (byte) 187, (byte) 191};

    public static final String BOM_STR = new String(BOM_BYTES);

    public static final char[] BOM_CAHRS = BOM_STR.toCharArray();

    public static final String[] SEARCH_STRS = new String[]{",", "\r", "\n"};

    public static final String[] TO_STRS = new String[]{"，", "\\r", "\\n"};

    protected static final Logger LOGGER = LoggerFactory.getLogger(CsvService.class);

    public void write(Class<?> beanClass, Collection<?> bases, boolean bom, final boolean header,
                      OutputStream outputStream, final Writer writer) throws IOException {
        Object first = bases.iterator().next();
        final Class<?> xlsClass = beanClass == null ? first.getClass() : beanClass;
        XlsBase xlsBase = first instanceof XlsBase ? (XlsBase) first : XlsUtils.XLS_BASE;
        XlsAccessorContext accessorContext = xlsClass == null ? null : new XlsAccessorContext(xlsClass, xlsBase);
        int columnCount = accessorContext.getHeader().getColumnCount();
        if (columnCount == 0 || columnCount == accessorContext.getAccessors().size()) {
            if (bom) {
                // 打印BOM
                outputStream.write(BOM_BYTES);
                outputStream.flush();
            }

            // 打印头信息
            boolean[] accessors = new boolean[columnCount];
            for (int i = 0; i < columnCount; i++) {
                XlsAccessor accessor = accessorContext.getAccessors().get(i);
                Field field = accessor.getField();
                if (field != null && field.getAnnotation(JsonIgnore.class) == null) {
                    accessors[i] = true;
                    if (header) {
                        writer.append(field.getName());
                        writer.append(',');
                    }
                }
            }

            final List<Accessor> fieldAccessors = new ArrayList<Accessor>();
            KernelReflect.doWithDeclaredFields(xlsClass, new CallbackBreak<Field>() {

                @Override
                public void doWith(Field field) throws BreakException {
                    if (!Modifier.isStatic(field.getModifiers()) && Modifier.isTransient(field.getModifiers())
                            && field.getAnnotation(JsonIgnore.class) == null) {
                        Accessor accessor = UtilAccessor.getAccessor(xlsClass, field);
                        if (accessor.getGetter() == null
                                || accessor.getGetter().getAnnotation(JsonIgnore.class) == null) {
                            fieldAccessors.add(accessor);
                            if (header) {
                                try {
                                    writer.append(field.getName());
                                    writer.append(',');

                                } catch (Exception e) {
                                    LOGGER.error("write header " + field, e);
                                }
                            }
                        }
                    }
                }
            });

            XlsCell xlsCellHeader;
            int[] rowCounts = new int[]{0, Integer.MAX_VALUE};
            for (Object base : bases) {
                // 打印内容
                HSSFSheet hssfSheet = new HSSFWorkbook().createSheet();
                xlsCellHeader = new XlsCell();
                rowCounts[0] = 0;
                accessorContext.writeXlsCellBean(xlsCellHeader, base, xlsBase, rowCounts);
                XlsAccessorUtils.writeHssfSheet(hssfSheet, xlsCellHeader);
                HSSFRow row = hssfSheet.getRow(0);
                writer.append("\r\n");
                for (int i = 0; i < columnCount; i++) {
                    if (accessors[i]) {
                        String value = row.getCell(i).getStringCellValue();
                        writer.append(
                                value == null ? "" : HelperString.replaceEachRepeatedly(value, SEARCH_STRS, TO_STRS));
                        writer.append(",");
                    }
                }

                for (Accessor accessor : fieldAccessors) {
                    writer.append(accessor.get(base).toString());
                    writer.append(",");
                }
            }

        } else {
            LOGGER.warn("write csv " + xlsBase);
        }

        writer.flush();
    }

    public void write(long updateTime, ZipOutputStream outputStream, boolean bom, boolean header,
                      PrintWriter printWriter) throws IOException {
        JdbcCondition jdbcCondition = null;
        if (updateTime > 0) {
            jdbcCondition = new JdbcCondition();
            jdbcCondition.getConditions().add("o.updateTime > ?");
            jdbcCondition.getConditions().add(updateTime);
        }

        for (Entry<String, Class<?>> entry : XlsCrudSupply.ME.getEntityNameMapClass()) {
            Collection<? extends XlsBase> bases = jdbcCondition == null
                    ? XlsUtils.getXlsBeans(XlsCrudSupply.ME.getEntityClass(entry.getKey()))
                    : XlsCrudSupply.ME.list(entry.getKey(), jdbcCondition, null, 0, 0);
            if (bases != null && !bases.isEmpty()) {
                outputStream.putNextEntry(new ZipEntry(entry.getKey() + ".csv"));
                write(entry.getValue(), bases, bom, header, outputStream, printWriter);
                outputStream.closeEntry();
            }
        }
    }

}
