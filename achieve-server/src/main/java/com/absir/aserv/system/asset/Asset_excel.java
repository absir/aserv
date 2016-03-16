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
import com.absir.bean.basis.Base;
import com.absir.server.value.Body;
import com.absir.server.value.Server;
import com.absir.servlet.InputRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

@Base
@Server
public class Asset_excel extends AssetServer {

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
                Map<String, Object> workmap = new TreeMap<String, Object>();
                try {
                    workmap.put("version", hssfWorkbook.getSummaryInformation().getOSVersion());

                } catch (Exception e) {
                }

                workmap.put("sheets", XlsAccessorUtils.getSheetList(hssfWorkbook, index, orientation));
                return workmap;

            } catch (IOException e) {
                return "read excel file:" + excel.getName() + " is error!";
            }
        }

        return "can not find upload excel!";
    }

    @Body
    public void test(HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println(HelperHtml.HTML4_DOC_TYPE);
        out.println("<form action='./' enctype='multipart/form-data' method='POST'>");
        out.println("<input type='file' name='excel'/>");
        out.println("<input type='submit'/>");
        out.println("</form>");
    }
}
