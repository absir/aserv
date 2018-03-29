/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.developer;

import com.absir.aserv.developer.model.EntityModel;
import com.absir.aserv.developer.model.ModelFactory;
import com.absir.aserv.init.InitBeanFactory;
import com.absir.aserv.support.Developer;
import com.absir.aserv.support.developer.*;
import com.absir.aserv.support.developer.IDeveloper.IDeploy;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.*;
import com.absir.core.base.Environment;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperFileName;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAbsir;
import com.absir.core.util.UtilContext;
import com.absir.orm.value.JoEntity;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Base
@Bean
public class DeveloperService implements IDeveloper, IDeploy {

    public static final Logger LOGGER = LoggerFactory.getLogger(DeveloperService.class);

    @Value("developer.web")
    private static String developerWeb;

    @Value("developer.new.type")
    protected int developerNewType = 2;

    @Value("developer.new.time")
    protected int developerNewTime;

    @Value("developer.diy")
    protected boolean developerDiy;

    @Inject(type = InjectType.Selectable)
    private ISecurity security;

    /**
     * 初始化开发环境
     */
    @InjectOrder(-1)
    @Started
    protected static void postConstruct() {
        if (InitBeanFactory.ME.isDevelopOrVersionChange()) {
            String deployPath = HelperFileName.normalizeNoEndSeparator(BeanFactoryUtils.getBeanConfig().getClassPath() + "/../../");
            LOGGER.info("deploy : " + deployPath);
            File file = new File(BeanFactoryUtils.getBeanConfig().getClassPath() + "deploy");
            if (file.exists() && file.isDirectory()) {
                for (File depFile : file.listFiles()) {
                    if (depFile.isFile() && depFile.getName().endsWith(".zip")) {
                        try {
                            HelperFile.copyDirectoryOverWrite(new ZipInputStream(new FileInputStream(depFile)), new File(deployPath), false, null, true);

                        } catch (Exception e) {
                            LOGGER.error("deployDir " + depFile, e);
                        }
                    }
                }
            }

            for (IDeploy deploy : BeanFactoryUtils.getOrderBeanObjects(IDeploy.class)) {
                try {
                    HelperFile.copyDirectoryOverWrite(KernelClass.getCodeSource(deploy.getClass(), "/deployResource"), new File(deployPath), false, null, true);

                } catch (Throwable e) {
                    LOGGER.error("deployResource " + deploy, e);
                }
            }

            if (Environment.isDevelop() && !KernelString.isEmpty(developerWeb)) {
                String developPath = HelperFileName.normalizeNoEndSeparator(developerWeb + "/../../../");
                if (!KernelString.isEmpty(deployPath) && (HelperFileName.getName(developPath)).equals(HelperFileName.getName(deployPath))) {
                    final String resourcesPath = developPath + "/src/main/resources/";
                    if (HelperFile.directoryExists(resourcesPath)) {
                        // 复制开发文件到开发环境
                        if (IDeveloper.ME != null) {
                            try {
                                // HelperFile.copyDirectoryOverWrite(IDeveloper.ME.getClass().getResource("/deploy"),
                                // new File(developerWeb), false, null, true);

                            } catch (Throwable e) {
                                LOGGER.error("developPath " + developPath, e);
                            }
                        }

                        // 复制缓存文件到开发环境
                        Developer.addListener(new KernelLang.CallbackTemplate<Map.Entry<String, File>>() {

                            @Override
                            public void doWith(Map.Entry<String, File> template) {
                                try {
                                    HelperFile.copyFile(template.getValue(), new File(resourcesPath + template.getKey()));

                                } catch (IOException e) {
                                    LOGGER.error("deployListener " + template, e);
                                }
                            }
                        });

                        return;
                    }
                }

                developerWeb = null;
            }
        }
    }

    public static String getDeveloperWeb() {
        return developerWeb;
    }

    public int getDeveloperNewType() {
        return developerNewType;
    }

    public int getDeveloperNewTime() {
        return developerNewTime;
    }

    public void setDeveloperNewTime(int developerNewTime) {
        this.developerNewTime = developerNewTime;
    }

    public boolean isDeveloperDiy() {
        return developerDiy;
    }

    @Inject
    protected void init() {
        if (developerNewType == 0) {
            developerNewTime = 0;

        } else if (developerNewType == 1) {
            developerNewTime = UtilAbsir.shortTime(HelperFile.lastModified(new File(BeanFactoryUtils.getBeanConfig().getClassPath())));

        } else {
            developerNewTime = UtilAbsir.shortTime(UtilContext.getCurrentTime());
        }
    }

    @Override
    public IModel getModelEntity(JoEntity joEntity) {
        return ModelFactory.getModelEntity(joEntity);
    }

    @Override
    public List<JCrudField> getCrudFields(JoEntity joEntity) {
        EntityModel entityModel = ModelFactory.getModelEntity(joEntity);
        if (entityModel == null) {
            return null;
        }

        List<JCrudField> crudFields = new ArrayList<JCrudField>();
        for (IField field : entityModel.getCrudFields()) {
            if (field.getCrudField().getjCrud() != null || field.getCrudField().getCruds() != null) {
                crudFields.add(field.getCrudField());
            }
        }

        if (entityModel.getjCruds() != null) {
            for (JCrud jCrud : entityModel.getjCruds()) {
                JCrudField crudField = new JCrudField();
                crudField.setjCrud(jCrud);
                crudFields.add(crudField);
            }
        }

        return crudFields;
    }

    @Override
    public String[] getGroupFields(JoEntity joEntity, String group) {
        EntityModel entityModel = ModelFactory.getModelEntity(joEntity);
        if (entityModel == null) {
            return null;
        }

        List<IField> fields = entityModel.getGroupFields(group);
        if (fields == null) {
            return null;
        }

        List<String> crudFields = new ArrayList<String>();
        for (IField field : entityModel.getGroupFields(group)) {
            crudFields.add(field.getName());
        }

        return KernelCollection.toArray(crudFields, String.class);
    }

    public boolean isDeveloper(Object render) {
        if (security != null) {
            JiUserBase user = security.loginRender(render);
            return user != null && user.isDeveloper();
        }

        return false;
    }

    @Override
    public int diy(Object render) {
        if (developerDiy && render != null && render instanceof ServletRequest) {
            ServletRequest request = (ServletRequest) render;
            if (BeanFactoryUtils.getEnvironment() == Environment.DEVELOP || isDeveloper(render)) {
                String parameter = request.getParameter("diy");
                if (parameter != null) {
                    boolean diy = DynaBinder.to(parameter, boolean.class);
                    DeveloperUtils.diy(request, diy);
                    return !diy ? 1 : 2;
                }
            }
        }

        return 0;
    }

    @Override
    public String getGeneratePath(String generatePath) {
        return DeveloperUtils.getGeneratePath(generatePath);
    }

    @Override
    public String getDeveloperPath(String includePath) {
        return DeveloperUtils.getDeveloperPath(includePath);
    }

    @Override
    public void copyDeveloper(File file, String filePath) throws IOException {
        if (developerWeb != null) {
            FileUtils.copyFile(file, new File(developerWeb + filePath));
        }
    }

    @Override
    public void generate(String filePath, String includePath, Object... renders) throws IOException {
        DeveloperUtils.generateRenders(filePath, includePath, renders);
    }
}
