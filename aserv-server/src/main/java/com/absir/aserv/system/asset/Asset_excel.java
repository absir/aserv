/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-6 下午3:33:33
 */
package com.absir.aserv.system.asset;

import com.absir.aserv.configure.xls.XlsAccessorUtils;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.system.helper.HelperHtml;
import com.absir.bean.lang.LangCodeUtils;
import com.absir.server.route.invoker.InvokerResolverErrors;
import com.absir.server.value.Body;
import com.absir.servlet.InputRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

public class Asset_excel extends AssetServer {

    public static final String READ_ERROR = LangCodeUtils.get("读取失败", Asset_excel.class);

    public static final String FILE_NOT_FOUND = LangCodeUtils.get("文件未找到", Asset_excel.class);

    @Body
    public Object route(int index, InputRequest input) {
        return route(index, false, input);
    }

    @Body
    public Object route(int index, boolean orientation, InputRequest input) {
        FileItem excel = UploadCrudFactory.getUploadFile(input, "excel");
        if (excel != null) {
            try {
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook(excel.getInputStream());
                Map<String, Object> workMap = new TreeMap<String, Object>();
                try {
                    workMap.put("version", hssfWorkbook.getSummaryInformation().getOSVersion());

                } catch (Exception e) {
                }

                workMap.put("sheets", XlsAccessorUtils.getSheetList(hssfWorkbook, index, orientation));
                return workMap;

            } catch (IOException e) {
                InvokerResolverErrors.onError("excel", READ_ERROR, null, null);
            }
        }

        InvokerResolverErrors.onError("excel", FILE_NOT_FOUND, null, null);
        return null;
    }

    @Body
    public void upload(HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println(HelperHtml.HTML4_DOC_TYPE);
        out.println("<form action='./' enctype='multipart/form-data' method='POST'>");
        out.println("<input type='file' name='excel'/>");
        out.println("<input type='submit'/>");
        out.println("</form>");
    }
}
