/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-25 上午10:40:08
 */
package com.absir.aserv.resource;

import com.absir.aserv.system.bean.JDirectory;
import com.absir.aserv.system.bean.JResource;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.client.helper.HelperEncrypt;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelObject;
import org.hibernate.Session;

import java.io.File;
import java.io.FileInputStream;

@Base
@Bean
public class ResourceProcessorDefault implements ResourceProcessor {

    @Override
    public boolean supports(ResourceScanner scanner) {
        return scanner instanceof ResourceScannerDefault;
    }

    @Override
    public boolean supportsDirectory(File directoryFile, ResourceScanner scanner) {
        Session session = BeanDao.getSession();
        if (directoryFile.getName().indexOf('.') >= 0) {
            return false;
        }

        String directoryId = directoryFile.getPath().substring(scanner.getScanPath().length()) + "/";
        JDirectory directory = BeanDao.get(session, JDirectory.class, directoryId);
        if (directory == null) {
            directory = new JDirectory();
            directory.setId(directoryId);
        }

        boolean modified = true;
        // 目录已经扫瞄
        directory.setScanned(true);
        directory.setUpdateTime(directoryFile.lastModified());
        session.merge(directory);
        session.flush();
        if (!modified) {
            // (未更改)跳过目录文件扫瞄
            QueryDaoUtils.createQueryArray(session, "UPDATE JResource o SET o.scanned = TRUE WHERE o.id LIKE ?", directoryId + '%').executeUpdate();
        }

        return modified;
    }

    @Override
    public void doProcessor(File resourceFile, ResourceScanner scanner) {
        Session session = BeanDao.getSession();
        String resourceId = resourceFile.getPath().substring(scanner.getScanPath().length());
        JResource resource = BeanDao.get(session, JResource.class, resourceId);
        if (resource == null) {
            resource = new JResource();
            resource.setId(resourceId);
        }

        // 文件已经扫瞄
        resource.setScanned(true);
        long fileSize = resourceFile.length();
        String md5 = null;
        FileInputStream inputStream = null;
        try {
            md5 = HelperEncrypt.encryptionMD5(new FileInputStream(resourceFile));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fileSize != resource.getFileSize() || (md5 != null && !KernelObject.equals(md5, resource.getFileMd5()))) {
            resource.setUpdateTime(ContextUtils.getContextTime());
            resource.setFileSize(fileSize);
            resource.setFileMd5(md5);
        }

        session.merge(resource);
        session.flush();
    }
}
