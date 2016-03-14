/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-25 上午10:32:58
 */
package com.absir.aserv.resource;

import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.*;
import com.absir.core.helper.HelperFileName;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author absir
 */
@Base
@Bean
public class ResourceScannerDefault extends ResourceScanner {

    /**
     * ME
     */
    public static final ResourceScannerDefault ME = BeanFactoryUtils.get(ResourceScannerDefault.class);

    /**
     * scanPath
     */
    @Value("resource.scan.path")
    private String scanPath = "resources";

    /**
     * resourceProcessors
     */
    @Orders
    @Inject
    private List<ResourceProcessor> resourceProcessors;

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.resource.ResourceScanner#getScanPath()
     */
    @Override
    public String getScanPath() {
        return scanPath;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.resource.ResourceScanner#getResourceProcessors()
     */
    @Override
    public List<ResourceProcessor> getResourceProcessors() {
        return resourceProcessors;
    }

    /**
     * 开始扫瞄
     */
    @Transaction(rollback = Throwable.class)
    @Started
    public void startedProcess() {
        if (resourceProcessors != null) {
            scanPath = HelperFileName.normalizeNoEndSeparator(BeanFactoryUtils.getBeanConfig().getResourcePath() + scanPath);
            List<ResourceProcessor> processors = new ArrayList<ResourceProcessor>();
            for (ResourceProcessor resourceProcessor : resourceProcessors) {
                if (resourceProcessor.supports(this)) {
                    processors.add(resourceProcessor);
                }
            }

            if (processors.size() == 0) {
                resourceProcessors = null;

            } else {
                resourceProcessors = processors;
                startScanner();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.resource.ResourceScanner#startScanner()
     */
    @Override
    public void doScanner(File scanFile) {
        Session session = BeanDao.getSession();
        // 设置所有资源未扫瞄状态
        QueryDaoUtils.updateQuery(session, "JResource", new Object[]{"o.scanned", false}, new Object[]{"o.scanned", true});
        QueryDaoUtils.updateQuery(session, "JDirectory", new Object[]{"o.scanned", false}, new Object[]{"o.scanned", true});
        // 开始扫瞄
        super.doScanner(scanFile);
        // 删除所有未扫瞄资源
        QueryDaoUtils.deleteQuery(session, "JDirectory", new Object[]{"o.scanned", false});
        QueryDaoUtils.deleteQuery(session, "JResource", new Object[]{"o.scanned", false});
    }
}
