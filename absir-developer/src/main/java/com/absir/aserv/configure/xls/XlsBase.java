/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-24 下午6:21:25
 */
package com.absir.aserv.configure.xls;

import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.base.Base;
import com.absir.core.helper.HelperFile;
import com.absir.core.kernel.KernelLang;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

@SuppressWarnings({"unchecked"})
public class XlsBase extends Base<Serializable> {

    public static final String XLS_DIR = "xls/";

    public static final String XLS_SUFFIX = ".xls";
    private static KernelLang.GetTemplate2<HSSFWorkbook, XlsBase, String> readHssfWorkbook;
    @JaLang("编号")
    protected Serializable id;

    public static void setReadHssfWorkbook(KernelLang.GetTemplate2<HSSFWorkbook, XlsBase, String> readHssfWorkbook) {
        XlsBase.readHssfWorkbook = readHssfWorkbook;
    }

    /**
     * 初始化
     */
    protected void initializing() {
    }

    public Serializable getId() {
        return id;
    }

    protected boolean is(Class<?> cls) {
        return DynaBinderUtils.is(cls) || XlsBase.class.isAssignableFrom(cls);
    }

    protected HSSFWorkbook getHssfWorkbook(String workbook) throws IOException {
        HSSFWorkbook hssfWorkbook = null;
        if (readHssfWorkbook != null) {
            hssfWorkbook = readHssfWorkbook.getWith(this, workbook);
        }

        if (hssfWorkbook == null) {
            return new HSSFWorkbook(HelperFile.openInputStream(new File(BeanFactoryUtils.getBeanConfigClassPath() + XLS_DIR
                    + workbook + XLS_SUFFIX)));
        }

        return hssfWorkbook;
    }

    protected <T> T read(HSSFCell hssfCell, Class<T> toClass) {
        Object value = XlsAccessorUtils.getCellObject(hssfCell);
        if (XlsBase.class.isAssignableFrom(toClass)) {
            return (T) XlsUtils.findXlsBean((Class<? extends XlsBase>) toClass, value);
        }

        return DynaBinderUtils.to(value, toClass);
    }

    protected void write(HSSFCell hssfCell, Object obj) {
        if (obj == null) {
            return;
        }

        if (obj instanceof XlsBase) {
            obj = ((XlsBase) obj).getId();
        }

        hssfCell.setCellValue(DynaBinderUtils.to(obj, String.class));
    }
}
