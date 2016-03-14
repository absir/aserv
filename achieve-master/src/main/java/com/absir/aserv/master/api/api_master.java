/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月7日 下午4:28:12
 */
package com.absir.aserv.master.api;

import com.absir.aserv.master.bean.JSlaveUpgrading;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.upgrade.UpgradeService;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.context.core.ContextUtils;
import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperFileName;
import com.absir.core.helper.HelperIO;
import com.absir.master.InputMaster;
import com.absir.server.value.Body;
import com.absir.server.value.Server;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author absir
 *
 */
@Base
@Server
public class api_master extends ApiMaster {

    @JaLang("时间")
    public long time() {
        return ContextUtils.getContextTime();
    }

    @JaLang("获取升级文件")
    public void upgrade(String filepath, HttpServletResponse response) throws IOException {
        File file = new File(UpgradeService.ME.getUpgradeResource() + filepath);
        if (file.exists() && file.isFile()) {
            response.addHeader("Content-Disposition", "attachment;filename=" + HelperFileName.getName(filepath));
            HelperIO.copy(HelperFile.openInputStream(file), response.getOutputStream());
        }
    }

    @JaLang("获取资源文件")
    public void resource(String filepath, HttpServletResponse response) throws IOException {
        File file = new File(BeanFactoryUtils.getBeanConfig().getResourcePath() + filepath);
        if (file.exists() && file.isFile()) {
            response.addHeader("Content-Disposition", "attachment;filename=" + HelperFileName.getName(filepath));
            HelperIO.copy(HelperFile.openInputStream(file), response.getOutputStream());
        }
    }

    @JaLang("获取上传文件")
    public void download(String filepath, HttpServletResponse response) throws IOException {
        File file = new File(UploadCrudFactory.getUploadPath() + filepath);
        if (file.exists() && file.isFile()) {
            response.addHeader("Content-Disposition", "attachment;filename=" + HelperFileName.getName(filepath));
            HelperIO.copy(HelperFile.openInputStream(file), response.getOutputStream());
        }
    }

    @JaLang("升级状态")
    public void upgradeStatus(@Body JSlaveUpgrading slaveUpgrading, InputMaster inputMaster) throws IOException {
        slaveUpgrading.setId((String) inputMaster.getId());
        BeanService.ME.merge(slaveUpgrading);
    }
}
