/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-6 下午3:05:14
 */
package com.absir.aserv.system.asset;

import com.absir.aserv.menu.value.MaPermission;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.crud.RichCrudFactory;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.system.service.SecurityService;
import com.absir.core.helper.HelperFileName;
import com.absir.server.in.Input;
import com.absir.server.value.Before;
import com.absir.server.value.Body;
import com.absir.server.value.Nullable;
import com.absir.server.value.Param;
import org.apache.commons.fileupload.FileItem;

import java.io.IOException;

/**
 * @author absir
 */
public class Asset_upload extends AssetServer {

    /**
     * @param input
     */
    @Before
    public void onAuthentication(@Nullable @Param String name, Input input) {
        SecurityService.ME.autoLogin(name, true, 0, input);
    }

    /**
     * @param file
     * @param input
     * @throws IOException
     */
    @Body
    @MaPermission(RichCrudFactory.UPLOAD)
    public String upload(@Param FileItem file, Input input) throws IOException {
        return UploadCrudFactory.ME.uploadExtension(HelperFileName.getExtension(file.getName()), file.getInputStream(), SecurityService.ME.getUserBase(input));
    }

    /**
     * @param files
     * @throws IOException
     */
    @Body
    @MaPermission(RichCrudFactory.UPLOAD)
    public String[] upload(@Param FileItem[] files, Input input) throws IOException {
        JiUserBase user = SecurityService.ME.getUserBase(input);
        int length = files.length;
        String[] uploadPaths = new String[length];
        for (int i = 0; i < length; i++) {
            FileItem file = files[i];
            uploadPaths[i] = UploadCrudFactory.ME.uploadExtension(HelperFileName.getExtension(file.getName()), file.getInputStream(), user);
        }

        return uploadPaths;
    }
}
