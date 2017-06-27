package com.absir.aserv.configure.csv;

import com.absir.aserv.configure.xls.*;
import com.absir.aserv.system.bean.value.JaIgnore;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.helper.HelperFile;
import com.absir.core.kernel.*;
import com.absir.core.util.UtilAbsir;
import com.absir.core.util.UtilAccessor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by absir on 2017/6/6.
 */
public class CsvUtils {

    public static final byte[] BOM_BYTES = new byte[]{(byte) 239, (byte) 187, (byte) 191};

    public static final String BOM_STR = new String(BOM_BYTES);

    public static final char[] BOM_CAHRS = BOM_STR.toCharArray();

    protected static final Logger LOGGER = LoggerFactory.getLogger(CsvUtils.class);

    protected static void writeFields(final Class<?> xlsClass, final int pathCount, final String fieldPath, final Class<?> beanClass, final boolean[] firstCells, final Set<String> fields, final List<UtilAccessor.Accessor> fieldAccessors, final Writer writer, final boolean header, final ICsvWriteHandler writeHandler) {
        KernelReflect.doWithDeclaredFields(beanClass, new KernelLang.CallbackBreak<Field>() {

            @Override
            public void doWith(Field field) throws KernelLang.BreakException {
                if (Modifier.isStatic(field.getModifiers()) || (fields != null && fields.contains(field.getName())) || field.getAnnotation(JsonIgnore.class) != null || field.getAnnotation(JaIgnore.class) != null) {
                    return;
                }

                UtilAccessor.Accessor accessor = KernelString.isEmpty(fieldPath) ? UtilAccessor.getAccessor(xlsClass, field) : UtilAccessor.getAccessorCls(xlsClass, fieldPath + field.getName());
                if (accessor.getGetter() != null) {
                    Class<?> type = accessor.getType();
                    if (pathCount < 3 && !XlsBase.class.isAssignableFrom(type) && !XlsBase.class.isAssignableFrom(KernelClass.componentClass(type)) && KernelClass.isCustomClass(type)) {
                        writeFields(xlsClass, pathCount + 1, KernelString.isEmpty(fieldPath) ? (field.getName() + '.') : (fieldPath + field.getName() + '.'), field.getType(), firstCells, null, fieldAccessors, writer, header, writeHandler);

                    } else {
                        fieldAccessors.add(accessor);
                        if (header) {
                            try {
                                if (firstCells[0]) {
                                    firstCells[0] = false;

                                } else {
                                    writeHandler.writeSplit(writer);
                                }

                                writeHandler.writeField(writer, fieldPath, accessor, null);

                            } catch (Exception e) {
                                LOGGER.error("write header " + field, e);
                            }
                        }
                    }
                }
            }
        });
    }

    public static void write(Class<?> beanClass, Collection<?> bases, boolean bom, final boolean header,
                             OutputStream outputStream, ICsvWriteHandler handler) throws IOException {

        if (handler == null) {
            handler = CsvWriteHandler.Default;
        }

        final ICsvWriteHandler writeHandler = handler;
        if (bom) {
            // 打印BOM
            outputStream.write(BOM_BYTES);
            outputStream.flush();
        }

        final Writer writer = new OutputStreamWriter(outputStream, KernelCharset.UTF8);
        Iterator<?> iterator = bases.iterator();
        Object first = iterator.hasNext() ? iterator.next() : null;
        if (first == null) {
            return;
        }

        final Class<?> xlsClass = beanClass == null ? first.getClass() : beanClass;
        XlsBase xlsBase = first instanceof XlsBase ? (XlsBase) first : XlsUtils.XLS_BASE;
        XlsAccessorContext accessorContext = new XlsAccessorContext(xlsClass, xlsBase);
        int columnCount = accessorContext.getHeader().getColumnCount();
        if (columnCount != accessorContext.getAccessors().size()) {
            columnCount = accessorContext.isXlsBean() ? 1 : 0;
        }

        Set<String> fields = null;
        boolean firstCell = true;
        // 打印头信息
        boolean[] accessorRows = new boolean[columnCount];
        for (int i = 0; i < columnCount; i++) {
            XlsAccessor accessor = accessorContext.getAccessors().get(i);
            Field field = accessor.getField();
            if (field == null) {
                continue;
            }

            if (XlsAccessorBean.class.isAssignableFrom(accessor.getClass()) || Enum.class.isAssignableFrom(field.getType())) {
                continue;
            }

            if (fields == null) {
                fields = new HashSet<String>();
            }

            fields.add(field.getName());
            if (field.getAnnotation(JsonIgnore.class) != null || field.getAnnotation(JaIgnore.class) != null) {
                continue;
            }

            accessorRows[i] = true;
            if (header) {
                if (firstCell) {
                    firstCell = false;

                } else {
                    writeHandler.writeSplit(writer);
                }

                Class<?> idType = null;
                if (i == 0 && accessorContext.isXlsBean()) {
                    idType = accessorContext.getIdType();
                    if (idType.isAssignableFrom(accessor.getAccessor().getType())) {
                        idType = null;
                    }
                }

                writeHandler.writeField(writer, null, accessor.getAccessor(), idType);
            }
        }

        final boolean[] firstCells = new boolean[]{firstCell};
        final List<UtilAccessor.Accessor> fieldAccessors = new ArrayList<UtilAccessor.Accessor>();
        writeFields(xlsClass, 0, null, xlsClass, firstCells, fields, fieldAccessors, writer, header, writeHandler);

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
            firstCell = true;
            for (int i = 0; i < columnCount; i++) {
                if (accessorRows[i]) {
                    if (firstCell) {
                        firstCell = false;

                    } else {
                        writeHandler.writeSplit(writer);
                    }

                    String value = row.getCell(i).getStringCellValue();
                    writeHandler.writeValue(writer, value);
                }
            }

            for (UtilAccessor.Accessor accessor : fieldAccessors) {
                if (firstCell) {
                    firstCell = false;

                } else {
                    writeHandler.writeSplit(writer);
                }

                Object value = accessor.get(base);
                if (value != null) {
                    if (value instanceof XlsBase) {
                        value = ((XlsBase) value).getId();

                    } else if (value instanceof Enum) {
                        value = ((Enum) value).ordinal();

                    } else if (value.getClass() == Boolean.class) {
                        value = value == Boolean.TRUE ? 1 : 0;

                    } else if (value.getClass() == Float.class) {
                        value = UtilAbsir.floatIntValue((Float) value);

                    } else {
                        Class<?> valueType = value.getClass();
                        if ((valueType.isArray() || Collection.class.isAssignableFrom(valueType)) && XlsBase.class.isAssignableFrom(KernelClass.componentClass(valueType))) {
                            XlsBase[] xlsBases = DynaBinder.to(value, XlsBase[].class);
                            if (xlsBases == null) {
                                value = null;
                            } else {
                                StringBuilder stringBuilder = null;
                                for (XlsBase xBase : xlsBases) {
                                    if (stringBuilder == null) {
                                        stringBuilder = new StringBuilder();

                                    } else {
                                        stringBuilder.append(';');
                                    }

                                    stringBuilder.append(xBase.getId());
                                }

                                value = stringBuilder == null ? null : stringBuilder.toString();
                            }
                        }
                    }
                }

                writeHandler.writeValue(writer, value == null ? null : value.toString());
            }
        }

        writer.flush();
    }

    public static void writeFile(Class<?> beanClass, Collection<?> bases, boolean bom, final boolean header,
                                 File file, final ICsvWriteHandler writeHandler) throws IOException {
        FileOutputStream outputStream = HelperFile.openOutputStream(file);
        try {
            write(beanClass, bases, bom, header, outputStream, writeHandler);

        } finally {
            outputStream.close();
        }
    }

    public interface ICsvWriteHandler {

        public void writeSplit(Writer writer) throws IOException;

        public void writeField(Writer writer, String fieldPath, UtilAccessor.Accessor accessor, Class<?> idType) throws IOException;

        public void writeValue(Writer writer, String value) throws IOException;

    }

    public enum CsvWriteHandler implements ICsvWriteHandler {

        Default {
            @Override
            public void writeSplit(Writer writer) throws IOException {
                writer.append(',');
            }

            @Override
            public void writeField(Writer writer, String fieldPath, UtilAccessor.Accessor accessor, Class<?> idType) throws IOException {
                Class<?> type = idType == null ? accessor.getType() : idType;
                if (XlsBase.class.isAssignableFrom(type)) {
                    type = KernelClass.typeClass(type, XlsBean.ID_VARIABLE);
                }

                if (type.isEnum()) {
                    type = Enum.class;
                }

                if (CharSequence.class.isAssignableFrom(type)) {
                    writer.append(accessor.getField().getName());

                } else {
                    if (KernelClass.isBasicClass(type)) {
                        writer.append(KernelClass.isMatchableFrom(type, boolean.class) ? 'B' : Character.toLowerCase(type.getSimpleName().charAt(0)));

                    } else {
                        if (type.isArray() || Collection.class.isAssignableFrom(type)) {
                            type = KernelClass.componentClass(type);
                            if (XlsBase.class.isAssignableFrom(type)) {
                                type = KernelClass.typeClass(type, XlsBean.ID_VARIABLE);
                            }

                            writer.append('[');
                            if (KernelClass.isBasicClass(type)) {
                                writer.append(KernelClass.isMatchableFrom(type, boolean.class) ? 'B' : Character.toLowerCase(type.getSimpleName().charAt(0)));

                            } else {
                                writer.append(type.getSimpleName().toLowerCase());
                            }

                        } else {
                            writer.append(type.getSimpleName().toLowerCase());
                        }
                    }

                    writer.append('#');
                    if (!KernelString.isEmpty(fieldPath)) {
                        writer.append(fieldPath.replace('.', '_'));
                        writer.append('_');
                    }

                    writer.append(accessor.getField().getName());
                }
            }

            @Override
            public void writeValue(Writer writer, String value) throws IOException {
                if (value != null) {
                    if (value.indexOf('\\') >= 0 || value.indexOf(',') >= 0) {
                        value = value.replace("\\", "\\\\");
                        value = value.replace(",", "\\.");
                        writer.append(value);

                    } else {
                        writer.append(value);
                    }
                }
            }

        };

    }

    public static String defaultReadValue(String value) {
        if (value != null) {
            if (value.indexOf('\\') >= 0) {
                StringBuilder stringBuilder = new StringBuilder();
                boolean transferred = false;
                int length = value.length();
                char chr;
                for (int i = 0; i < length; i++) {
                    chr = value.charAt(i);
                    if (transferred) {
                        transferred = false;
                        switch (chr) {
                            case '.':
                                stringBuilder.append(',');
                                break;
                            case '\\':
                                stringBuilder.append('\\');
                                break;
                            default:
                                stringBuilder.append('\\');
                                stringBuilder.append(chr);
                                break;
                        }

                    } else if (chr == '\\') {
                        transferred = true;

                    } else {
                        stringBuilder.append(chr);
                    }
                }

                return stringBuilder.toString();
            }
        }

        return value;
    }

}
